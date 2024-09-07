package org.localtors;

import static org.utils.DataUtils.isNumeric;
import static org.utils.DataUtils.toNumeric;

import com.apptasticsoftware.rssreader.Item;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import org.utils.Account;

public class BingReward {

  public static void main(String[] args) {
    final Account account = new Account(args);
    WebDriver webDriver = new EdgeDriver();
    webDriver.get("https://login.live.com/");
    webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    try {

      webDriver
          .findElement(By.xpath("//input[@placeholder='Email, phone, or Skype']"))
          .sendKeys(account.getUsername(), Keys.ENTER);

      // Chờ trường nhập mật khẩu xuất hiện, hoặc xử lý xác nhận mã nếu cần
      WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));

      // Kiểm tra xem có phải là màn hình xác nhận mã không
      if (account.isNeedAuthenticator()) {
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[text()='Check your Authenticator app']")));
      } else {
        // Chờ cho trường nhập password xuất hiện
        WebElement passwordField =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//input[@placeholder='Password']")));

        // Nhập password và nhấn Enter
        passwordField.sendKeys(account.getPassword(), Keys.ENTER);
      }

      // Chờ cho trang "Stay signed in?" xuất hiện bằng cách tìm văn bản
      WebElement staySignedInTitle =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.xpath("//div[text()='Stay signed in?']")));

      // Kiểm tra nếu có chữ "Stay signed in?" và chọn "Yes"
      if (staySignedInTitle != null) {
        WebElement yesButton =
            wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@type='submit' and text()='Yes']")));
        yesButton.click();
      }

      try {
        //        dailyTask(webDriver);
      } catch (Exception e) {
        System.out.println("error in dailyTask = " + e);
      }
      searchBing(webDriver, 90);
      TimeUnit.SECONDS.sleep(30);

      // Thêm các bước kiểm tra tiếp theo nếu cần thiết
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      // Đóng trình duyệt
      webDriver.quit();
    }
  }

  private static void dailyTask(WebDriver webDriver) {
    System.out.println("<===== dailyTask start =====> ");
    webDriver.get("https://rewards.bing.com/");
    //    ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
    String tab0 = webDriver.getWindowHandle();
    System.out.println("tab0 = " + tab0);
    //    WebDriver.

    final List<WebElement> elements =
        webDriver.findElements(By.xpath("//div[@class='c-card-content']"));
    System.out.println("elements = " + elements);

    elements.stream()
        .filter(WebElement::isDisplayed)
        .filter(webElement -> isNumeric(webElement.getText()))
        .filter(webElement -> 15 > toNumeric(webElement.getText()))
        .forEach(
            webElement -> {
              System.out.println(
                  "webElement.getText() = "
                      + toNumeric(webElement.getText())
                      + webElement.getText());
              webElement.click();
              ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
              if (tabs.size() > 1) {
                sleep(
                    0,
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
    final List<Item> list = GoogleTrends.getAll(GEO.values());
    Collections.shuffle(list);
    list.stream()
        .filter(item -> atomicInteger.getAndIncrement() < points / 3)
        .map(Item::getTitle)
        .filter(Optional::isPresent)
        .forEach(item -> search(webDriver, item.get()));
    System.out.println("<===== searchBing end =====> ");
  }

  private static void search(WebDriver webDriver, String keyWord) {
    System.out.println("search() called with: [" + keyWord + "]");
    ((JavascriptExecutor) webDriver)
        .executeScript("window.open('about:blank', '_blank');");
    ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
    webDriver.switchTo().window(tabs.get(1));
    webDriver.get("https://www.bing.com");

    sleep(1, () -> {});
    final String today = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd/MM"));
    webDriver
        .findElement(By.id("sb_form_q"))
        .sendKeys(
            keyWord,
            Keys.ENTER);
    //             Chờ 5 giây sử dụng WebDriverWait
    new WebDriverWait(webDriver, Duration.ofSeconds(5))
        .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    sleep(7, () -> closeTabAndGoBack(webDriver, tabs));
  }

  private static void closeTabAndGoBack(WebDriver webDriver, ArrayList<String> tabs) {
    if (tabs.size() <= 1) {
      return;
    }
    webDriver.close();

    // Chuyển lại tab gốc
    webDriver.switchTo().window(tabs.get(0));
  }

  static void sleep(int seconds, Runnable finallyRun) {
    System.out.println("sleep seconds = " + seconds);
    try {
      TimeUnit.SECONDS.sleep(seconds);
      System.out.println("sleep done seconds = " + seconds);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      finallyRun.run();
    }
  }
}
