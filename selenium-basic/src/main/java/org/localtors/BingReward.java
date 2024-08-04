package org.localtors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.IntStream;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BingReward {

  public static void main(String[] args) {
    WebDriver webDriver = new EdgeDriver();
    webDriver.get("https://login.live.com/");
    webDriver.manage()
        .timeouts()
        .implicitlyWait(Duration.ofSeconds(5));
    try {

      webDriver.findElement(By.xpath("//input[@id='i0116']"))
          .sendKeys(args[0], Keys.ENTER);

      // Chờ cho trường nhập password xuất hiện
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
      WebElement passwordField = wait.until(
          ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));

      // Nhập password và nhấn Enter
      passwordField.sendKeys(args[1], Keys.ENTER);

      // Chờ cho trang "Stay signed in?" xuất hiện bằng cách tìm văn bản
      WebElement staySignedInTitle = wait.until(
          ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//div[text()='Stay signed in?']")));

// Kiểm tra nếu có chữ "Stay signed in?" và chọn "Yes"
      if (staySignedInTitle != null) {
        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@type='submit' and text()='Yes']")));
        yesButton.click();
      }

      // Mở tab mới và điều hướng đến bing.com
      IntStream.range(1,3)
          .forEach(value -> {
            ((JavascriptExecutor) webDriver).executeScript("window.open('about:blank', '_blank');");
            ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
            webDriver.switchTo().window(tabs.get(value));
            webDriver.get("https://www.bing.com");

            webDriver.findElement(By.id("sb_form_q")).sendKeys("hello from selenium");
            webDriver.findElement(By.id("sb_form_q")).sendKeys(Keys.ENTER);
          });


      // Thêm các bước kiểm tra tiếp theo nếu cần thiết
    } finally {
      // Đóng trình duyệt
//      webDriver.quit();
    }
  }


}
