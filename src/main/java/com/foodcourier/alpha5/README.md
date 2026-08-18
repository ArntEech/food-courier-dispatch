# Alpha 5 — Optimization & Reporting

## What this module does

`DeliveryOptimizationService.generateReport()` takes a list of
`Delivery` records (courier, order, distance, estimated time) and
produces a summary report:

- **Average delivery time** across all deliveries, in minutes
- **Total distance** covered across all deliveries, in kilometres
- **Orders per courier** — deliveries handled per courier, with each
  courier's individual distance and time totals

Couriers are then ranked by order volume. Per the task requirement,
this reuses an existing sort rather than implementing a new one —
Issabella's `QuickSort` or Asare's `MergeSort` is called against a
list of `CourierReportRow` objects, once one of those classes has an
actual implementation (see `NOTES.md`: as of this snapshot both are
empty stubs on `main`, so the ranking currently falls back to
`CourierReportRow`'s own natural ordering as a placeholder).

The report is printed to console and written to a text file as
evidence for the technical report's performance-analysis section.

## Greedy vs Dynamic Programming trade-offs

The project's optimisation layer (Alpha 5, per the top-level README)
is expected to implement both a greedy algorithm and a
dynamic-programming algorithm for delivery decisions. Neither is
implemented yet in this repo (`Greedy.java` / `DynamicProgramming.java`
are empty stubs), so this section describes the trade-off the team
still needs to make a concrete implementation choice about:

**Greedy** (e.g. assigning the nearest available courier to each new
order, or dispatching by earliest deadline) makes the locally best
choice at each step and never revisits it. It runs fast — O(n log n)
once orders are sorted — and is simple to implement and explain. The
cost is no guarantee of a globally optimal outcome: always picking the
nearest courier can leave a courier who was slightly farther away, but
better positioned for several *upcoming* orders, unused. The project
explicitly asks for a documented greedy failure case — this is where
it would come from.

**Dynamic programming** (e.g. selecting which subset of orders a
courier should take on under a capacity or time-budget constraint,
knapsack-style) considers overlapping subproblems and builds toward a
provably optimal solution via memoisation or tabulation. It guarantees
optimality under the stated constraints but costs more — typically
O(n × capacity) time and memory — which grows quickly as order volume
or the budget range increases.

**In short:** greedy suits real-time dispatch where speed matters more
than provable optimality and constraints are loose. DP suits decisions
with a hard constraint (courier capacity, delivery time window) where
paying for the extra computation is worth getting the optimal
combination. This report is descriptive, not prescriptive — it
doesn't choose between them; it's the evidence used afterward to
evaluate whichever assignment strategy the team implements.

## Data source

Right now: a hand-built in-memory sample (`SampleReportDemo.java`),
because both `data/generated/dummy1.csv` and Alpha 4's route output
are empty in this snapshot. Once either is populated, swap the demo's
data-building step for a real CSV load or a call into Alpha 4's
output — `generateReport()` itself doesn't need to change, since it
only depends on `List<Delivery>`.

