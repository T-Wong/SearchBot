/*	Created by: Tyler Wong
 * 	Date: August 10th, 2014
 * 	Purpose: Selenium script that controls a FireFox browser to obtain	
 * 	all Bing Rewards points as possible per account.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Bing {

	// Constants used for timing in between searches and other variables
	private final int MIN_TIME = 10;
	private final int MAX_TIME = 30;
	private final int SEARCHES_PER = 2;	// The number of searches to earn 1 credit
	
    Random rand = new Random();
	
	// Holds the number of searches needed to be done for mobile and desktop. set to 40 just in case the website changes and needs to be defaulted
	private int desktopSearches = 40;
	private int mobileSearches = 40;
	
	String currentSearch;	// Type of search being done. ex. web, image, or video
	
	// Holds account info
    private Map<String, char[]> accounts = new LinkedHashMap<String, char[]>();
    
    // holds words from word list
    private Object[] wordArray;
    
    // Holds the titles of the earn and explore rewards needed to be clicked
	ArrayList<String> titles = new ArrayList<String>();

    // Declare variables for selenium
    private WebDriver driver;
    private String baseUrl;
    
    public Bing(Map<String, char[]> accounts, Object[] wordArray) {
    	this.accounts = accounts;
    	this.wordArray = wordArray;
    }
  
    // Execute whole script
    public void execute() {
        try {
            setUp();
            search();
        }
        catch(Exception e) {
//        	saveScreenshot("C:\\Users\\Tyler\\Desktop\\picture.png");
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(null, "An error has occured, stopping.\n\n" + e.getMessage(), "An error has occured", JOptionPane.ERROR_MESSAGE);
        	try {
        		tearDown();
        	}
        	catch(Exception e2) {}
        	return;	
        }
    }
    
    @Before
    public void setUp() {
        baseUrl = "http://www.bing.com/rewards/dashboard";
    }
    
    @Test
    public void search() throws Exception {
    	// Saves phantomjs.exe to a temp folder on local file system to be used later
    	InputStream in = null;
    	OutputStream out = null;
    	try {
	    	in = getClass().getResourceAsStream("phantomjs.exe");
	    	out = new FileOutputStream(System.getenv("APPDATA") + "/BingBot/phantomjs.exe");
	    	IOUtils.copy(in, out);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	finally {
    		IOUtils.closeQuietly(in);
    		IOUtils.closeQuietly(out);
    	}

    	for(Map.Entry<String, char[]> account : accounts.entrySet()) { 	    		
    		// Initialize new phantomjs driver (1.97) for normal web browser
    		DesiredCapabilities caps = new DesiredCapabilities();
    		caps.setCapability("takesScreenshot", true);
    		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, System.getenv("APPDATA") + "/BingBot/phantomjs.exe");
    		caps.setJavascriptEnabled(true);
    		driver = new PhantomJSDriver(caps);
    		driver.manage().window().setSize(new Dimension(1920, 1080));
    		
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            driver.get(baseUrl);
            
            // Navigate to sign in
            driver.findElement(By.className("identityOption")).findElement(By.linkText("Sign in")).click();
            
            // Login
            driver.findElement(By.name("login")).sendKeys(account.getKey());
            driver.findElement(By.name("passwd")).sendKeys(new String(account.getValue()));
            driver.findElement(By.id("idSIButton9")).click();
            
            if(!isElementPresent(By.className("tileset"))){	// Will stop and show an error dialog with which account is having problems
            	throw new Exception("Requires user interaction. Try logging into \"" + account.getKey() + "\"");
            }

            // Gets the number of searches needed to be done
            List<WebElement> searchList = (driver.findElements(By.className("tileset")).get(1)).findElements(By.tagName("li"));
            for(WebElement li : searchList) {
            	if(li.findElement(By.className("title")).getText().trim().equals("Mobile search")) {
            		mobileSearches = formatText(li.findElement(By.className("progress")).getText());
            	}
            	else if(li.findElement(By.className("title")).getText().trim().equals("PC search")) {
            		desktopSearches = formatText(li.findElement(By.className("progress")).getText());
            	}
            }

            // Earns the "earn and explore" rewards. 
        	WebElement ul = driver.findElement(By.className("tileset"));
 
            // Finds which elements need to be clicked
            List<WebElement> toBeClicked = ul.findElements(By.cssSelector("div[class='check open-check dashboard-sprite']"));
            for(WebElement reward : toBeClicked) {
            	titles.add(reward.findElement(By.xpath("./../..")).findElement(By.className("title")).getText());
            }

            // Clicks the rewards
            for(String title : titles) {
            	// finds the rewards html elements again to prevent stale elements
            	WebElement ul2 = driver.findElement(By.className("row"));
                List<WebElement> lis2 = ul2.findElements(By.tagName("li"));
                String base = driver.getWindowHandle();
                
                for(WebElement li2 : lis2) {
                	try {
	                	if(li2.findElement(By.className("title")).getText().equals(title)) {
	                		li2.findElement(By.className("title")).click();
	                	}
                	}
                	catch(Exception e) {}
                }
                
            	// Switches to the window and closes it
            	Set<String> set = driver.getWindowHandles();
            	set.remove(base);
            	assert set.size() == 1;
            	driver.switchTo().window((String) set.toArray()[0]);
            	driver.close();
            	driver.switchTo().window(base);
                // Wait for page to load and for base window to refresh with changes. not sure if there is any other way because the list becomes stale after refresh
                try {
                	Thread.sleep(3000);
                }
                catch(Exception e) {} 
            }

            if(desktopSearches != 0) {
	            // Back arrow to get to bing home page
	            driver.findElement(By.id("back-to-bing-text")).click();
	            
	            // Start desktop searching. first search is always web search for simplicity
	            driver.findElement(By.id("sb_form_q")).clear();
            	// 50 50 chance for a lowercase search or regular search with default capitalization
            	if(rand.nextInt(2) == 1) {
            		driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
            	}
            	else {
            		driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString().toLowerCase());
            	}	            driver.findElement(By.id("sb_form_go")).click();
	            currentSearch = "web";
	            desktopSearches--;
	            
	        	// Wait in between searches
	        	try {
	        		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
            }
            
            // Start desktop searching
            for(int i = 1; i <= desktopSearches; i++) {
            	// Randomly picks whether to search for web, images, or videos and then switches to that page
            	int typeOfSearch = rand.nextInt(5);

            	if(typeOfSearch >= 2 && !currentSearch.equals("web")) {		// web search
            		currentSearch = "web";
            		driver.findElement(By.linkText("Web"));
            	}
            	else if(typeOfSearch == 3 && !currentSearch.equals("image")) {	// image search
            		currentSearch = "image";
            		driver.findElement(By.linkText("Images"));
            	}
            	else if(typeOfSearch == 4 && !currentSearch.equals("video")){		// video search
            		currentSearch = "video";
            		driver.findElement(By.linkText("Videos"));
            	}

            	// Do the actual search
            	driver.findElement(By.id("sb_form_q")).clear();
            	
            	// 50 50 chance for a lowercase search or regular search with default capitalization
            	if(rand.nextInt(2) == 1) {
            		driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
            	}
            	else {
            		driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString().toLowerCase());
            	}
            	driver.findElement(By.id("sb_form_go")).click();
            	
            	// Wait in between searches
            	try {
            		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
            }
        	driver.quit();
        	
			// Mobile searching
            if(mobileSearches != 0) {
        		// Initialize new phantomjs driver (1.97) for normal web browser
            	DesiredCapabilities mobileCaps = DesiredCapabilities.iphone();
            	mobileCaps.setJavascriptEnabled(true);
        		mobileCaps.setCapability("takesScreenshot", true);
        		mobileCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, System.getenv("APPDATA") + "/BingBot/phantomjs.exe");
        		mobileCaps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10");
        		driver = new PhantomJSDriver(mobileCaps);
        		
	            driver.get(baseUrl);

	            // Login
	            driver.findElement(By.className("idText")).click();
	            driver.findElement(By.name("login")).sendKeys(account.getKey());
	            driver.findElement(By.name("passwd")).sendKeys(new String(account.getValue()));
	            driver.findElement(By.id("idSIButton9")).click();
	            //driver.switchTo().alert().accept();
	            
	            driver.get("http://www.bing.com/");
	            
	            // First search from homepage
	            driver.findElement(By.id("sbBoxCnt")).click();
	            driver.findElement(By.id("sb_form_q")).click();
	            
	            driver.findElement(By.id("sb_form_q")).clear();
	            
            	// 50 50 chance for a lowercase search or regular search with default capitalization
	            if(rand.nextInt(2) == 1) {
	            	driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
	            }
	            else {
	            	driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString().toLowerCase());
	            }
	            driver.findElement(By.id("sbBtn")).click();
	            currentSearch = "web";
//	            mobileSearches--;	Removed because sometimes 9/10 mobile points are earned because the first search doesn't register for points. Adds 1 extra search.
	            
	        	// Wait in between searches
	        	try {
	        		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        	
	        	// mobile searches from previous search page
	        	for(int i = 1; i <= mobileSearches; i++) {
	            	// Search
	            	driver.findElement(By.id("sb_form_q")).clear();
		            if(rand.nextInt(2) == 1) {
		            	driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
		            }
		            else {
		            	driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString().toLowerCase());
		            }
	            	driver.findElement(By.id("sb_form_go")).click();
	            	
	            	// Wait in between searches
	            	try {
	            		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
	            	}
	            	catch(Exception e) {
	            		e.printStackTrace();
	            	}
	            }
	        	driver.quit();
	    	}
    	}
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
    
    private int formatText(String text) {
    	// number of credits
    	int creditsEarned = 0;
    	int creditsMax = 0;
    	
    	// used to tell where certain points in the string is and what has been discovered
    	boolean firstMarker = false;
    	int firstMark = 0;
    	boolean secondMarker = true;
    	
    	for(int i = 0; i < text.length(); i++) {
    		char c = text.charAt(i);
    		
    		// finds where the of is and finds the number of credits already earned
    		if(!firstMarker && c == 'o' && text.charAt(i+1) == 'f') {
    			creditsEarned = Integer.parseInt(text.substring(0, i).trim());
    			firstMarker = true;
    			secondMarker = false;
    			firstMark = i;
    		}
    		// finds where credits is to find the total amount of credits available to earn
    		if(!secondMarker && c == 'c' && text.charAt(i+1) == 'r') {
    			creditsMax = Integer.parseInt(text.substring(firstMark+2, i).trim());
    			secondMarker = true;
    		}
    	}
    	return (creditsMax - creditsEarned) * SEARCHES_PER;
    }
    
    private boolean isElementPresent(By by, WebElement webElement) {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        try {
        	webElement.findElement(by);
        	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            return true;
        } catch (NoSuchElementException e) {
        	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            return false;
        }
    }
    
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    private void saveScreenshot(String location) {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile, new File(location));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
