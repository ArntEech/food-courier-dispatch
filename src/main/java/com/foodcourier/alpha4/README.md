# Alpha 4 — Route & Navigation

**Operational question:** How do we navigate the delivery network?

**Owners:** Caleb Adjei (Graph, Disjoint Set), Frank (BFS, DFS), Daniel (Dijkstra, Prim), Jonathan (Kruskal, integration)

---

## Why each algorithm is used where

### BFS / DFS — Frank
> TODO (Frank): explain, in your own words, why BFS/DFS are used here —
> e.g. connectivity checks ("can this courier even reach this customer"),
> unweighted hop-count, or validating the network has no isolated locations
> before routing is attempted. Keep it to what you can defend live.

### Dijkstra — Daniel
> TODO (Daniel): explain why Dijkstra is used for the actual courier
> route (weighted shortest path — road distance/time matters, unlike
> BFS which treats every edge as equal cost), and any edge cases you
> handled (disconnected locations, single-node route, tie-breaking).

### Prim — Daniel
> TODO (Daniel): explain what Prim demonstrates here — likely an
> alternative way to compute the minimum spanning network (same goal as
> Kruskal, different approach: grows outward from one node using a heap,
> vs. Kruskal's global edge-sort). Worth noting when Prim is preferable
> to Kruskal (denser graphs) vs. the reverse (sparser graphs).

### Kruskal — Jonathan (this section is mine, written in full)

Kruskal's algorithm answers a different question than Dijkstra: not
"what's the fastest route for this one delivery," but "what's the
minimal set of roads needed to keep every location in the network
connected at all." That's a network-design question, not a single-trip
routing question — useful for Alpha 5's optimisation/reporting work,
e.g. identifying which roads are structurally essential vs. redundant.

**How it works here:** every road (edge) is sorted by distance using a
from-scratch merge sort. Starting from the shortest edge, we add it to
the spanning network only if it doesn't create a cycle — checked via
the Disjoint Set's `connected()` method. If two locations are already
connected through some other path, adding another direct road between
them would be redundant for the "keep everything connected" goal, so
it's skipped. This greedy strategy is provably optimal for MST: always
picking the cheapest edge that doesn't form a cycle never needs to be
undone later.

**Complexity:** O(E log E), dominated by the sort — the union-find
operations (`union`/`connected`) are near O(1) amortised thanks to path
compression.

**Tested against:** a hand-traced 5-location graph (A–E) with a known
correct MST (total weight 10, 4 edges) — see `KruskalTest.java` for the
normal case, plus single-vertex, disconnected-graph, and tied-weight
edge cases.

---

## Known integration gaps (tracked, not yet resolved)

- `GraphInterface<V>` currently has no way to list edges with weights
  (`getNeighbors()` only returns `List<V>`, no weight). Kruskal, Dijkstra,
  and Prim all need this. **Owner: Caleb** — proposed fix: add
  `List<Edge<V>> getEdges()` or a `getWeight(V from, V to)` accessor.
- `RouteNavigationService.getRoute()` is currently a sketch (see class
  Javadoc) — the exact method signatures Dijkstra/BFS need to expose are
  documented there as contracts, not yet wired to real implementations.

---

## Data

Loaded from `data/seed/locations.csv` (nodes) and `data/seed/roads.csv`
(edges). **Note:** `locations.csv` is currently empty in the repo —
needs to be populated with real location data before this Alpha can run
against anything but hand-built test graphs.
