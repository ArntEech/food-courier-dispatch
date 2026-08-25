# Food Courier Dispatch System

**DCIT 204/308 University Project — Ghana Context**

A food courier dispatch system implementing 5 algorithmic modules (Alpha 1–5) with custom data structures, demonstrating end-to-end order processing from intake to optimization.

---

## Quick Start

```bash
# Prerequisites: Java 17+, Maven 3.9+

# 1. Seed the database (run once, or to reset data)
mvn compile exec:java -Dexec.mainClass="com.foodcourier.db.DatabaseSeeder" -q

# 2. Run the full Alpha 1–5 pipeline demo
mvn compile exec:java -Dexec.mainClass="com.foodcourier.app.FoodCourierApplications" -q

# 3. Run all tests (91 tests)
mvn test
```

---

## System Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Alpha 1    │───▶│  Alpha 2    │───▶│  Alpha 3    │───▶│  Alpha 4    │───▶│  Alpha 5    │
│  Order      │    │  Priority   │    │  Courier    │    │  Route      │    │  Optimize   │
│  Intake     │    │  Dispatch   │    │  Assignment │    │  Navigation │    │  & Report   │
│  (Queue)    │    │  (Heap)     │    │  (Hash+BST) │    │  (Graph)    │    │  (Greedy/DP)│
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### Alpha Modules

| Alpha | Class | Custom Data Structure | Algorithm |
|-------|-------|----------------------|-----------|
| **A1** | `OrderIntakeService` | `ArrayQueue` (circular buffer) | FIFO order intake |
| **A2** | `DispatchService` | `BinaryHeap` (max-heap by priority) | Priority queue dispatch |
| **A3** | `CourierAssignmentService` | `HashMapTable` + `BST` | Hash lookup + sorted traversal |
| **A4** | `RouteNavigationService` | `Graph` (adjacency list) | BFS + Dijkstra + Kruskal MST |
| **A5** | `DeliveryOptimizationService` | — | Greedy (earliest-deadline) + 0/1 Knapsack DP |

---

## Database

### Schema (6 Tables + 2 Views)

```sql
locations     (id, name, latitude, longitude, type)
roads         (id, from_id, to_id, distance_km, travel_time_min, is_bidirectional)
customers     (id, name, phone, location_id FK)
restaurants   (id, name, phone, location_id FK)
couriers      (id, name, phone, status, current_location_id FK)
orders        (id, customer_id FK, restaurant_id FK, order_time, priority, status, prep_min)
deliveries    (id, order_id FK, courier_id FK, distance_km, est_time, actual_time, status)

Views:
  v_courier_performance  — orders, distance, time per courier
  v_daily_summary        — deliveries, km, avg time per day
```

### Seed Data (Ghana / UG Campus Context)

| Entity | Count | Description |
|--------|-------|-------------|
| Locations | 8 | UG Main Gate, Legon Hall, Commonwealth Hall, Akuafo Hall, University Hospital, Okponglo, Atomic Junction, UG Business School |
| Roads | 11 | Campus walkways + connecting roads (bidirectional) |
| Customers | 6 | Sample customers at campus locations |
| Restaurants | 5 | Food outlets near campus |
| Couriers | 5 | Available couriers with starting locations |
| Orders | 6 | Pending food orders with priorities |

---

## Running the Pipeline

### Expected Output

```
=== Food Courier Dispatch System ===

[Alpha 4] Loading delivery network...
  Network: 8 locations, 22 roads

[Setup] Building domain objects from seed data...
  Loaded: 6 customers, 5 restaurants, 5 couriers

[Alpha 1] Loading orders into intake queue...
  Orders in queue: 6
  Built 6 fully-populated orders

[Alpha 2] Prioritizing orders via BinaryHeap...
  Orders in dispatch heap: true (sample lookup)

[Alpha 3] Assigning couriers to orders...
  Order 1 → Courier Courier A (1)
    Route: 2 hops, 0.80 km, ~17 min
  Order 2 → Courier Courier A (1)
    Route: 3 hops, 1.50 km, ~18 min
  ...

[Alpha 5] Generating delivery optimization report...
=== Delivery Optimization Report ===
Total deliveries: 6
Average delivery time: 18.17 minutes
Total distance covered: 7.90 km

Couriers ranked by order volume:
Courier ID   | Name            | Orders   | Distance (km)   | Time (min)  
----------------------------------------------------------------------
1            | Courier A       | 6        | 7.90            | 109.0       

[Alpha 4] Minimum Spanning Network (Kruskal):
  MST edges: 7, total weight: 8.00 km

=== Pipeline complete ===
```

---

## Project Structure

```
food-courier-dispatch/
├── src/
│   ├── main/java/com/foodcourier/
│   │   ├── alpha1/          # Order intake (Queue)
│   │   ├── alpha2/          # Priority dispatch (Heap)
│   │   ├── alpha3/          # Courier assignment (Hash+BST)
│   │   ├── alpha4/          # Route navigation (Graph)
│   │   ├── alpha5/          # Optimization & reporting (Greedy+DP)
│   │   ├── algorithms/      # Core algorithms (sort, search, graph, optimization)
│   │   ├── app/             # FoodCourierApplications (main entry)
│   │   ├── db/              # DatabaseSeeder, schema
│   │   ├── domain/          # Domain models (Order, Courier, Customer, etc.)
│   │   └── dsa/             # Custom data structures (Queue, Heap, Hash, BST, Graph)
│   └── test/                # 91 unit tests
├── data/
│   ├── seed/                # CSV seed files (locations, roads, customers, etc.)
│   └── generated/           # dummy1.csv (Alpha 5 sample deliveries)
├── database/
│   └── schema.sql           # SQLite schema
└── pom.xml                  # Maven config (Java 17, JUnit 5, sqlite-jdbc)
```

---

## Key Commands

| Task | Command |
|------|---------|
| Compile | `mvn compile` |
| Seed DB | `mvn compile exec:java -Dexec.mainClass="com.foodcourier.db.DatabaseSeeder" -q` |
| Run pipeline | `mvn compile exec:java -Dexec.mainClass="com.foodcourier.app.FoodCourierApplications" -q` |
| Run tests | `mvn test` |
| Run single test | `mvn test -Dtest=DynamicProgrammingTest` |
| Inspect DB | `sqlite3 food_courier.db "SELECT * FROM v_courier_performance;"` |

---

## Design Notes

- **No built-in collections** for assessed components: custom `ArrayQueue`, `BinaryHeap`, `HashMapTable`, `BST`, `Graph`, `DisjointSet`
- **Algorithms implemented from scratch**: Linear/Binary Search, QuickSort/MergeSort, BFS/DFS, Dijkstra, Prim, Kruskal, 0/1 Knapsack DP, Greedy earliest-deadline-first
- **Persistence**: SQLite via `sqlite-jdbc` 3.46.1.0
- **Ghana context**: Seed data uses University of Ghana campus locations and road network

---

## Academic Context

This project satisfies DCIT 204/308 requirements:

- ✅ 5 Alpha modules with custom data structures
- ✅ Custom sorting (QuickSort, MergeSort) and searching (Linear, Binary)
- ✅ Graph algorithms (BFS, DFS, Dijkstra, Prim, Kruskal)
- ✅ Dynamic Programming (0/1 Knapsack for order batching)
- ✅ Greedy algorithm (earliest-deadline-first)
- ✅ Database persistence with schema, seed data, and reporting views
- ✅ Comprehensive test coverage (91 tests, all passing)