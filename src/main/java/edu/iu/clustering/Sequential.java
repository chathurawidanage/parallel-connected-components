package edu.iu.clustering;

import java.io.File;

public class Sequential {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        NodePayload payload;

        if (args.length == 1) {
            payload = GraphBuilder.buildGraphWithEdgeList(args[0]);
        } else if (args.length == 2) {
            String dataStructure = args[1];
            if (dataStructure.equals("csr")) {
                payload = GraphBuilder.buildGraphWithCSR(args[0]);
            } else if (dataStructure.equals("adj")) {
                payload = GraphBuilder.buildGraphWithAdjMatrix(args[0]);
            } else {
                payload = GraphBuilder.buildGraphWithEdgeList(args[0]);
            }

        } else {
            File currentDirFile = new File(".");
            String root = currentDirFile.getAbsolutePath();
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/com-amazon.ungraph.txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora.txt");
            //payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small.txt");
            payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/livejournal/com-lj.ungraph.txt");
        }
        payload.compute(0);

        Utils.printStats(payload);
        System.out.println("Time : " + (System.currentTimeMillis() - t1));
    }
}
