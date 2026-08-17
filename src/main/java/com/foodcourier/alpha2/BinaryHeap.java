package com.foodcourier.alpha2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.foodcourier.dsa.heap.HeapInterface;

public class BinaryHeap<E> implements HeapInterface<E> {

    private final List<E> heap;
    private final Comparator<? super E> comparator;

    public BinaryHeap() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public BinaryHeap(Comparator<? super E> comparator) {
        this.heap = new ArrayList<>();
        this.comparator = comparator != null
                ? comparator
                : (left, right) -> {
                    if (left instanceof Comparable) {
                        return ((Comparable<Object>) left).compareTo(right);
                    }
                    throw new IllegalArgumentException("BinaryHeap requires a comparable item or a Comparator.");
                };
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    @Override
    public void insert(E item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot insert a null item into the heap.");
        }
        heap.add(item);
        upheap(heap.size() - 1);
    }

    @Override
    public E min() {
        if (heap.isEmpty()) {
            return null;
        }
        return heap.get(0);
    }

    @Override
    public E removeMin() {
        if (heap.isEmpty()) {
            return null;
        }

        E minimum = heap.get(0);
        E last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            downheap(0);
        }

        return minimum;
    }

    @Override
    public void upheap(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (compare(heap.get(index), heap.get(parentIndex)) >= 0) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    @Override
    public void downheap(int index) {
        int leftIndex = 2 * index + 1;

        while (leftIndex < heap.size()) {
            int smallestIndex = leftIndex;
            int rightIndex = leftIndex + 1;

            if (rightIndex < heap.size() && compare(heap.get(rightIndex), heap.get(leftIndex)) < 0) {
                smallestIndex = rightIndex;
            }

            if (compare(heap.get(index), heap.get(smallestIndex)) <= 0) {
                break;
            }

            swap(index, smallestIndex);
            index = smallestIndex;
            leftIndex = 2 * index + 1;
        }
    }

    private void swap(int leftIndex, int rightIndex) {
        E temp = heap.get(leftIndex);
        heap.set(leftIndex, heap.get(rightIndex));
        heap.set(rightIndex, temp);
    }

    private int compare(E left, E right) {
        return comparator.compare(left, right);
    }
}
