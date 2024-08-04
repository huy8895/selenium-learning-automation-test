package org.localtors;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class GoogleTrends {
  public enum GEO {
    VN, US, JP
  }
  public static List<Item> get(GEO geo) {
    RssReader rssReader = new RssReader();
    List<Item> items = null;
    try {
      items = rssReader.read(
              "https://trends.google.com/trends/trendingsearches/daily/rss?geo="+ geo)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("error GoogleTrends get()" + e.getMessage());
    }

    return items;
  }
}
