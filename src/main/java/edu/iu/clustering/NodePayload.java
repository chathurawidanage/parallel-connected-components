package edu.iu.clustering;

public class NodePayload {

  private int[] nodes;
  private int[] clusters;

  public NodePayload(int[] nodes, int[] clusters) {
    this.nodes = nodes;
    this.clusters = clusters;
  }

  public int[] getNodes() {
    return nodes;
  }

  public int[] getClusters() {
    return clusters;
  }
}
