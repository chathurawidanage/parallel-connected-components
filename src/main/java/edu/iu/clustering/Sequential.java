package edu.iu.clustering;

import java.io.File;

public class Sequential {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        File currentDirFile = new File(".");
        String root = currentDirFile.getAbsolutePath();
//        NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/amazon/com-amazon.ungraph.txt");
        //NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/cora/cora.txt");
        NodePayload payload = GraphBuilder.buildGraphWithEdgeList(root + "/src/main/resources/data/small/small.txt");
        payload.compute(0);

        Utils.printStats(payload);
        System.out.println("Time : " + (System.currentTimeMillis() - t1));
    }
}
