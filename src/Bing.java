/*	Created by: Tyler Wong
 * 	Date: August 10th, 2014
 * 	Purpose: Selenium script that controls a FireFox browser to obtain	
 * 	all Bing Rewards points as possible per account.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Bing {

	// constants used for timing in between searches and other variables
	private final int MIN_TIME = 10;
	private final int MAX_TIME = 30;
	private final int SEARCHES_PER = 2;	// the number of searches to earn 1 credit
    Random rand = new Random();
	
	// holds the number of searches needed to be done for mobile and desktop. set to 40 just in case the website changes
	private int desktopSearches = 40;
	private int mobileSearches = 40;
	
	String currentSearch;	// type of search being done. ex. web, image, or video
	
	// holds account info
    private Map<String, char[]> accounts = new HashMap<String, char[]>();
    
    // holds words from word list
    private Object[] wordArray;
    
    // declare variables for selenium
    private WebDriver driver;
    private String baseUrl;
    
    public Bing(Map<String, char[]> accounts, Object[] wordArray) {
    	this.accounts = accounts;
    	this.wordArray = wordArray;
    }
  
    // execute whole script
    public void execute() {
        try {
            setUp();
            search();
//            tearDown();		not used anymore
        }
        catch(Exception e) {
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
    public void search() {
    	for(Map.Entry<String, char[]> account : accounts.entrySet()) {
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            driver.get(baseUrl);

            // navigate to sign in
            driver.findElement(By.id("id_s")).click();
            driver.findElement(By.linkText("Connect")).click();
            
            // login
            driver.findElement(By.name("login")).sendKeys(account.getKey());
            driver.findElement(By.name("passwd")).sendKeys(new String(account.getValue()));
            driver.findElement(By.id("idSIButton9")).click();
            driver.switchTo().alert().accept();
            
            // gets the number of searches needed to be done
            List<WebElement> searchList = (driver.findElement(By.xpath("//*[@id=\"dashboard_wrapper\"]/div[1]/div[2]/ul"))).findElements(By.tagName("li"));
            for(WebElement li : searchList) {
            	if(li.findElement(By.className("title")).getText().trim().equals("Mobile search")) {
            		mobileSearches = formatText(li.findElement(By.className("progress")).getText());
            	}
            	else if(li.findElement(By.className("title")).getText().trim().equals("PC search")) {
            		desktopSearches = formatText(li.findElement(By.className("progress")).getText());
            	}
            }
            
            // gets the number of "earn and explore" rewards there are
            WebElement ulNum = driver.findElement(By.xpath("//*[@id=\"dashboard_wrapper\"]/div[1]/div[1]/ul"));
            List<WebElement> lisNum = ulNum.findElements(By.tagName("li"));
            int size = lisNum.size();
            
            // earns the "earn and explore" rewards. have to use this for loop because we need to refresh the elements to prevent stale elements from being used
            for(int i = 0; i < size; i++) {
            	// finds the rewards html elements
            	WebElement ul = driver.findElement(By.xpath("//*[@id=\"dashboard_wrapper\"]/div[1]/div[1]/ul"));
                List<WebElement> lis = ul.findElements(By.tagName("li"));
                String base = driver.getWindowHandle();
	        	lis.get(i).findElement(By.className("title")).click();
		        	
	        	// switches to the window and closes it
	        	Set<String> set = driver.getWindowHandles();
	        	set.remove(base);
	        	assert set.size() == 1;
	        	driver.switchTo().window((String) set.toArray()[0]);
	            driver.close();
	            driver.switchTo().window(base);
	            
	            // wait for page to load and for base window to refresh with changes. not sure if there is any other way because the list becomes stale after refresh
	            try {
	            	Thread.sleep(3000);
	            }
	            catch(Exception e) {}
            }
            
            if(desktopSearches != 0) {
	            // back arrow to get to bing home page
	            driver.findElement(By.className("me_backarrow")).click();
	            
	            // start desktop searching. first search is always web search for simplicity
	            driver.findElement(By.id("sb_form_q")).clear();
	            driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
	            driver.findElement(By.id("sb_form_go")).click();
	            currentSearch = "web";
	            desktopSearches--;
	            
	        	// wait in between searches
	        	try {
	        		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
            }
            // start desktop searching
            for(int i = 1; i <= desktopSearches; i++) {
            	// randomly picks whether to search for web, images, or videos and then switches to that page
            	int typeOfSearch = rand.nextInt(5);
            	
            	if(typeOfSearch >= 2 && !currentSearch.equals("web")) {		// web search
            		currentSearch = "web";
            		driver.findElement(By.linkText("WEB")).click();
            	}
            	else if(typeOfSearch == 3 && !currentSearch.equals("image")) {	// image search
            		currentSearch = "image";
            		driver.findElement(By.linkText("IMAGES")).click();
            	}
            	else if(typeOfSearch == 4 && !currentSearch.equals("video")){		// video search
            		currentSearch = "video";
            		driver.findElement(By.linkText("VIDEOS")).click();
            	}

            	// do the actual search
            	driver.findElement(By.id("sb_form_q")).clear();
            	driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
            	driver.findElement(By.id("sb_form_go")).click();
            	
            	// wait in between searches
            	try {
            		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            	}
            }
        	driver.quit();
            
            if(mobileSearches != 0) {
	            // mobile searching
	            FirefoxProfile profile = new FirefoxProfile(); 
	            profile.setPreference("general.useragent.override", "iPhone"); 
	            
	            driver = new FirefoxDriver(profile);
	            driver.get(baseUrl);
	            
	            // login
	            driver.findElement(By.className("idText")).click();
	            driver.findElement(By.name("login")).sendKeys(account.getKey());
	            driver.findElement(By.name("passwd")).sendKeys(new String(account.getValue()));
	            driver.findElement(By.id("i0011")).click();
	            driver.switchTo().alert().accept();
	            
	            driver.get("http://www.bing.com/");
	            
	            // first search from homepage
	            driver.findElement(By.id("sbBoxCnt")).click();
	            driver.findElement(By.id("sb_form_q")).click();
	            
	            driver.findElement(By.id("sb_form_q")).clear();
	            driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
	            driver.findElement(By.id("sbBtn")).click();
	            currentSearch = "web";
	            mobileSearches--;
	            
	        	// wait in between searches
	        	try {
	        		Thread.sleep(((rand.nextInt(MAX_TIME - MIN_TIME)) + MIN_TIME) *  1000);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        	
	        	// mobile searches from previous search page
	        	for(int i = 1; i <= mobileSearches; i++) {
	            	// randomly picks whether to search for web, images, or videos and then switches to that page
//	            	int typeOfSearch = rand.nextInt(5);
//	            	
//	            	if(typeOfSearch >= 5 && !currentSearch.equals("web")) {		// web search
//	            		currentSearch = "web";
//	            		driver.findElement(By.linkText("WEB")).click();
//	            	}
//	            	else if(typeOfSearch == 3 && !currentSearch.equals("image")) {	// image search
//	            		currentSearch = "image";
//	            		driver.findElement(By.linkText("IMAGES")).click();
//	            	}
//	            	else if(typeOfSearch == 4 && !currentSearch.equals("video")){		// video search
//	            		currentSearch = "video";
//	            		driver.findElement(By.linkText("VIDEOS")).click();
//	            	}
	
	            	// do the actual search
	            	driver.findElement(By.id("sb_form_q")).clear();
	            	driver.findElement(By.id("sb_form_q")).sendKeys(wordArray[rand.nextInt(wordArray.length)].toString());
	            	driver.findElement(By.id("sbBtn")).click();
	            	
	            	// wait in between searches
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
}
