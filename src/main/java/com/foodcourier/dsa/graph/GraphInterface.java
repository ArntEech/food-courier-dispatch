package com.foodcourier.dsa.graph;

import java.util.List;

public interface GraphInterface<V> {

    void addVertex(V vertex);

    void addEdge(V from, V to);

    void addEdge(V from, V to, double weight);

    boolean containsVertex(V vertex);

    List<V> getNeighbors(V vertex);

    int vertexCount();

    int edgeCount();
}