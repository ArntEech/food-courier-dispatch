# Benchmark: MergeSort vs QuickSort (dashboard/report snapshot sort)

**Author:** Asare Kelvin
**Component:** `algorithms/sorting` (used by Alpha 1/Alpha 2 to sort a snapshot
of the order queue for the dashboard/report view)

## What's being compared

- `MergeSort.sort(T[], Comparator<T>)` — this project's custom merge sort.
- `QuickSort.sort(T[], Comparator<T>)` — Issabella's custom quicksort
  (`src/main/java/com/foodcourier/algorithms/sorting/QuickSort.java`).

Both are given the exact same generated input at each size, so the
comparison isn't skewed by one algorithm getting easier data.

## Method

`SortBenchmark.java` generates dummy order keys (standing in for a field
like priority or timestamp) at four sizes — 100, 1,000, 10,000, and
100,000 — matching the sizes called out in the project README. Each size
is run for 5 trials with a different random seed per trial. Timing uses
`System.nanoTime()` around the `sort()` call only (data generation is
excluded from the timed region). Results are printed to stdout and written
to `experiments/results/mergesort_vs_quicksort_results.csv`.

Run it with:

```bash
mvn compile exec:java -Dexec.mainClass=com.foodcourier.algorithms.sorting.SortBenchmark
```

or by running `SortBenchmark.main` directly from an IDE.

## Complexity (what we expect to see)

|Best|Average|Worst|Space|Stable?|
| --- | --- | --- | --- | --- | --- |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) avg (recursion stack) | No |

On random dummy data at these sizes, we'd expect quicksort to be
competitive with (often slightly faster than) merge sort, since it sorts
in place and has smaller constant factors — merge sort pays for its O(n)
auxiliary array on every call. The gap to watch for is quicksort's worst
case: if `QuickSort`'s pivot choice is naive (e.g. always the last
element) and the input is already sorted or reverse-sorted, its runtime
degrades toward O(n²) while merge sort's stays flat. Once real numbers are
in, note here whether that shows up at the 10,000/100,000 sizes.

## Results

Sample run below (5 trials/size, JDK 21). Re-run `SortBenchmark` on your
own machine before putting numbers in the final report — these will shift
with hardware and JVM warm-up, and are here to show the expected shape of
the result, not as the report's final figures.

| Size | Avg MergeSort (ms) | Avg QuickSort (ms) |
| --- | --- | --- |
| 100 | 0 | 0 |
| 1,000 | 0 | 1 |
| 10,000 | 5 | 13 |
| 100,000 | 71 | 27 |

Raw per-trial numbers are in
`experiments/results/mergesort_vs_quicksort_results.csv`.

**Observations:**

- At 100 and 1,000 elements both algorithms are effectively instant — too
  fast for `System.nanoTime()` at this scale to say much, and run-to-run
  noise (a few ms either way) dominates.
- From 10,000 elements up, quicksort is consistently faster than merge
  sort on this random dummy data (roughly 2–3×), which matches the
  complexity table above: quicksort sorts in place with smaller constant
  factors, while merge sort pays for allocating and copying into its
  auxiliary array on every merge step.
- Run-to-run variance is visible even within one size (e.g. quicksort at
  10,000 ranged 6–21 ms across trials) — this is normal JVM/JIT warm-up
  noise, not an algorithmic effect, which is why the benchmark averages 5
  trials per size instead of trusting a single run.
- No worst-case blowup shows up here because the data is random, not
  sorted/reverse-sorted — quicksort's O(n²) worst case only shows up with
  an adversarial input against its pivot choice (see the placeholder
  `QuickSort`'s last-element pivot strategy above). Worth trying a sorted
  input as a follow-up trial once Issabella's real implementation is in,
  to see whether her pivot strategy avoids that case.

## Note on the QuickSort file in this branch

`QuickSort.java` in this branch is a **placeholder** (last-element pivot,
same method signatures as `MergeSort`) — just enough for the benchmark to
compile and produce a first set of numbers. Swap in Issabella's actual
`QuickSort.java` before the final report; as long as her class keeps the
same static `sort(T[])` / `sort(T[], Comparator<T>)` signatures, nothing
else in `SortBenchmark.java` needs to change.
