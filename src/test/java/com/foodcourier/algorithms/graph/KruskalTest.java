package com.foodcourier.algorithms.graph;

// Commented out until AdjacencyListGraph is merged by the team
// import com.foodcourier.dsa.graph.AdjacencyListGraph;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectNumberOfEdges() {
        /*
        TODO: Uncomment once the AdjacencyListGraph class is pushed to the repository.
        
        AdjacencyListGraph<Location> graph = AdjacencyListGraph.loadDefaultGraph();
        List<AdjacencyListGraph.WeightedEdge<Location>> mst = Kruskal.minimumSpanningTree(graph);
        assertEquals(graph.vertexCount() - 1, mst.size(), "MST should have V-1 edges");
        
        double totalWeight = mst.stream()
                               .mapToDouble(AdjacencyListGraph.WeightedEdge::getWeight)
                               .sum();
        assertTrue(totalWeight > 0, "Total MST weight should be positive");
        */
    }
}


// Commented out until AdjacencyListGraph is merged by the team
// import com.foodcourier.dsa.graph.AdjacencyListGraph;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectNumberOfEdges() {
        /*
        TODO: Uncomment once the AdjacencyListGraph class is pushed to the repository.
        
        AdjacencyListGraph<Location> graph = AdjacencyListGraph.loadDefaultGraph();
        List<AdjacencyListGraph.WeightedEdge<Location>> mst = Kruskal.minimumSpanningTree(graph);
        assertEquals(graph.vertexCount() - 1, mst.size(), "MST should have V-1 edges");
        
        double totalWeight = mst.stream()
                               .mapToDouble(AdjacencyListGraph.WeightedEdge::getWeight)
                               .sum();
        assertTrue(totalWeight > 0, "Total MST weight should be positive");
        */
    }
}


// Commented out until AdjacencyListGraph is merged by the team
// import com.foodcourier.dsa.graph.AdjacencyListGraph;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectNumberOfEdges() {
        /*
        TODO: Uncomment once the AdjacencyListGraph class is pushed to the repository.
        
        AdjacencyListGraph<Location> graph = AdjacencyListGraph.loadDefaultGraph();
        List<AdjacencyListGraph.WeightedEdge<Location>> mst = Kruskal.minimumSpanningTree(graph);
        assertEquals(graph.vertexCount() - 1, mst.size(), "MST should have V-1 edges");
        
        double totalWeight = mst.stream()
                               .mapToDouble(AdjacencyListGraph.WeightedEdge::getWeight)
                               .sum();
        assertTrue(totalWeight > 0, "Total MST weight should be positive");
        */
    }
}


// Commented out until AdjacencyListGraph is merged by the team
// import com.foodcourier.dsa.graph.AdjacencyListGraph;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectNumberOfEdges() {
        /*
        TODO: Uncomment once the AdjacencyListGraph class is pushed to the repository.
        
        AdjacencyListGraph<Location> graph = AdjacencyListGraph.loadDefaultGraph();
        List<AdjacencyListGraph.WeightedEdge<Location>> mst = Kruskal.minimumSpanningTree(graph);
        assertEquals(graph.vertexCount() - 1, mst.size(), "MST should have V-1 edges");
        
        double totalWeight = mst.stream()
                                .mapToDouble(AdjacencyListGraph.WeightedEdge::getWeight)
                                .sum();
        assertTrue(totalWeight > 0, "Total MST weight should be positive");
        */
    }
}


import com.foodcourier.dsa.graph.AdjacencyListGraph;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Kruskal}.
 */
public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectNumberOfEdges() {
        // Load the default graph (uses data/seed CSV files)
        AdjacencyListGraph<Location> graph = AdjacencyListGraph.loadDefaultGraph();

        // Compute MST using Kruskal
        List<AdjacencyListGraph.WeightedEdge<Location>> mst = Kruskal.minimumSpanningTree(graph);

        // For a connected undirected graph the MST should contain (V - 1) edges
        assertEquals(graph.vertexCount() - 1, mst.size(), "MST should have V-1 edges");

        // Verify total weight is positive (basic sanity check)
        double totalWeight = mst.stream()
                               .mapToDouble(AdjacencyListGraph.WeightedEdge::getWeight)
                               .sum();
        assertTrue(totalWeight > 0, "Total MST weight should be positive");
    }
}
