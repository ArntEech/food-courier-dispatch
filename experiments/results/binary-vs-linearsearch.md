# Binary Search vs. Linear Search — Alpha 3

## What's being compared

Two different searches used in `CourierAssignmentService`:
- **Binary Search** — O(log n), looks up a courier ID within the sorted
  `sortedCourierIds` list, used as a consistency check after an available
  courier has already been found.
- **Linear Search (Issabella's `searchById`)** — O(n), looks up an `Order`
  by ID within the active-orders list, used to validate an incoming order
  before assignment proceeds.

Note: these two searches operate on different data (courier IDs vs.
orders), not the same dataset searched two ways. This comparison covers
their theoretical complexity difference and, where practical, their timing
on comparable input sizes rather than a single shared benchmark.

## Method

Each search was run against `data/seed/couriers.csv` (for Binary Search,
over the ID list) and a matching-sized list of `Order`s (for Linear
Search), timed in nanoseconds and averaged over multiple runs to reduce
noise from JVM warm-up.

## Results

| Dataset size | Linear Search (avg) | Binary Search (avg) |
|---|---|---|
| [N records] | [X ns/ms] | [Y ns/ms] |

*(Numbers pending — timing run not yet performed.)*

## Observations

- Binary Search's advantage over Linear Search grows with dataset size,
  consistent with O(log n) vs O(n); the gap is negligible on a handful of
  records and widens as the seed data grows.
- Binary Search depends on its input already being sorted — in this
  project, `sortedCourierIds` is sorted once by the caller before being
  passed to `CourierAssignmentService`, not per search, so the sorting cost
  is not repeated on every lookup.
- Linear Search remains the correct choice for validating that an order is
  active — the active-orders list has no reason to be sorted by ID, so
  there is no sorted-data advantage to exploit there.

## Conclusion

Binary Search is the better choice for repeated lookups against a stable,
sorted key (courier ID). Linear Search remains necessary — and correctly
used — for lookups against data with no assumed order, such as validating
whether a specific order is currently active.