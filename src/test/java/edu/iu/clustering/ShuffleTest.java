package edu.iu.clustering;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ShuffleTest {

  @Test
  public void tagTest() {
    Shuffle shuffle = new Shuffle();
    int tag = shuffle.getTag(1, 1);
    Assert.assertEquals(tag, 65537);

    Map<String, Integer> tags = new HashMap<>();

    // make sure no tag collisions
    for (int i = 0; i < 127; i++) {
      for (int j = 0; j < 127; j++) {
        String key = Math.min(i, j) + "-" + Math.max(i, j);
        tag = shuffle.getTag(i, j);
        Assert.assertTrue(!tags.containsKey(key) || tags.get(key) == tag);

        tags.put(key, tag);
      }
    }
  }
}