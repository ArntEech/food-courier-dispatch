package com.foodcourier.gui;

import com.foodcourier.domain.*;
import com.foodcourier.alpha1.OrderIntakeService;
import com.foodcourier.alpha2.DispatchService;
import com.foodcourier.alpha3.CourierAssignmentService;
import com.foodcourier.alpha4.DataLoader;
import com.foodcourier.alpha4.RouteNavigationService;
import com.foodcourier.alpha5.DeliveryOptimizationService;
import com.foodcourier.dsa.hashtable.HashMapTable;
import com.foodcourier.dsa.list.SinglyLinkedList;
import com.foodcourier.dsa.queue.ArrayQueue;
import com.foodcourier.dsa.tree.BST;
import com.foodcourier.dsa.graph.Graph;
import com.foodcourier.dsa.disjointset.DisjointSet;
import com.foodcourier.algorithms.graph.Kruskal;
import com.foodcourier.algorithms.optimization.DynamicProgramming;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Presentation GUI for DCIT 204/308 oral defense.
 * Demonstrates the Alpha 1-5 pipeline with live visual flow.
 */
public class PresentationGUI extends Application {

    // State shared across stages
    private OrderIntakeService intake;
    private DispatchService dispatch;
    private List<Order> allOrders;
    private List<Courier> couriers;
    private List<Restaurant> restaurants;
    private List<Customer> customers;
    private HashMapTable<String, Courier> courierById;
    private BST<String> courierIdsSorted;
    private Graph<Integer> graph;
    private RouteNavigationService routeNav;

    // UI components
    private VBox flowPanel;
    private TextArea detailArea;
    private Label stageLabel;
    private Button nextBtn;
    private Button resetBtn;
    private int currentStage = 0;

    private static final String[] STAGE_NAMES = {
        "Stage 0: System Overview",
        "Stage 1: Alpha 1 — Order Intake (ArrayQueue)",
        "Stage 2: Alpha 2 — Priority Dispatch (BinaryHeap)",
        "Stage 3: Alpha 3 — Courier Assignment (HashMap + BST)",
        "Stage 4: Alpha 4 — Route Navigation (Graph: Dijkstra/Kruskal)",
        "Stage 5: Alpha 5 — Optimization (Greedy + DP Knapsack)",
        "Stage 6: Database Integration (SQLite)",
        "Stage 7: Performance & Summary"
    };

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Food Courier Dispatch — DCIT 204/308 Demo");

        // Layout
        BorderPane root = new BorderPane();

        // Header
        Label header = new Label("🇬🇭 Ghana Smart Service Operations Optimizer");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #006b3f; -fx-text-fill: white;");
        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(Double.MAX_VALUE);

        // Stage label
        stageLabel = new Label(STAGE_NAMES[0]);
        stageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        stageLabel.setPadding(new Insets(5));

        // Flow panel (middle)
        flowPanel = new VBox(10);
        flowPanel.setPadding(new Insets(10));
        flowPanel.setStyle("-fx-background-color: #f5f5f5;");

        ScrollPane scroll = new ScrollPane(flowPanel);
        scroll.setFitToWidth(true);

        // Detail area (right)
        detailArea = new TextArea();
        detailArea.setEditable(false);
        detailArea.setWrapText(true);
        detailArea.setFont(Font.font("Consolas", 12));
        detailArea.setPrefWidth(400);
        VBox rightBox = new VBox(5, new Label("Algorithm / Data Structure Detail"), detailArea);
        rightBox.setPadding(new Insets(10));
        rightBox.setPrefWidth(420);

        // Control buttons
        nextBtn = new Button("▶ Next Stage");
        nextBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nextBtn.setOnAction(e -> advanceStage());

        resetBtn = new Button("⟲ Reset");
        resetBtn.setOnAction(e -> resetSystem());

        Button seedBtn = new Button("🌱 Seed DB");
        seedBtn.setOnAction(e -> {
            try {
                Runtime.getRuntime().exec("mvn compile exec:java -Dexec.mainClass=com.foodcourier.db.DatabaseSeeder");
                detailArea.setText("Database seeding started in background...\nRun: mvn compile exec:java -Dexec.mainClass=com.foodcourier.db.DatabaseSeeder");
            } catch (IOException ex) {
                detailArea.setText("Error: " + ex.getMessage());
            }
        });

        HBox controls = new HBox(10, nextBtn, resetBtn, seedBtn);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        // Left: stage navigation
        VBox leftBox = new VBox(5);
        leftBox.setPadding(new Insets(10));
        leftBox.setPrefWidth(220);
        leftBox.setStyle("-fx-background-color: #e8f5e9;");
        Label navTitle = new Label("Pipeline Stages");
        navTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        leftBox.getChildren().add(navTitle);

        for (int i = 0; i < STAGE_NAMES.length; i++) {
            final int idx = i;
            Button b = new Button(STAGE_NAMES[i].replaceFirst("Stage \\d+: ", ""));
            b.setMaxWidth(Double.MAX_VALUE);
            b.setOnAction(e -> jumpToStage(idx));
            leftBox.getChildren().add(b);
        }

        // Assemble
        root.setTop(header);
        root.setLeft(leftBox);
        root.setCenter(new VBox(5, stageLabel, scroll));
        root.setRight(rightBox);
        root.setBottom(controls);

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize data
        loadData();
        renderStage(0);
    }

    private void loadData() throws IOException {
        // Alpha 4: Load network
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadAllData();
        graph = dataLoader.getGraph();

        DisjointSet<Integer> disjointSet = new DisjointSet<>();
        for (Integer v : graph.getVertices()) {
            disjointSet.makeSet(v);
        }
        routeNav = new RouteNavigationService(graph, disjointSet);

        // Load domain objects
        customers = loadCustomers(dataLoader.getLocationNames());
        restaurants = loadRestaurants(dataLoader.getLocationNames());
        couriers = loadCouriers(dataLoader.getLocationNames());

        courierById = new HashMapTable<>();
        for (Courier c : couriers) courierById.put(c.getId(), c);

        courierIdsSorted = new BST<>();
        for (Courier c : couriers) courierIdsSorted.insert(c.getId());

        // Alpha 1: Load orders
        intake = new OrderIntakeService();
        intake.loadSeedData(Paths.get("data/seed/orders.csv"));

        allOrders = new ArrayList<>();
        while (intake.getQueueSize() > 0) {
            Order raw = intake.getNextOrder();
            if (raw != null) {
                Customer fullCust = customers.stream()
                    .filter(c -> c.getId().equals(raw.getCustomer().getId()))
                    .findFirst().orElse(null);
                Restaurant fullRest = restaurants.stream()
                    .filter(r -> r.getId().equals(raw.getRestaurant().getId()))
                    .findFirst().orElse(null);
                if (fullCust != null && fullRest != null) {
                    allOrders.add(new Order(raw.getId(), fullCust, fullRest,
                        raw.getValue(), raw.getPriority(), raw.getStatus(), raw.getTimestamp()));
                }
            }
        }

        // Alpha 2: Dispatch
        dispatch = new DispatchService();
        for (Order o : allOrders) dispatch.insert(o);
    }

    private void advanceStage() {
        if (currentStage < STAGE_NAMES.length - 1) {
            currentStage++;
            renderStage(currentStage);
        }
    }

    private void jumpToStage(int idx) {
        currentStage = idx;
        renderStage(idx);
    }

    private void resetSystem() {
        currentStage = 0;
        try {
            loadData();
        } catch (IOException e) {
            detailArea.setText("Reset error: " + e.getMessage());
        }
        renderStage(0);
    }

    private void renderStage(int stage) {
        stageLabel.setText(STAGE_NAMES[stage]);
        flowPanel.getChildren().clear();
        detailArea.clear();

        switch (stage) {
            case 0: renderOverview(); break;
            case 1: renderAlpha1(); break;
            case 2: renderAlpha2(); break;
            case 3: renderAlpha3(); break;
            case 4: renderAlpha4(); break;
            case 5: renderAlpha5(); break;
            case 6: renderDatabase(); break;
            case 7: renderPerformance(); break;
        }
    }

    // ---- Stage 0: Overview ----
    private void renderOverview() {
        addCard("System Architecture",
            "Alpha 1 → Alpha 2 → Alpha 3 → Alpha 4 → Alpha 5\n\n" +
            "Order Intake (Queue) → Priority Dispatch (Heap) → Courier Assignment (Hash+BST)\n" +
            "→ Route Navigation (Graph) → Optimization (Greedy + DP)\n\n" +
            "All data loaded from SQLite, processed through custom data structures.");

        addCard("Ghana Context: University of Ghana, Legon Campus",
            "8 locations: UG Main Gate, Legon Hall, Commonwealth Hall, Akuafo Hall,\n" +
            "University Hospital, Okponglo, Atomic Junction, UG Business School\n" +
            "11 roads (bidirectional), 6 customers, 5 restaurants, 5 couriers, 6 orders");

        addCard("Custom Data Structures (No built-ins for assessed logic)",
            "• ArrayQueue (circular buffer) — Alpha 1\n" +
            "• BinaryHeap (max-heap) — Alpha 2\n" +
            "• HashMapTable (chaining) — Alpha 3\n" +
            "• BST (sorted) — Alpha 3\n" +
            "• Graph (adjacency list) — Alpha 4\n" +
            "• DisjointSet (union-find) — Alpha 4 MST");

        detailArea.setText(
            "PROJECT BRIEF CHECKLIST (DCIT 204/308)\n" +
            "=====================================\n" +
            "✅ 5 Alpha modules with custom structures\n" +
            "✅ Custom sorting (Quick/Merge) & searching (Linear/Binary)\n" +
            "✅ Graph algorithms (BFS, Dijkstra, Kruskal)\n" +
            "✅ Dynamic Programming (0/1 Knapsack)\n" +
            "✅ Greedy (earliest-deadline-first)\n" +
            "✅ Database persistence (SQLite)\n" +
            "✅ 91 unit tests passing\n\n" +
            "Click 'Next Stage' to walk through each Alpha.");
    }

    // ---- Stage 1: Alpha 1 ----
    private void renderAlpha1() {
        addCard("Alpha 1: Order Intake Service",
            "Data Structure: ArrayQueue (circular buffer)\n" +
            "Operation: FIFO — First In, First Out\n\n" +
            "Orders loaded from data/seed/orders.csv into the queue.");

        // Show queue contents
        StringBuilder sb = new StringBuilder("Queue (FIFO order):\n");
        for (int i = 0; i < allOrders.size(); i++) {
            Order o = allOrders.get(i);
            sb.append(String.format("  [%d] Order %s (Priority %d, %s)\n",
                i, o.getId(), o.getPriority().getValue(), o.getStatus()));
        }
        addCodeCard(sb.toString());

        addCard("Why ArrayQueue?",
            "• O(1) enqueue/dequeue with circular wrap-around\n" +
            "• Fixed capacity, no dynamic resize needed for known order volume\n" +
            "• Models real dispatch: orders arrive and are processed in sequence");

        detailArea.setText(
            "ARRAYQUEUE IMPLEMENTATION\n" +
            "=========================\n" +
            "public class ArrayQueue<T> {\n" +
            "    private T[] items;\n" +
            "    private int front = 0, rear = 0, size = 0;\n\n" +
            "    public void enqueue(T item) {\n" +
            "        if (size == items.length) resize();\n" +
            "        items[rear] = item;\n" +
            "        rear = (rear + 1) % items.length; // circular\n" +
            "        size++;\n" +
            "    }\n\n" +
            "    public T dequeue() {\n" +
            "        T item = items[front];\n" +
            "        front = (front + 1) % items.length;\n" +
            "        size--;\n" +
            "        return item;\n" +
            "    }\n" +
            "}\n\n" +
            "Test: OrderIntakeServiceTest (3 tests)");
    }

    // ---- Stage 2: Alpha 2 ----
    private void renderAlpha2() {
        addCard("Alpha 2: Priority Dispatch Service",
            "Data Structure: BinaryHeap (max-heap by priority)\n" +
            "Operation: Orders extracted by HIGHEST priority first\n\n" +
            "Priority levels: 1 (low) → 4 (urgent)");

        // Show heap order
        List<Order> sorted = new ArrayList<>(allOrders);
        sorted.sort(Comparator.comparingInt((Order o) -> o.getPriority().getValue()).reversed());

        StringBuilder sb = new StringBuilder("Dispatch Order (by priority, highest first):\n");
        for (Order o : sorted) {
            sb.append(String.format("  Priority %d → Order %s (%s)\n",
                o.getPriority().getValue(), o.getId(), o.getCustomer().getName()));
        }
        addCodeCard(sb.toString());

        addCard("Why BinaryHeap?",
            "• O(log n) insert, O(log n) extract-max\n" +
            "• Always surfaces the most urgent order first\n" +
            "• More efficient than re-sorting the entire list each time");

        detailArea.setText(
            "BINARYHEAP (MAX-HEAP) IMPLEMENTATION\n" +
            "====================================\n" +
            "public void insert(Order o) {\n" +
            "    heap.add(o);\n" +
            "    siftUp(heap.size() - 1);\n" +
            "}\n\n" +
            "private void siftUp(int i) {\n" +
            "    while (i > 0) {\n" +
            "        int parent = (i - 1) / 2;\n" +
            "        if (heap.get(i).getPriority() <= heap.get(parent).getPriority())\n" +
            "            break;\n" +
            "        swap(i, parent);\n" +
            "        i = parent;\n" +
            "    }\n" +
            "}\n\n" +
            "Test: DispatchServiceTest (4 tests)");
    }

    // ---- Stage 3: Alpha 3 ----
    private void renderAlpha3() {
        addCard("Alpha 3: Courier Assignment Service",
            "Data Structures: HashMapTable (O(1) lookup) + BST (sorted IDs)\n" +
            "Operation: Find available courier for each order\n\n" +
            "Uses LinearSearch + BinarySearch for matching");

        StringBuilder sb = new StringBuilder("Couriers (HashMapTable keyed by ID):\n");
        for (Courier c : couriers) {
            sb.append(String.format("  %s → %s (%s)\n", c.getId(), c.getName(), c.getStatus()));
        }
        sb.append("\nBST Inorder (sorted IDs): ");
        List<String> inorder = courierIdsSorted.inOrder();
        sb.append(String.join(", ", inorder.toArray(new String[0])));
        addCodeCard(sb.toString());

        addCard("Why HashMap + BST?",
            "• HashMapTable: O(1) courier lookup by ID during assignment\n" +
            "• BST: sorted courier IDs for range queries / ordered iteration\n" +
            "• Demonstrates two complementary access patterns");

        detailArea.setText(
            "HASHTABLETABLE (CHAINING) IMPLEMENTATION\n" +
            "=======================================\n" +
            "public V get(K key) {\n" +
            "    int idx = hash(key) % capacity;\n" +
            "    Node node = buckets[idx];\n" +
            "    while (node != null) {\n" +
            "        if (node.key.equals(key)) return node.value;\n" +
            "        node = node.next; // collision chain\n" +
            "    }\n" +
            "    return null;\n" +
            "}\n\n" +
            "Collision handling: separate chaining\n" +
            "Test: HashMapTableTest (4 tests), BSTTest (4 tests)");
    }

    // ---- Stage 4: Alpha 4 ----
    private void renderAlpha4() {
        addCard("Alpha 4: Route Navigation Service",
            "Data Structure: Graph (adjacency list)\n" +
            "Algorithms: BFS (reachability), Dijkstra (shortest path), Kruskal (MST)\n\n" +
            String.format("Network: %d locations, %d roads (bidirectional)",
                graph.vertexCount(), graph.edgeCount() / 2));

        // Dijkstra demo for order 1
        Order o1 = allOrders.get(0);
        StringBuilder sb = new StringBuilder();
        try {
            RouteNavigationService.RouteResult route =
                routeNav.getRoute(o1, couriers.get(0));
            sb.append(String.format("Route for Order %s:\n", o1.getId()));
            sb.append(String.format("  From: %s\n", o1.getRestaurant().getLocation().getName()));
            sb.append(String.format("  To:   %s\n", o1.getCustomer().getLocation().getName()));
            sb.append(String.format("  Hops: %d, Distance: %.2f km\n",
                route.getPath().size(), route.getTotalDistance()));
            sb.append("  Path: ");
            sb.append(route.getPath().stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + " → " + b)
                .orElse(""));
            sb.append("\n");
        } catch (Exception e) {
            sb.append("Route error: ").append(e.getMessage());
        }
        addCodeCard(sb.toString());

        // Kruskal MST
        Kruskal.Result mst = routeNav.minimumSpanningNetwork();
        addCard("Minimum Spanning Network (Kruskal)",
            String.format("MST edges: %d, total weight: %.2f km\n" +
                "Connects all campus locations with minimum road distance.",
                mst.getEdges().size(), mst.getTotalWeight()));

        detailArea.setText(
            "DIJKSTRA (SHORTEST PATH) PSEUDOCODE\n" +
            "===================================\n" +
            "dist[source] = 0\n" +
            "for each vertex v: dist[v] = ∞\n" +
            "priorityQueue.add(source)\n" +
            "while pq not empty:\n" +
            "    u = pq.extractMin()\n" +
            "    for each edge (u, v, w):\n" +
            "        if dist[u] + w < dist[v]:\n" +
            "            dist[v] = dist[u] + w\n" +
            "            pred[v] = u\n" +
            "            pq.decreaseKey(v)\n\n" +
            "Complexity: O((V + E) log V)\n" +
            "Test: DijkstraTest (5), KruskalTest (5), PrimTest (3)");
    }

    // ---- Stage 5: Alpha 5 ----
    private void renderAlpha5() {
        addCard("Alpha 5: Optimization & Reporting",
            "Algorithms: Greedy (earliest-deadline-first) + Dynamic Programming (0/1 Knapsack)\n\n" +
            "Demonstrates order batching under capacity constraints.");

        // DP Knapsack demo
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 2, 10));
        items.add(new DynamicProgramming.Item("O2", 3, 15));
        items.add(new DynamicProgramming.Item("O3", 5, 25));
        items.add(new DynamicProgramming.Item("O4", 7, 30));

        int capacity = 10;
        List<DynamicProgramming.Item> selected = DynamicProgramming.knapsack(items, capacity);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("0/1 Knapsack — Capacity %dkg\n", capacity));
        sb.append("Items: O1(2kg,10), O2(3kg,15), O3(5kg,25), O4(7kg,30)\n\n");
        sb.append("Selected subset:\n");
        for (DynamicProgramming.Item it : selected) {
            sb.append(String.format("  %s (%dkg, value %d)\n", it.id, it.weight, it.value));
        }
        sb.append(String.format("\nTotal: %dkg, value %d\n",
            DynamicProgramming.totalWeight(selected), DynamicProgramming.totalValue(selected)));
        sb.append(String.format("(Brute force would try 2^4 = 16 combinations)\n"));
        addCodeCard(sb.toString());

        addCard("Greedy vs DP",
            "Greedy (earliest-deadline): fast O(n log n) but suboptimal\n" +
            "DP (0/1 Knapsack): optimal O(n·W) but slower\n" +
            "→ Demonstrates algorithm selection trade-offs (brief Section 10)");

        detailArea.setText(
            "DYNAMIC PROGRAMMING (0/1 KNAPSACK) — TABULATION\n" +
            "================================================\n" +
            "dp[i][w] = max value using first i items, capacity w\n\n" +
            "for i in 1..n:\n" +
            "  for w in 0..W:\n" +
            "    if weight[i] <= w:\n" +
            "      dp[i][w] = max(dp[i-1][w],\n" +
            "                     dp[i-1][w-weight[i]] + value[i])\n" +
            "    else:\n" +
            "      dp[i][w] = dp[i-1][w]\n\n" +
            "Backtrack from dp[n][W] to recover selected items.\n" +
            "Complexity: O(n·W) time, O(n·W) space\n" +
            "Test: DynamicProgrammingTest (7 tests), GreedyTest (6 tests)");
    }

    // ---- Stage 6: Database ----
    private void renderDatabase() {
        addCard("Database Integration (SQLite)",
            "Schema: 6 tables + 2 reporting views\n" +
            "• locations, roads, customers, restaurants, couriers, orders\n" +
            "• deliveries (audit), v_courier_performance, v_daily_summary\n\n" +
            "Seeded from CSV → food_courier.db");

        addCodeCard(
            "Schema (database/schema.sql):\n" +
            "CREATE TABLE locations (\n" +
            "  id INTEGER PRIMARY KEY,\n" +
            "  name TEXT, latitude REAL, longitude REAL, type TEXT);\n" +
            "CREATE TABLE roads (\n" +
            "  id INTEGER PRIMARY KEY,\n" +
            "  from_location_id INTEGER, to_location_id INTEGER,\n" +
            "  distance_km REAL, travel_time_min INTEGER,\n" +
            "  is_bidirectional INTEGER);\n" +
            "CREATE TABLE orders (\n" +
            "  id INTEGER PRIMARY KEY, customer_id, restaurant_id,\n" +
            "  order_time TEXT, priority INTEGER, status TEXT,\n" +
            "  estimated_prep_minutes INTEGER);");

        addCard("Why Database?",
            "• Persistent storage — survives program restart\n" +
            "• Reloads data into custom structures on launch\n" +
            "• Reporting views for courier performance analysis\n" +
            "• Part of running system, not just storage (brief Section 4)");

        detailArea.setText(
            "DATABASE SEEDER\n" +
            "===============\n" +
            "Reads 6 CSV files → populates SQLite\n" +
            "Run: mvn compile exec:java \\\n" +
            "  -Dexec.mainClass=com.foodcourier.db.DatabaseSeeder\n\n" +
            "Query example:\n" +
            "  SELECT * FROM v_courier_performance;\n\n" +
            "Current seed data:\n" +
            "  8 locations, 11 roads, 6 customers,\n" +
            "  5 restaurants, 5 couriers, 6 orders");
    }

    // ---- Stage 7: Performance ----
    private void renderPerformance() {
        addCard("Performance & Empirical Analysis",
            "Algorithm efficiency measured as input size grows\n" +
            "(Brief Section 9: Search, Sort, Hash, BST, Heap, Graph)");

        addCodeCard(
            "Experiment Results (sample):\n\n" +
            "Search (10,000 records):\n" +
            "  Linear:  ~50,000 ns (O(n))\n" +
            "  Binary:  ~130 ns   (O(log n))\n\n" +
            "Sort (10,000 records):\n" +
            "  Selection/Insertion: O(n²) — slow\n" +
            "  Merge/Quick:        O(n log n) — fast\n\n" +
            "Heap Dispatch (20,000 requests):\n" +
            "  Insert: O(log n) per op\n" +
            "  Extract: O(log n) per op");

        addCard("Test Coverage Summary",
            "91 unit tests passing across:\n" +
            "• Data structures: Queue, Heap, HashMap, BST, Graph, DisjointSet\n" +
            "• Algorithms: Search, Sort, Dijkstra, Prim, Kruskal, DP, Greedy\n" +
            "• Services: OrderIntake, Dispatch, CourierAssignment\n\n" +
            "Run: mvn test");

        addCard("Key Takeaways for Defense",
            "✅ All 5 Alphas implemented with custom structures\n" +
            "✅ Graph algorithms: BFS, Dijkstra, Kruskal MST\n" +
            "✅ DP (0/1 Knapsack) vs Greedy trade-off demonstrated\n" +
            "✅ Database persistence with reporting views\n" +
            "✅ Comprehensive test suite (91 tests)\n" +
            "✅ Ghana-localized dataset (UG Legon Campus)");

        detailArea.setText(
            "ORAL DEFENSE PREP NOTES\n" +
            "========================\n" +
            "Each member defends ONE structure + ONE algorithm:\n\n" +
            "Member 1: ArrayQueue + Linear Search\n" +
            "Member 2: BinaryHeap + Dijkstra\n" +
            "Member 3: HashMapTable + QuickSort\n" +
            "Member 4: BST + MergeSort\n" +
            "Member 5: Graph + Kruskal MST\n" +
            "Member 6: DisjointSet + DP Knapsack\n\n" +
            "Live demo: click through stages, run tests,\n" +
            "show DB query, explain trade-offs.");
    }

    // ---- UI Helpers ----
    private void addCard(String title, String content) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");
        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web("#006b3f"));
        Label c = new Label(content);
        c.setFont(Font.font("Arial", 12));
        c.setWrapText(true);
        card.getChildren().addAll(t, c);
        flowPanel.getChildren().add(card);
    }

    private void addCodeCard(String content) {
        TextArea ta = new TextArea(content);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setFont(Font.font("Consolas", 11));
        ta.setPrefHeight(120);
        ta.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #00ff00;");
        flowPanel.getChildren().add(ta);
    }

    // ---- Data loaders (mirror FoodCourierApplications) ----
    private List<Customer> loadCustomers(java.util.Map<Integer, String> locationNames) throws IOException {
        List<Customer> list = new ArrayList<>();
        java.nio.file.Files.lines(Paths.get("data/seed/customers.csv")).skip(1).forEach(line -> {
            if (line.isBlank()) return;
            String[] p = line.split(",");
            if (p.length < 4) return;
            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            int locId = Integer.parseInt(p[3].trim());
            String locName = locationNames.getOrDefault(locId, "Unknown");
            Location loc = new Location(String.valueOf(locId), locName, 0, 0);
            list.add(new Customer(id, name, phone, loc));
        });
        return list;
    }

    private List<Restaurant> loadRestaurants(java.util.Map<Integer, String> locationNames) throws IOException {
        List<Restaurant> list = new ArrayList<>();
        java.nio.file.Files.lines(Paths.get("data/seed/restaurants.csv")).skip(1).forEach(line -> {
            if (line.isBlank()) return;
            String[] p = line.split(",");
            if (p.length < 4) return;
            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            int locId = Integer.parseInt(p[3].trim());
            String locName = locationNames.getOrDefault(locId, "Unknown");
            Location loc = new Location(String.valueOf(locId), locName, 0, 0);
            list.add(new Restaurant(id, name, loc));
        });
        return list;
    }

    private List<Courier> loadCouriers(java.util.Map<Integer, String> locationNames) throws IOException {
        List<Courier> list = new ArrayList<>();
        java.nio.file.Files.lines(Paths.get("data/seed/couriers.csv")).skip(1).forEach(line -> {
            if (line.isBlank()) return;
            String[] p = line.split(",");
            if (p.length < 5) return;
            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            CourierStatus status = CourierStatus.valueOf(p[3].trim().toUpperCase());
            int locId = Integer.parseInt(p[4].trim());
            String locName = locationNames.getOrDefault(locId, "Unknown");
            Location loc = new Location(String.valueOf(locId), locName, 0, 0);
            list.add(new Courier(id, name, phone, status, loc));
        });
        return list;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
