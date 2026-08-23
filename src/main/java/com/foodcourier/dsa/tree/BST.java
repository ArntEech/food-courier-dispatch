package com.foodcourier.dsa.tree;

public class BST<E extends Comparable<E>> implements BSTInterface<E> {

    private Node<E> root;
    private int size;

    private static class Node<E> {
        E data;
        Node<E> left;
        Node<E> right;

        Node(E data) {
            this.data = data;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void insert(E item) {
        root = insertRecursive(root, item);
    }

    private Node<E> insertRecursive(Node<E> node, E item) {
        if (node == null) {
            size++;
            return new Node<>(item);
        }

        int comparison = item.compareTo(node.data);

        if (comparison < 0) {
            node.left = insertRecursive(node.left, item);
        } else if (comparison > 0) {
            node.right = insertRecursive(node.right, item);
        }

        return node;
    }

    @Override
    public boolean contains(E item) {
        return search(item) != null;
    }

    @Override
    public E search(E item) {
        Node<E> current = root;

        while (current != null) {
            int comparison = item.compareTo(current.data);

            if (comparison == 0) {
                return current.data;
            }

            if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    @Override
    public void remove(E item) {
        if (contains(item)) {
            root = removeRecursive(root, item);
            size--;
        }
    }

    private Node<E> removeRecursive(Node<E> node, E item) {
        if (node == null) {
            return null;
        }

        int comparison = item.compareTo(node.data);

        if (comparison < 0) {
            node.left = removeRecursive(node.left, item);
        } else if (comparison > 0) {
            node.right = removeRecursive(node.right, item);
        } else {
            if (node.left == null) {
                return node.right;
            }

            if (node.right == null) {
                return node.left;
            }

            Node<E> smallest = findSmallest(node.right);
            node.data = smallest.data;
            node.right = removeRecursive(node.right, smallest.data);
        }

        return node;
    }

    private Node<E> findSmallest(Node<E> node) {
        while (node.left != null) {
            node = node.left;
        }

        return node;
    }
}
