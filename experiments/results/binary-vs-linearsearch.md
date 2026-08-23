# Binary Search vs. Linear Search — Courier Lookup

## What's being compared
Looking up a courier by ID in `data/seed/couriers.csv`, using Linear Search
(O(n)) vs Binary Search (O(log n), requires the list sorted by ID first via
`BST.inOrder()`).

## Method
Both searches run against the same courier dataset, searching for the same
target IDs, timed in nanoseconds and averaged over multiple runs.

## Results
| Dataset size | Linear Search (avg) | Binary Search (avg) |
|---|---|---|
| [N couriers] | [X ns/ms] | [Y ns/ms] |

*(Numbers pending — see below.)*

## Observations
- Binary Search's advantage grows with dataset size; the gap is small on a
  handful of couriers but widens as the seed data grows.
- Binary Search pays a one-time O(n) sorting cost via `inOrder()`, not per
  search — still a net win across repeated lookups.
- Linear Search remains the right tool for `findAvailableCourier`, since
  availability isn't a sorted attribute.

## Conclusion
Binary Search wins for repeated lookups by a stable, sortable key (courier
ID). Linear Search stays necessary for unsorted-attribute queries.