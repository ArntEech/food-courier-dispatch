package com.foodcourier.dsa.heap;

import java.util.ArrayList;

import com.foodcourier.domain.Order;

public class BinaryHeap implements HeapInterface<Order> {

    private final ArrayList<Order> heap;
    private final ArrayList<PriorityEntry<?>> priorityHeap;

    public static final class PriorityEntry<T> {

        private final T item;
        private final double priority;

        private PriorityEntry(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        public T getItem() {
            return item;
        }

        public double getPriority() {
            return priority;
        }
    }

    public BinaryHeap() {
        heap = new ArrayList<>();
        priorityHeap = new ArrayList<>();
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
    public void insert(Order item) {
        heap.add(item);
        upheap(heap.size() - 1);
    }

    @Override
    public Order min() {
        return heap.get(0);
    }

    @Override
    public Order removeMin() {

        if (heap.isEmpty()) {
            return null;
        }

        Order minimum = heap.get(0);

        Order last = heap.remove(heap.size() - 1);

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

            if (heap.get(index).getPriority().getValue()
                    >= heap.get(parentIndex).getPriority().getValue()) {
                break;
            }

            Order temporary = heap.get(index);
            heap.set(index, heap.get(parentIndex));
            heap.set(parentIndex, temporary);

            index = parentIndex;
        }
    }

    @Override
    public void downheap(int index) {

        while (true) {

            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (leftChild >= heap.size()) {
                break;
            }

            int smallerChild = leftChild;

            if (rightChild < heap.size()
                    && heap.get(rightChild).getPriority().getValue()
                    < heap.get(leftChild).getPriority().getValue()) {

                smallerChild = rightChild;
            }

            if (heap.get(index).getPriority().getValue()
                    <= heap.get(smallerChild).getPriority().getValue()) {
                break;
            }

            Order temporary = heap.get(index);
            heap.set(index, heap.get(smallerChild));
            heap.set(smallerChild, temporary);

            index = smallerChild;
        }
    }

    public <T> void insertPriority(T item, double priority) {
        priorityHeap.add(new PriorityEntry<>(item, priority));
        int index = priorityHeap.size() - 1;
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (priorityHeap.get(parentIndex).priority <= priorityHeap.get(index).priority) {
                break;
            }
            swapPriorityEntries(index, parentIndex);
            index = parentIndex;
        }
    }

    public PriorityEntry<?> removeMinPriority() {
        if (priorityHeap.isEmpty()) {
            return null;
        }

        PriorityEntry<?> minimum = priorityHeap.get(0);
        PriorityEntry<?> last = priorityHeap.remove(priorityHeap.size() - 1);
        if (!priorityHeap.isEmpty()) {
            priorityHeap.set(0, last);
            int index = 0;
            while (true) {
                int leftChild = 2 * index + 1;
                int rightChild = leftChild + 1;
                int smallerChild = index;

                if (leftChild < priorityHeap.size()
                        && priorityHeap.get(leftChild).priority < priorityHeap.get(smallerChild).priority) {
                    smallerChild = leftChild;
                }
                if (rightChild < priorityHeap.size()
                        && priorityHeap.get(rightChild).priority < priorityHeap.get(smallerChild).priority) {
                    smallerChild = rightChild;
                }
                if (smallerChild == index) {
                    break;
                }
                swapPriorityEntries(index, smallerChild);
                index = smallerChild;
            }
        }
        return minimum;
    }

    public boolean isPriorityEmpty() {
        return priorityHeap.isEmpty();
    }

    private void swapPriorityEntries(int first, int second) {
        PriorityEntry<?> temporary = priorityHeap.get(first);
        priorityHeap.set(first, priorityHeap.get(second));
        priorityHeap.set(second, temporary);
    }
}