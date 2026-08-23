# Alpha 1 — Order Intake Service

## Architecture & Algorithm Selections

### 1. Why Queue (`ArrayDeque`) for Order Intake?
- **First-In, First-Out (FIFO) Guarantees**: A `Queue` ensures that incoming customer orders are handled strictly in chronological sequence.
- **Constant Time Performance**: Operations like `add()` / `offer()` (enqueue) and `poll()` (dequeue) run in $O(1)$ amortized time complexity.
- **Decoupling Pipeline Components**: Serves as a buffer between incoming submission requests and downstream processing workers.

---

### 2. Why QuickSort for Batch Sorting?
- **In-Place Sorting**: QuickSort sorts elements directly in memory without allocating additional arrays, keeping memory usage at $O(\log n)$ auxiliary space.
- **Average Time Complexity**: $O(n \log n)$ average-case performance makes it highly efficient for staging and ordering large datasets before ingestion into the active intake queue.
- **Cache-Friendly locality**: Array access patterns during partitioning benefit heavily from processor cache locality.

---

### 3. Why Linear Search for Queue Inspection?
- **Unsorted Key Field**: The intake queue maintains ordering strictly by **Timestamp**, not by `orderId`. Binary search cannot be applied to an unsorted attribute.
- **Zero Overhead**: Linear search scans the collection sequentially ($O(n)$ time complexity) without requiring extra memory to build and maintain secondary hash index maps during high-throughput queue operations.

---

## End-to-End Execution Flow

1. **Ingestion**: Raw records are loaded from `data/seed/orders.csv`.
2. **Preprocessing**: `QuickSort.sort(orders)` arranges orders in ascending chronological order by timestamp.
3. **Enqueueing**: Sorted orders are pushed sequentially into the intake `Queue` (`orderQueue`).
4. **Lookup**: `LinearSearch.searchById(orderQueue, orderId)` searches active queue contents sequentially.
5. **Dispatch**: `getNextOrder()` polls orders from the queue sequentially in strict FIFO order.