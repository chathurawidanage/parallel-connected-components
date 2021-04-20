package edu.iu.clustering;

import java.util.ArrayList;
import java.util.List;

public class Node {
    int myIndex;
    int value;
    List<Node> arrayList = new ArrayList<>();

    public int getMyIndex() {
        return myIndex;
    }

    public void setMyIndex(int myIndex) {
        this.myIndex = myIndex;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Node(int myIndex, int value) {
        this.myIndex = myIndex;
        this.value = value;
    }

    public List<Node> getArrayList() {
        return arrayList;
    }

    public void setArrayList(List<Node> arrayList) {
        this.arrayList = arrayList;
    }

    public void addArrayList(Node node) {
        arrayList.add(node);
    }
}
