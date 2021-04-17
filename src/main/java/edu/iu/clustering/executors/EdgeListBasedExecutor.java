package edu.iu.clustering.executors;

import edu.iu.clustering.Executor;

import java.util.*;

public class EdgeListBasedExecutor implements Executor {

    private final Map<Integer, Set<Integer>> edgeList;
    private final Map<Integer, Integer> indexMap;
    private int[] nodes;

    public EdgeListBasedExecutor(int[] nodes, Map<Integer, Set<Integer>> edgeList) {
        this.nodes = nodes;
        this.indexMap = new HashMap<>();
        int index = 0;
        for (int node : nodes) {
            indexMap.put(node, index++);
        }
        this.edgeList = edgeList;
    }

    @Override
    public Set<Integer> runBFS(int numOFVertices, int startIndex) {
        Set<Integer> visited = new HashSet<>();

        // this q will always keep the real node value. Not the index
        LinkedList<Integer> q = new LinkedList<>();

        // convert to the value and insert
        q.add(nodes[startIndex]);

        // Set source as visited
        visited.add(startIndex);

        int vis;
        while (!q.isEmpty()) {
            vis = q.poll();

            // For every adjacent vertex to
            // the current vertex
            for (int adj : edgeList.getOrDefault(vis, Collections.emptySet())) {
                int index = indexMap.get(adj);
                if (!visited.contains(index)) {

                    // Push the adjacent node to
                    // the queue
                    q.add(adj);

                    // Set
                    visited.add(index);
                }
            }
        }
        return visited;
    }
}
