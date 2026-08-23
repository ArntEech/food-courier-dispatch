package com.foodcourier.algorithms.sorting;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;

/**
 * Benchmarks {@link MergeSort} against {@link QuickSort} on generated dummy
 * order data, at the input sizes called out in the project README
 * (100 / 1,000 / 10,000 / 100,000). Satisfies the "performance analysis"
 * requirement for Alpha 2 / Alpha 5.
 *
 * <p>Run with: {@code mvn compile exec:java
 * -Dexec.mainClass=com.foodcourier.algorithms.sorting.SortBenchmark}
 * (or run {@code main} directly from your IDE).
 *
 * <p>Results are printed to stdout and appended as CSV rows to
 * {@code experiments/results/mergesort_vs_quicksort_results.csv} so they can
 * be pulled into a chart or the final report.
 */
public final class SortBenchmark {

    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int TRIALS_PER_SIZE = 5;
    private static final String RESULTS_CSV = "experiments/results/mergesort_vs_quicksort_results.csv";

    public static void main(String[] args) throws IOException {
        System.out.printf("%-10s %-12s %-15s %-15s%n", "Size", "Trial", "MergeSort(ms)", "QuickSort(ms)");

        try (FileWriter writer = new FileWriter(RESULTS_CSV)) {
            writer.write("size,trial,mergesort_ms,quicksort_ms\n");

            for (int size : SIZES) {
                for (int trial = 1; trial <= TRIALS_PER_SIZE; trial++) {
                    Integer[] baseData = generateDummyOrderKeys(size, trial);

                    Integer[] forMerge = baseData.clone();
                    Integer[] forQuick = baseData.clone();

                    long mergeMillis = timeSort(() -> MergeSort.sort(forMerge, Comparator.naturalOrder()));
                    long quickMillis = timeSort(() -> QuickSort.sort(forQuick, Comparator.naturalOrder()));

                    System.out.printf("%-10d %-12d %-15d %-15d%n", size, trial, mergeMillis, quickMillis);
                    writer.write(size + "," + trial + "," + mergeMillis + "," + quickMillis + "\n");
                }
            }
        }

        System.out.println("\nResults written to " + RESULTS_CSV);
    }

    /**
     * Generates a reproducible array of pseudo order keys (e.g. standing in
     * for priority or timestamp values) for a given size. Same seed per
     * (size, trial) pair means both algorithms sort identical input, so the
     * comparison is fair.
     */
    private static Integer[] generateDummyOrderKeys(int size, int trialSeed) {
        Random random = new Random(size * 31L + trialSeed);
        Integer[] data = new Integer[size];
        for (int i = 0; i < size; i++) {
            data[i] = random.nextInt(1_000_000);
        }
        return data;
    }

    private static long timeSort(Runnable sortCall) {
        long start = System.nanoTime();
        sortCall.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000; // convert to milliseconds
    }
}
