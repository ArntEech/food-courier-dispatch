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

# Dijkstra — Daniel

For the courier system, the thing that actually matters when picking a route between two locations isn't how many roads you cross — it's the real cost of getting there, whether that's measured in distance or travel time. BFS can't capture that: it treats every edge as costing exactly 1, so it would happily return a route with two short hops over one direct road that's actually faster. That's fine for something like "fewest turns," but useless for "cheapest/fastest route," which is what a courier dispatch system needs. Dijkstra solves the actual problem because it accounts for edge weight at every step, always expanding toward whichever reachable location currently has the lowest total cost from the source.

**How the priority queue is used:** `BinaryHeap` only needs to support `insert`, `extractMin`, and `isEmpty` — it doesn't need decrease-key. Instead of updating an existing entry when a shorter distance is found, I just push a new `(vertex, distance)` entry into the heap. When a stale entry for an already-finalized vertex gets popped later, it's simply skipped. This is the standard "lazy deletion" trick that keeps a plain binary heap correct without needing a more complex heap implementation.

**Edge cases handled:**
- **Disconnected locations:** if a location can't be reached from the source, its distance stays at `Double.POSITIVE_INFINITY` rather than the algorithm crashing or returning a misleading 0. `shortestPath()` returns an empty list for unreachable targets instead of throwing.
- **Single-node / source-to-itself:** asking for the distance or path from a location to itself returns `0.0` and a single-element path, with no special-casing needed — the path-reconstruction loop naturally stops as soon as it reaches the source.
- **Negative-weight rejection:** Dijkstra's correctness *depends on* non-negative edge weights (once a vertex is finalized, a negative edge could invalidate that decision). Rather than silently producing wrong answers, the algorithm throws `IllegalArgumentException` the moment it encounters a negative-weight edge, so a bad input fails loudly instead of corrupting a "shortest" route quietly.

---

# Prim — Daniel

Prim's algorithm demonstrates how to build the cheapest possible network that still connects every location — useful if the team ever needs to reason about, say, the minimum-cost set of roads/links needed to keep every drop-off point reachable, rather than the cheapest path between two specific points (which is what Dijkstra is for). Same overall goal as Kruskal — minimum spanning tree — but a different strategy for getting there:

- **Prim** grows the tree outward from one starting vertex. At each step, it looks only at edges on the current *frontier* of the tree (edges connecting an included vertex to one not yet included) and picks the cheapest one, using a heap to always surface that cheapest frontier edge first. It's a local, incremental view of the graph.
- **Kruskal** takes a global view instead: sort *every* edge in the graph by weight up front, then greedily add the next-cheapest edge as long as it doesn't create a cycle — checked with a disjoint-set/union-find structure, without caring which specific vertices it connects.

Both are greedy and both are provably correct, and for the same connected graph they always converge on the same total MST weight, even though the edges they pick along the way and the order they pick them in can differ. That's exactly what `KruskalTest` verifies: it cross-checks Prim's and Kruskal's total MST weight against each other on both a hand-traced test graph (where the correct answer is known ahead of time) and real seed road data (a more realistic, harder-to-hand-verify case) — agreement between two structurally different algorithms is a strong signal that both implementations are correct, rather than both being wrong in the same way.

**Edge cases handled:**
- **Disconnected graph:** if the graph isn't fully connected from the start vertex, Prim returns the MST of just the reachable component (a "forest" fragment) rather than failing. `connectsAllVertices()` reports `false` in this case so callers can detect that the result doesn't span the whole graph.
- **Single vertex:** returns an empty edge list with `0.0` total weight, and `connectsAllVertices()` is trivially `true` since there's nothing left to connect.

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
