package org.localtors;

import com.apptasticsoftware.rssreader.Item;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.localtors.GoogleTrends.GEO;
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

      // Chờ trường nhập mật khẩu xuất hiện, hoặc xử lý xác nhận mã nếu cần
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));

      // Kiểm tra xem có phải là màn hình xác nhận mã không
      boolean isAuthenticatorScreen = false;
      try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[text()='Check your Authenticator app']")));
        isAuthenticatorScreen = true; // Đây là giả định, thay đổi idOfAuthenticatorField với id thật
      } catch (Exception e) {
        // Màn hình xác nhận mã không xuất hiện, có thể là lỗi khác
        System.out.println(
            "Exception Màn hình xác nhận mã không xuất hiện, có thể là lỗi khác = " + e);
      }

      if (isAuthenticatorScreen == false) {
        // Chờ cho trường nhập password xuất hiện
        WebElement passwordField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));

        // Nhập password và nhấn Enter
        passwordField.sendKeys(args[1], Keys.ENTER);
      }

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

      dailyTask(webDriver);
//      searchBing(webDriver);
      TimeUnit.SECONDS.sleep(30);

      // Thêm các bước kiểm tra tiếp theo nếu cần thiết
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      // Đóng trình duyệt
      webDriver.quit();
    }
  }

  public static boolean isNumeric(String str) {
    if (str == null) return false;
    final String[] split = str.split("\n");

    return Optional.of(split[0]).filter(
        s -> s.matches("\\d+")
    ).isPresent();
  }

  private static void dailyTask(WebDriver webDriver) {
    System.out.println("dailyTask start =====> ");
    webDriver.get("https://rewards.bing.com/");
    webDriver.findElements(By.xpath("//div[@class='c-card-content']"))
        .stream()
        .filter(webElement -> isNumeric(webElement.getText()))
        .forEach(webElement -> {
      System.out.println("webElement.getText() = " + webElement.getText());
      final WebElement element = webElement.findElement(By.xpath("(//span[@aria-label='plus'])"));
      element.click();

      webElement.click();
    });
  }

  private static void searchBing(WebDriver webDriver) {
    System.out.println("searchBing ========> ");
    // Mở tab mới và điều hướng đến bing.com
    GoogleTrends.get(GEO.JP)
        .stream()
        .map(Item::getTitle)
        .filter(Optional::isPresent)
        .forEach(item -> {
          ((JavascriptExecutor) webDriver).executeScript("window.open('about:blank', '_blank');");
          ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
          webDriver.switchTo()
              .window(tabs.get(1));
          webDriver.get("https://www.bing.com");

          webDriver.findElement(By.id("sb_form_q"))
              .sendKeys(item.get(), Keys.ENTER);
//             Chờ 5 giây sử dụng WebDriverWait
          new WebDriverWait(webDriver, Duration.ofSeconds(5))
              .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
          try {
            TimeUnit.SECONDS.sleep(7);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          } finally {
            // Đóng tab mới
            webDriver.close();

            // Chuyển lại tab gốc
            webDriver.switchTo()
                .window(tabs.get(0));
          }


        });
  }


}
