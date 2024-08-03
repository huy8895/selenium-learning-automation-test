package org.localtors;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class LocatorEx {

  public static void main(String[] args) {
    WebDriver webDriver= new EdgeDriver();
    webDriver.get("https://www.bing.com/");
    webDriver.findElement(By.id("sb_form_q")).sendKeys("hello from selenium");
    webDriver.findElement(By.id("sb_form_q")).sendKeys(Keys.ENTER);
    webDriver.findElement(By.id("sb_form_go")).click();

    webDriver.close();
  }

}
