# Alpha 4 — Route & Navigation

**Operational question:** How do we navigate the delivery network?

**Owners:** Caleb Adjei (Graph, Disjoint Set, data loading), Frank (BFS, DFS), Daniel (Dijkstra, Prim), Jonathan (Kruskal, integration)

---

## Architecture note: one canonical graph

Alpha 4 initially had two independent graph implementations (Caleb's
`AdjacencyListGraph` and Daniel's `Graph`), which caused a real compile
conflict once `GraphInterface` diverged between them. Resolved by
standardizing on **`dsa.graph.Graph<V>`** (Daniel's) as the single graph
implementation used across the whole Alpha `DataLoader`, Kruskal,
Prim, and Dijkstra all build on and consume the same `Graph<V>` /
`Edge<V>` types now. `DataLoader` loads bidirectional roads via
`graph.addUndirectedEdge()`.

---

## Why each algorithm is used where

### BFS / DFS — Frank
> TODO (Frank): explain, in your own words, why BFS/DFS are used here
> e.g. connectivity checks ("can this courier even reach this
> customer"), unweighted hop-count, or validating the network has no
> isolated locations before routing is attempted. Keep it to what you
> can defend live.

### Dijkstra — Daniel
> TODO (Daniel): explain why Dijkstra is used for the actual courier
> route (weighted shortest path — road distance/time matters, unlike
> BFS which treats every edge as equal cost), and any edge cases you
> handled (disconnected locations, single-node route, negative-weight
> rejection).

### Prim — Daniel
> TODO (Daniel): explain what Prim demonstrates here, and how it
> compares to Kruskal (same goal — minimum spanning network — different
> approach: Prim grows outward from one start vertex using a heap,
> Kruskal globally sorts all edges first). Worth noting Kruskal and
> Prim are cross-checked against each other in `KruskalTest` — both
> must agree on total MST weight for a connected graph, and they do,
> against both a hand-traced test graph and real seed road data.

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
Set's `connected()` method. If two locations are already connected
through some other path, adding another direct road between them would
be redundant for the "keep everything connected" goal, so it's
skipped. This greedy strategy is provably optimal for MST: always
picking the cheapest edge that doesn't form a cycle never needs to be
undone later.

**Complexity:** O(E log E), dominated by the sort the union-find
operations (`union`/`connected`) are near O(1) amortised thanks to path
compression (Caleb's `DisjointSet` implementation).

**Note on undirected edges:** since bidirectional roads are stored as
two directed `Edge` entries internally (A→B and B→A), the raw edge list
Kruskal collects contains both directions. This is harmless the
union-find naturally skips the redundant direction once the first one
connects the pair but worth knowing if edge counts look roughly
double the raw road count.

**Verified against:**
- A hand-traced 5-vertex graph (A–E) with a known correct MST (total
  weight 10, 4 edges) normal case, single-vertex, disconnected-graph,
  and tied-weight edge cases all covered in `KruskalTest.java`.
- **Cross-checked against Prim** on the same graph both algorithms
  independently agree on total MST weight (10.0), which is a strong
  correctness signal since MST total weight is unique even when the
  exact edge set isn't.
- **Real seed data** (`data/seed/locations.csv` /
  `data/seed/roads.csv`, loaded via `DataLoader`) Kruskal and Prim
  again agree exactly on total MST weight, and Dijkstra returns a
  correct, sensible shortest path between two real locations.

---

## Data

Loaded from `data/seed/locations.csv` (vertices) and
`data/seed/roads.csv` (edges) via `alpha4/DataLoader.java`, which
builds a `Graph<Integer>` keyed by location ID.
