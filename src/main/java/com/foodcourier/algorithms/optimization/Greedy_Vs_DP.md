# Greedy vs Dynamic Programming — comparison (Jasher, Alpha 5)

Both algorithms in this package solve the same problem framed two ways:
**given a courier's remaining time budget and a pool of candidate
deliveries (each with an `estimatedTimeMinutes` cost and an order
`value`), which deliveries should the courier take on to maximise total
value?** That's 0/1 knapsack — weight = time, value = order value,
capacity = the courier's time budget.

- `Greedy.batchDeliveries(deliveries, capacityMinutes)` — sorts
  candidates by value-per-minute descending and takes each one that
  still fits, never revisiting a decision.
- `DynamicProgramming.java` (Francis) is the exact/tabulated solver for
  the same problem — as of this writeup it's still an empty stub, so the
  numbers below come from a small local reference DP written just for
  this comparison (`GreedyVsDynamicProgrammingComparisonTest
  .referenceDpOptimalValue`), not from Francis's file. The problem shape
  is identical, so swapping in the real implementation later shouldn't
  change the conclusions here.

## Result quality

Greedy is **not guaranteed optimal**. `GreedyTest
.batchDeliveries_canLoseToOptimalOnClassicCounterexample()` shows a
constructed failure case:

| Delivery | time (min) | value | value/min |
|---|---|---|---|
| A | 10 | 60  | 6.0 |
| B | 20 | 100 | 5.0 |
| C | 30 | 120 | 4.0 |

Capacity = 50 minutes. Greedy takes A then B (30 minutes used, value
160) — by the time it's B's turn there's only 20 minutes left, so C
(which needs 30) never gets considered, even though **B + C** fits
exactly in 50 minutes for value 220. Greedy commits to the locally-best
item (A) first and can't undo that choice once a better combination
turns out to need the budget A used up. DP evaluates every combination
implicitly via its table and always finds B + C.

On the generated dummy data in
`GreedyVsDynamicProgrammingComparisonTest`, the gap is usually much
smaller than that — real order values/times aren't adversarially
constructed, so the greedy density heuristic tends to land within a few
percent of optimal. But "usually close" isn't a guarantee, and the test
asserts the one property that always holds: **DP's value is never less
than greedy's**, because DP is exact and greedy is a heuristic for the
same problem.

## Running time

- **Greedy**: dominated by the sort, O(n log n) for n candidate
  deliveries. Capacity doesn't affect the running time at all.
- **DP**: O(n × capacity) time and space for the tabulation, since it
  builds an `n × capacity` table. This grows with the time budget, not
  just the number of deliveries — a courier with a longer shift (bigger
  capacity, in minutes) makes every DP run more expensive even if the
  order count doesn't change.

For a small number of deliveries and a modest capacity the difference
is invisible. It shows up as candidate counts and shift lengths grow:
greedy's cost barely moves (sorting a few hundred items is cheap), while
DP's table can get large fast if capacity is expressed in minutes over
a long shift. `GreedyVsDynamicProgrammingComparisonTest` runs both at a
few sizes (n = 50, 200, 500 deliveries) and prints the measured value
and wall-clock time for each — run it locally (`mvn test -Dtest=
GreedyVsDynamicProgrammingComparisonTest`) to see actual numbers on a
given machine, since exact timings aren't something to hard-code here.

## Trade-off, in short

Greedy suits real-time dispatch: a new order comes in, a decision is
needed immediately, and being close to optimal fast beats being exactly
optimal slowly. DP suits an offline planning step — e.g. building a
courier's batch for their whole shift up front — where the time budget
is known ahead of time and it's worth paying the extra computation for
a provably best combination. Neither is "better" in isolation; they
answer the same question under different constraints.
