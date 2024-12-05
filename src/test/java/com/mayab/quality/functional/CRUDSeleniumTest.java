package com.mayab.quality.functional;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class CRUDSeleniumTest {

    private static WebDriver driver;
    private WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeEach
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
    
        ChromeOptions options = new ChromeOptions();
        
        URL seleniumUrl = new URL("http://localhost:4444/wd/hub");
        
        driver = new RemoteWebDriver(seleniumUrl, options);
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        js = (JavascriptExecutor) driver;
    }

    public void createUser() throws Exception {
        driver.get("https://mern-crud-mpfr.onrender.com/");
        driver.findElement(By.xpath("//button[text()='Add New']")).click();

        driver.findElement(By.name("name")).sendKeys("Pacotest");
        driver.findElement(By.name("email")).sendKeys("paco@test.com");
        driver.findElement(By.name("age")).sendKeys("20");
        driver.findElement(By.xpath("//div[contains(@class, 'ui selection dropdown')]")).click();
        driver.findElement(By.xpath("//div[@role='option']/span[text()='Male']")).click();

        driver.findElement(By.xpath("//button[text()='Add']")).click();
    }

    public void deleteUser() {
        driver.get("https://mern-crud-mpfr.onrender.com/");
        driver.findElement(By.xpath("//td[text()='Pacotest']/following-sibling::td/button[contains(@class, 'black') and contains(@class, 'button')]")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'red') and contains(@class, 'button') and @data-userid]")));
        driver.findElement(By.xpath("//button[contains(@class, 'red') and contains(@class, 'button') and @data-userid]")).click();
    }

    public boolean findUser() {
        List<WebElement> rows = driver.findElements(By.xpath("//table[@class='ui single line table']//tbody//tr"));
        boolean userFound = false;

        for (WebElement row : rows) {
            String name = row.findElement(By.xpath(".//td[1]")).getText();
            if (name.equals("Pacotest")) {
                userFound = true;
                break;
            }
        }
        return userFound;
    }

    public static void takeScreenshot(String fileName) throws IOException {
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(file, new File("src/screenshots/" + fileName + ".jpeg"));
    }

    @Test
    public void test1_CreateUser_Success() throws Exception {
        createUser();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui green success message')]//p")));

        WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'ui green success message')]//p"));
        takeScreenshot("createSuccess");
        assertThat(successMessage.getText(), is("Successfully added!"));
    }

    @Test
    public void test2_CreateUser_Duplicate() throws Exception {
        createUser();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui yellow warning message')]//p")));

        WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'ui yellow warning message')]//p"));
        takeScreenshot("createDuplicate");
        assertThat(successMessage.getText(), is("That email is already taken."));
    }

    @Test
    public void test3_UpdateUser_Age() throws Exception {
        driver.get("https://mern-crud-mpfr.onrender.com/");
        driver.findElement(By.xpath("//td[text()='Pacotest']/following-sibling::td/button[contains(@class, 'blue') and contains(@class, 'button')]")).click();

        wait.until(ExpectedConditions.attributeToBe(driver.findElement(By.name("age")), "value", "20"));

        WebElement ageField = driver.findElement(By.name("age"));
        ageField.clear();
        ageField.sendKeys("22");

        driver.findElement(By.xpath("//button[text()='Save']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'ui green success message')]//p")));

        WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'ui green success message')]//p"));
        takeScreenshot("updateAge");
        assertThat(successMessage.getText(), is("Successfully updated!"));
    }

    @Test
    public void test4_DeleteUser() throws Exception {
        deleteUser();
        Thread.sleep(1000);

        List<WebElement> rows = driver.findElements(By.xpath("//table[@class='ui single line table']//tbody//tr"));
        boolean userNotFound = true;

        for (WebElement row : rows) {
            String name = row.findElement(By.xpath(".//td[1]")).getText();
            if (name.equals("Pacotest")) {
                userNotFound = false;
                break;
            }
        }

        takeScreenshot("deleteSuccess");
        assertThat(userNotFound, is(true));
    }

    @Test
    public void test5_Find_ByName() throws Exception {
        driver.get("https://mern-crud-mpfr.onrender.com/");
        createUser();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui green success message')]//p")));

        boolean userFound = findUser();

        takeScreenshot("findByName");
        deleteUser();
        Thread.sleep(1000);

        assertThat(userFound, is(true));
    }

    @Test
    public void test6_Find_All() throws Exception {
        driver.get("https://mern-crud-mpfr.onrender.com/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='ui single line table']")));

        WebElement table = driver.findElement(By.xpath("//table[@class='ui single line table']"));
        takeScreenshot("findAll");
        
        assertThat(table.isDisplayed(), is(true));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
