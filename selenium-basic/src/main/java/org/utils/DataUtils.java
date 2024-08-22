package org.utils;

import java.util.Optional;

public class DataUtils {

  public static boolean isNumeric(String str) {
    if (str == null) return false;
    final String[] split = str.split("\n");

    return Optional.of(split[0]).filter(
        s -> s.matches("\\d+")
    ).isPresent();
  }

  public static Integer toNumeric(String str) {
    if (str == null) return 0;
    final String[] split = str.split("\n");

    return Optional.of(split[0]).filter(
        s -> s.matches("\\d+")
    ).map(Integer::parseInt).get();
  }

}
