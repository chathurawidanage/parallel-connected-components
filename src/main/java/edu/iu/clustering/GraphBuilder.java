package edu.iu.clustering;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphBuilder {

    private static final Logger LOG = Logger.getLogger(GraphBuilder.class.getName());

    public static NodePayload buildGraph(String filePath) {
        CSVReader reader = null;
        LinkedHashMap<Integer, ArrayList<Integer>> edgeList = new LinkedHashMap<>();
        try {
            reader = new CSVReader(new FileReader(filePath));
            String[] nextLine; //read one line at a time
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    String[] edge = token.split("\\s+");
                    int startNode = Integer.parseInt(edge[0].trim());
                    int endNode = Integer.parseInt(edge[1].trim());
                    edgeList.computeIfAbsent(startNode, list -> new ArrayList<>()).add(endNode);
                    edgeList.computeIfAbsent(endNode, list -> new ArrayList<>()).add(startNode);
                }
            }
            int[][] adjacencyMatrix = new int[edgeList.keySet().size()][edgeList.keySet().size()];
            List<Integer> listKeys = new ArrayList<>(edgeList.keySet());
            int[] nodes = listKeys.stream().mapToInt(key -> key).toArray();
            listKeys.forEach(key -> {
                ArrayList<Integer> list = edgeList.get(key);
                list.forEach(value -> {
                    adjacencyMatrix[listKeys.indexOf(key)][listKeys.indexOf(value)] = 1;
                });
            });
            return new NodePayload(nodes, new int[nodes.length], adjacencyMatrix);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to load the graph", e);
        }
        return null;
    }
}
