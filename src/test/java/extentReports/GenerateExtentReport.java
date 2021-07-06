package extentReports;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class GenerateExtentReport {

	//creating driver object
	private static WebDriver driver = null;
	//setting expected title from the ebay.com/Daraz.lk
	static String expectedTitleebay ="Electronics, Cars, Fashion, Collectibles & More | eBay";
	static String expectedTitledaraz ="Online Shopping Sri Lanka: Clothes, Electronics & Phones | Daraz.lk";

	//create the htmlReporter object 
	ExtentSparkReporter htmlReporter;
	ExtentReports extent;
	ExtentTest test;
	private Object extentReports;

	@BeforeSuite
	public void setup() throws IOException {

		htmlReporter = new ExtentSparkReporter(System.getProperty("user.dir")+"/test-output/extentReport.html");
		htmlReporter.loadXMLConfig(System.getProperty("user.dir")+ "/extent-config.xml");

		//create ExtentReports and attach reporter(s)
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);

		extent.setSystemInfo("Host name","localhost");
		extent.setSystemInfo("OS","Windows10");
		extent.setSystemInfo("Environemnt","QA");
		extent.setSystemInfo("Browser","chrome");
		extent.setSystemInfo("user","manul");

		/*
			htmlReporter = new ExtentSparkReporter("extentReport.html");
		 */

		//initializing and starting the browser
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();

	}

	@Test (priority = 0)
	public void test1() throws InterruptedException {

		//creates a toggle for the given test, add all log events under it
		test = extent.createTest("ebay Search Test", "test to validate search box ");

		test.log(Status.INFO, "Starting test case");

		//maximize the window 
		driver.manage().window().maximize();
		test.pass("maximize has done");

		//Navigate to Ebay.com
		driver.get("https://www.ebay.com");
		Thread.sleep(1000);
		test.pass("Navigate to Ebay.com");

		//compare whether the title id matching
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitleebay);
		test.pass("title is correct");

		//enter in the TextBox
		driver.findElement(By.xpath("//*[@id=\"gh-ac\"]")).sendKeys("Mobile");
		test.pass("Entered text in the text box");
		//hit enter
		driver.findElement(By.xpath("//*[@id=\"gh-btn\"]")).sendKeys(Keys.RETURN);	
		test.pass("Press keybopard enter key");

		test.info("test completed");

	}

	@Test (priority = 1)
	public void test2() throws InterruptedException {

		//creates a toggle for the given test, add all log events under it
		test = extent.createTest("Daraz Search Test", "test to validate search box ");

		test.log(Status.INFO, "Starting test case");

		//maximize the window 
		driver.manage().window().maximize();
		test.pass("maximize has done");

		//Navigate to daraz.lk
		driver.get("https://www.daraz.lk/#");
		Thread.sleep(1000);
		test.pass("Navigate to Daraz.lk");

		//compare whether the title id matching
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitledaraz);
		test.pass("title is correct");

		//enter in the TextBox
		driver.findElement(By.xpath("//*[@id=\"q\"]")).sendKeys("Mobile");
		test.pass("Entered text in the text box");
		//hit enter
		driver.findElement(By.xpath("//*[@id=\"topActionHeader\"]/div/div[2]/div/div[2]/form/div/div[2]/button")).sendKeys(Keys.RETURN);	
		test.pass("Press keybopard enter key");

		test.info("test completed");

	}

	@Test (priority = 2)
	public void test3() throws InterruptedException {

		//creates a toggle for the given test, add all log events under it
		test = extent.createTest("Daraz Search Test", "Forcefully failed ");

		test.log(Status.INFO, "Starting test case");

		//maximize the window 
		driver.manage().window().maximize();
		test.pass("maximize has done");

		//Navigate to daraz.lk
		driver.get("https://www.daraz.lk/#");
		Thread.sleep(1000);
		test.pass("Navigate to Daraz.lk");

		//compare whether the title id matching
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitledaraz);
		test.pass("title is correct");

		//enter in the TextBox
		driver.findElement(By.xpath("//*[@id=\"q\"]")).sendKeys("Mobile");
		test.pass("Entered text in the text box");
		//hit enter
		driver.findElement(By.xpath("invalidXPath")).sendKeys(Keys.RETURN);	
		test.pass("Press keybopard enter key");

		test.info("test completed");
		//test.fail("test failed");

	}

	@AfterMethod
	public void getResult(ITestResult result) throws IOException {

		if (result.getStatus()==ITestResult.FAILURE) {

			test.log(Status.FAIL, "Test case Failed : " + result.getName());
			test.log(Status.FAIL, "Test case Failed : " + result.getThrowable());

			String screenshotPath = GenerateExtentReport.getScreenShot(driver, result.getName());
			test.addScreenCaptureFromPath(screenshotPath);// adding screen shot

		}else if (result.getStatus() == ITestResult.SKIP) {
			test.log(Status.SKIP, "Test Case SKIPPED IS " + result.getName());
		}
		else if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, "Test Case PASSED IS " + result.getName());
		}
	}

	@AfterSuite
	public void tearDown() {

		driver.quit();

		//write results into the file
		extent.flush();

	}

	public static String getScreenShot(WebDriver driver, String screenShotName) throws IOException {

		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);

		//after execution you will see a folder "FaiiedTestsScreenshots" under src folder
		String destination = System.getProperty("user.dir")+"/Screnshots/" + screenShotName + dateName + ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;

	}

}
