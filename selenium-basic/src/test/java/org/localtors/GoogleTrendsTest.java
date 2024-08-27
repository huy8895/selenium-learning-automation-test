package org.localtors;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import org.localtors.GoogleTrends.GEO;

class GoogleTrendsTest {

  @org.junit.jupiter.api.Test
  void get() {
    final AtomicInteger integer = new AtomicInteger();
    GoogleTrends.getAll(GEO.values()).stream()
        .peek(item -> System.out.println("index = " + integer.getAndIncrement()))
        .forEach(item -> System.out.println("item = " + item.getTitle()));
    }

  @org.junit.jupiter.api.Test
  void getAll() {
    }
}