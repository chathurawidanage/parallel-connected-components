package edu.iu.clustering.executors;

import edu.iu.clustering.Executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdjMatrixBasedExecutor implements Executor {

    private final int[][] graph;

    public AdjMatrixBasedExecutor(int[][] graph) {
        this.graph = graph;
    }

    @Override
    public boolean[] runBFS(int numOFVertices, int start) {
        boolean[] visited = new boolean[numOFVertices];
        Arrays.fill(visited, false);
        List<Integer> q = new ArrayList<>();
        q.add(start);
        // Set source as visited
        visited[start] = true;

        int vis;
        while (!q.isEmpty()) {
            vis = q.get(0);

            // Print the current node
            q.remove(q.get(0));

            // For every adjacent vertex to
            // the current vertex
            for (int i = 0; i < numOFVertices; i++) {
                if (this.graph[vis][i] == 1 && (!visited[i])) {

                    // Push the adjacent node to
                    // the queue
                    q.add(i);

                    // Set
                    visited[i] = true;
                }
            }
        }
        return visited;
    }
}
