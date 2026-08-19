package com.foodcourier.dsa.heap;

import com.foodcourier.domain.Order;
import java.util.ArrayList;

public class BinaryHeap implements HeapInterface<Order> {

    private ArrayList<Order> heap;

    public BinaryHeap() {
        heap = new ArrayList<>();
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
}