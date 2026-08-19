

### Dijkstra

Dijkstra's algorithm finds the shortest paths from one source vertex to every
reachable vertex in a weighted graph. The implementation is in
`src/main/java/com/foodcourier/algorithms/graph/Dijkstra.java` and uses the custom `BinaryHeap` from Alpha 2
as a priority queue.

Available operations include:

- `shortestPaths(graph, source)` returns the shortest distance to every vertex.
- `shortestDistance(graph, source, target)` returns one shortest distance.
- `shortestPath(graph, source, target)` returns the vertices in the selected route.

Dijkstra requires non-negative edge weights. Unreachable vertices retain a
distance of `Double.POSITIVE_INFINITY`, and no path is returned for them.

### Prim

Prim's algorithm builds a minimum spanning tree for a connected weighted graph
by repeatedly selecting the least-cost edge that reaches an unvisited vertex.
The implementation is in `src/main/java/com/foodcourier/algorithms/graph/Prim.java` and also uses the custom
`BinaryHeap` as its edge-priority queue.

`minimumSpanningTree(graph, start)` returns a `Prim.Result` containing the
selected edges, their total weight, and a `connectsAllVertices()` check. For a
disconnected graph, the result represents the reachable minimum spanning
forest from the chosen start vertex.

