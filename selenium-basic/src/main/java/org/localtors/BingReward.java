package org.localtors;

import com.apptasticsoftware.rssreader.Item;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
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

      webDriver.findElement(By.xpath("//input[@placeholder='Email, phone, or Skype']"))
          .sendKeys(args[0], Keys.ENTER);

      // Chờ trường nhập mật khẩu xuất hiện, hoặc xử lý xác nhận mã nếu cần
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));

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
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Password']")));

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

      try{
//        dailyTask(webDriver);
      } catch (Exception e){
        System.out.println("error in dailyTask = " + e);
      }
      searchBing(webDriver, 120);
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

  public static Integer toNumeric(String str) {
    if (str == null) return 0;
    final String[] split = str.split("\n");

    return Optional.of(split[0]).filter(
        s -> s.matches("\\d+")
    ).map(Integer::parseInt).get();
  }

  private static void dailyTask(WebDriver webDriver) {
    System.out.println("<===== dailyTask start =====> ");
    webDriver.get("https://rewards.bing.com/");
//    ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
    String tab0 = webDriver.getWindowHandle();
    System.out.println("tab0 = " + tab0);
//    WebDriver.

    final List<WebElement> elements = webDriver.findElements(
        By.xpath("//div[@class='c-card-content']"));
    System.out.println("elements = " + elements);

    elements
        .stream()
        .filter(WebElement::isDisplayed)
        .filter(webElement -> isNumeric(webElement.getText()))
        .filter(webElement -> 15 > toNumeric(webElement.getText()))
        .forEach(webElement -> {
      System.out.println("webElement.getText() = " + toNumeric(webElement.getText()) + webElement.getText());
      webElement.click();
          ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
          if (tabs.size() > 1){sleep(0,
              () -> {
            System.out.println("dailyTask after sleep = " + tabs);
            webDriver.switchTo().window(tab0);
              });
          }
    });
    System.out.println("<===== dailyTask end =====> ");
  }

  private static void searchBing(WebDriver webDriver, int points) {
    System.out.println("<===== searchBing ========> ");
    // Mở tab mới và điều hướng đến bing.com
    AtomicInteger atomicInteger = new AtomicInteger(0);
    GoogleTrends.getAll(GEO.values())
        .stream()
        .filter(item -> atomicInteger.getAndIncrement() < points/3)
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

          sleep(7, () -> closeTabAndGoBack(webDriver, tabs));
        });
    System.out.println("<===== searchBing end =====> ");
  }

  private static void closeTabAndGoBack(WebDriver webDriver, ArrayList<String> tabs) {
    if (tabs.size() <= 1){
      return;
    }
    webDriver.close();

    // Chuyển lại tab gốc
    webDriver.switchTo()
        .window(tabs.get(0));
  }

  static void sleep(int seconds, Runnable finallyRun){
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      finallyRun.run();
    }
  }


}
