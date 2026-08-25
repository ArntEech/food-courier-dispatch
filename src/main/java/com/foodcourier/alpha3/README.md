# Alpha 3 — Courier Assignment (Hash Table + BST)

## Overview

Alpha 3 is responsible for matching orders to couriers. Given an incoming
order, the system needs to confirm the order is genuinely active, then find
a courier who's available to take it.

## Why Hash Table + BST (design intent)

**Hash Table (`HashMapTable`)** — O(1) average-case lookup of a courier by
ID. Used whenever the system already knows *which* courier it wants.

**BST (`BST<E extends Comparable<E>>`)** — intended to keep courier IDs in
sorted order and support retrieving them that way. This is the design
target for sorted, ordered access to the courier pool.

## Current implementation status

`BSTInterface`/`BST` currently expose `insert`, `contains`, `search`,
`remove`, `size`, and `isEmpty` — there is no method yet to export the
tree's contents as a sorted list (e.g. an `inOrder()` traversal). Because of
this, `CourierAssignmentService` currently takes a pre-sorted `List<String>`
of courier IDs directly via its constructor, rather than reading from the
BST. This keeps the service fully working and testable today, without
requiring any change to `BST.java` or `BSTInterface.java`. Once a sorted-
export method is added to the BST, `CourierAssignmentService` can be updated
to source `sortedCourierIds` from `BST.inOrder()` instead of a pre-sorted
list — the internal logic of `findAvailableCourier` does not otherwise
change.

## Why both Linear Search and Binary Search

**Linear Search (Issabella's `LinearSearch.searchById`, reused as-is)** —
her implementation searches `List<Order>` by ID, not couriers. It's used in
`findAvailableCourier` as a validation step: before attempting to assign a
courier, confirm the incoming order is genuinely present in the system's
active-orders list.

**Binary Search** — used as a consistency check once an available courier
has been found by scanning the sorted ID list: confirms that courier's ID
is genuinely present in that same sorted list, using `BinarySearch.search`
at O(log n) rather than re-scanning it.

**The scan for an available courier itself** (checking each courier's
`CourierStatus` until one is found) is a plain loop inside
`CourierAssignmentService` — not a call to `LinearSearch`, since her method
only operates on `Order`, not `Courier`. This loop is O(n), the same
complexity class as a linear search, but is implemented directly rather
than reusing a named utility, since no existing one fits the type.

## Files

| File | Purpose |
|---|---|
| `dsa/hashtable/HashMapTable.java` | O(1) courier lookup by ID |
| `dsa/tree/BST.java` | Sorted courier ID storage (sorted-export pending) |
| `algorithms/searching/BinarySearch.java` | O(log n) lookup over a sorted ID array |
| `algorithms/searching/LinearSearch.java` | O(n) lookup of an Order by ID (Issabella's, cross-alpha) |
| `alpha3/CourierAssignmentService.java` | Wires all of the above together |

## Performance comparison

See [`experiments/results/binary-search-vs-linear-search.md`](../experiments/results/binary-search-vs-linear-search.md).