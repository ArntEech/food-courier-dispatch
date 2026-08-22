# Alpha 4 — Route & Navigation

**Operational question:** How do we navigate the delivery network?

**Owners:** Caleb Adjei (Graph, Disjoint Set, data loading), Frank (BFS, DFS), Daniel (Dijkstra, Prim), Jonathan (Kruskal, integration)

**Status: complete.** All five algorithms and `RouteNavigationService` are
implemented, integrated, and verified against real seed data.

---

## Architecture note: one canonical graph

Alpha 4 initially had two independent graph implementations (Caleb's
`AdjacencyListGraph` and Daniel's `Graph`), which caused a real compile
conflict once `GraphInterface` diverged between them. Resolved by
standardizing on **`dsa.graph.Graph<V>`** (Daniel's) as the single graph
implementation used across the whole Alpha `DataLoader`, BFS, DFS,
Dijkstra, Prim, and Kruskal all build on and consume the same `Graph<V>`
/ `Edge<V>` types. `DataLoader` loads bidirectional roads via
`graph.addUndirectedEdge()`.

BFS and DFS were originally written against a different, index-based
graph shape (`indexOf`/`idOf`/`neighborsOf`) before the canonical-graph
decision landed retargeted to the real `Graph<V>` API
(`getEdgesFrom()`, `vertexCount()`, `getVertices()`) while keeping the
original design: hand-rolled custom queue/stack instead of
`java.util.Queue`/`Deque`, and the `isReachable()`/`shortestHopDistance()`
helper methods.

---

## Why each algorithm is used where

### BFS / DFS — Frank
> TODO (Frank): explain, in your own words, why BFS/DFS are used here —
> e.g. connectivity checks ("can this courier even reach this
> customer"), unweighted hop-count, or validating the network has no
> isolated locations before routing is attempted. Both are verified
> working against real seed data (see integration notes below) — this
> section just needs your own explanation of the *why*, since that's
> what you'll be defending live.

### Dijkstra — Daniel
> TODO (Daniel): explain why Dijkstra is used for the actual courier
> route (weighted shortest path road distance/time matters, unlike
> BFS which treats every edge as equal cost), and any edge cases you
> handled (disconnected locations, single-node route, negative-weight
> rejection).

### Prim — Daniel
> TODO (Daniel): explain what Prim demonstrates here, and how it
> compares to Kruskal (same goal minimum spanning network different
> approach: Prim grows outward from one start vertex using a heap,
> Kruskal globally sorts all edges first). Worth noting Kruskal and
> Prim are cross-checked against each other in `KruskalTest` both
> agree on total MST weight for a connected graph, against both a
> hand-traced test graph and real seed road data.

### Kruskal — Jonathan (this section is mine, written in full)

Kruskal's algorithm answers a different question than Dijkstra: not
"what's the fastest route for this one delivery," but "what's the
minimal set of roads needed to keep every location in the network
connected at all." That's a network-design question, not a single-trip
routing question useful for Alpha 5's optimisation/reporting work,
e.g. identifying which roads are structurally essential vs. redundant.

**How it works here:** every road (edge) is pulled from the graph and
sorted by distance using a from-scratch merge sort (operating on a
`List<Edge<V>>`, not an array, to avoid generic-array-creation
warnings). Starting from the shortest edge, we add it to the spanning
network only if it doesn't create a cycle checked via the Disjoint
Set's `connected()` method. This greedy strategy is provably optimal
for MST: always picking the cheapest edge that doesn't form a cycle
never needs to be undone later.

**Complexity:** O(E log E), dominated by the sort the union-find
operations (`union`/`connected`) are near O(1) amortised thanks to path
compression (Caleb's `DisjointSet` implementation).

**Verified against:** a hand-traced 5-vertex graph (known correct MST,
weight 10), cross-checked against Prim on the same graph, and against
real seed data via `RouteNavigationService.minimumSpanningNetwork()`
(MST weight 8.0 km, connects all vertices).

---

## RouteNavigationService — integration (Jonathan)

`getRoute(Order, Courier)` is fully wired, not a sketch:

1. Converts the courier's, restaurant's, and customer's `Location`
   objects to graph vertex IDs (`Location.getId()`, parsed as an int matches the numeric `location_id` in the seed CSVs).
2. Checks reachability with **BFS** before attempting to route, so a
   disconnected network fails with a clear error instead of Dijkstra
   silently returning nothing.
3. Computes courier → restaurant and restaurant → customer as two
   **Dijkstra** legs, combines them into one path + total distance.

`isReachable()` and `minimumSpanningNetwork()` are also real, backed by
BFS and Kruskal respectively.

Verified end-to-end against real seed data: a full `getRoute()` call
correctly chains all three locations with a sensible total distance,
and `minimumSpanningNetwork()` matches the same 8.0 km MST Kruskal
produces standalone.

**Design note:** works internally in `Integer` location IDs (matching
`DataLoader`'s `Graph<Integer>`) rather than `Graph<Location>` directly,
since that's the pipeline that's actually tested end to end.

---

## Data

Loaded from `data/seed/locations.csv` (vertices) and
`data/seed/roads.csv` (edges) via `alpha4/DataLoader.java`, which
builds a `Graph<Integer>` keyed by location ID.