package edu.iu.clustering.executors;

import edu.iu.clustering.Executor;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

public class CSRBasedExecutor implements Executor {
    private int[] offset;
    private int[] edgeList;

    public CSRBasedExecutor(int[] offset, int[] edgeList) {
        this.offset = offset;
        this.edgeList = edgeList;
    }

    @Override
    public Set<Integer> runBFS(int numOFVertices, int start) {
        boolean[] visited = new boolean[numOFVertices];
        Arrays.fill(visited, false);
        LinkedList<Integer> q = new LinkedList<>();
        q.add(start);
        // Set source as visited
        visited[start] = true;

        while (q.size() != 0) {

            int s = q.poll();

            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            int startIndex = offset[s];
            int endIndex = -1;
            if (s == offset.length-1) {
                endIndex = edgeList.length - offset[s] -1;
            }
            else {
                endIndex = offset[s + 1];
            }


            for (int i = startIndex; i < endIndex; i++) {
                int n = edgeList[i];
                if (!visited[n]) {
                    visited[n] = true;
                    q.add(n);
                }
            }
        }
        // todo change
        return Collections.emptySet();
    }
}
