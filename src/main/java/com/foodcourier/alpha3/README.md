# Alpha 3 — Courier Assignment (Hash Table + BST)

## Overview

Alpha 3 is responsible for matching orders to couriers. The core requirement
is two-fold: look up a specific courier instantly by ID, and be able to query
couriers in sorted order. No single data structure does both well, which is
why this alpha is split across two structures instead of one.

## Why Hash Table + BST (not just one structure)

**Hash Table (`HashMapTable`)** — O(1) average-case lookup by courier ID.
Used whenever the system already knows *which* courier it wants.

**BST (`BST<String>`)** — maintains courier IDs in sorted order and supports
in-order traversal, which a hash table cannot do. This is what makes sorted
queries — and Binary Search — possible in the first place.

## Sort key: courier ID, stored as `String` in the BST

The BST stores courier **IDs**, not full `Courier` objects. `BST<E extends
Comparable<E>>` requires its type parameter to implement `Comparable`, and
`Courier` does not (nor should it be changed to, since it's a shared domain
class used across every alpha). `String` already implements `Comparable`
naturally, so storing IDs in the BST — and resolving the full `Courier`
object via `HashMapTable` when needed — avoids modifying shared code
entirely while still getting sorted access.

## Why both Linear Search and Binary Search

**Linear Search** does the real work in `findAvailableCourier`: courier
availability isn't correlated with ID order, so scanning for the first
`AVAILABLE` courier is the correct tool, not a workaround.

**Binary Search** is used as a consistency check immediately after: once
Linear Search finds a candidate, Binary Search confirms that candidate's ID
is genuinely present in the BST-derived sorted ID list — an O(log n) check
that `HashMapTable` and `BST` agree on the same courier, using the same
sorted data Linear Search just scanned.

## Files

| File | Purpose |
|---|---|
| `dsa/hashtable/HashMapTable.java` | O(1) courier lookup by ID |
| `dsa/tree/BST.java` | Sorted courier ID storage, in-order export |
| `algorithms/searching/BinarySearch.java` | O(log n) lookup over sorted IDs |
| `algorithms/searching/LinearSearch.java` | O(n) scan for first available courier |
| `alpha3/CourierAssignmentService.java` | Wires all four together |

## Performance comparison

See [`experiments/results/binary-search-vs-linear-search.md`](../experiments/results/binary-search-vs-linear-search.md).