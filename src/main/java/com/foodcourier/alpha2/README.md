# Alpha 2 — Priority Dispatch

## Why a Binary Heap?

Alpha 2 uses a Binary Heap as the priority queue for dispatching orders.

A Binary Heap is suitable because the dispatch service repeatedly needs to retrieve the order with the highest priority. In our priority system, a smaller priority value represents a higher priority.

The heap provides efficient operations for this:

- `insert(Order)` — adds an order to the heap in O(log n) time.
- `min()` — returns the highest-priority order in O(1) time.
- `removeMin()` — removes and returns the highest-priority order in O(log n) time.

This is more efficient for repeated priority-based dispatching than scanning the entire list every time the next order needs to be selected.

## How Priority Is Scored

Each order has a `Priority` value:

| Priority | Score |
|---|---:|
| HIGH | 1 |
| MEDIUM | 2 |
| LOW | 3 |
| VERY_LOW | 4 |

The lower the score, the higher the priority.

Therefore, the dispatch service processes orders in ascending priority-score order:

`1 -> 2 -> 3 -> 4`

For example, an order with `HIGH(1)` is dispatched before an order with `MEDIUM(2)`, and an order with `MEDIUM(2)` is dispatched before an order with `LOW(3)`.

The `BinaryHeap` uses these scores when maintaining the heap property. The order at the root of the min-heap is therefore the order with the highest dispatch priority.

## Dispatch Flow

When an order is inserted:

1. The order is added to the Binary Heap.
2. The heap restores its ordering using the order's priority score.
3. When `dispatchNext()` is called, the minimum element is removed.
4. The dispatched order is also removed from the active-order list used by `findOrder()`.

This allows Alpha 2 to support both efficient priority-based dispatching and order lookup for cancellation.
