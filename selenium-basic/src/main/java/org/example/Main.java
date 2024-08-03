package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class Main {

  public static void main(String[] args) {
//    System.setProperty("webdriver.chrome.driver",
//        "/Users/huytrinh/Work/udemy-resources/selenium/driver/chromedriver-mac-arm64/chromedriver");

    final WebDriver driver = new EdgeDriver();
    driver.get("https://www.bing.com");
    System.out.println("driver.getTitle() = " + driver.getTitle());

    driver.quit();

    
  }
}