import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WordList {
    
    // declare variables for selenium
    private WebDriver driver;
    private String wikiUrl;
    private String aolUrl;
    
    public WordList() { }
  
    // execute whole script
    public void execute() {
        setUp();
        getWords();
        tearDown();
    }
    
    @Before
    public void setUp() {
        driver = new FirefoxDriver();
        wikiUrl = "http://en.wikipedia.org/wiki/Wikipedia:5000";		//This list updates every Sunday morning (UTC), aggregating data from the 7 days preceeding 11:59PM Saturday.
        aolUrl = "http://search.aol.com/aol/trends";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }
    
    @Test
    public void getWords() {
    	// get top 5000 atricle titles on wikipedia
    	driver.get(wikiUrl);
    	
    	WebElement tbody = driver.findElement(By.xpath("//*[@id=\"mw-content-text\"]/dl[2]/dd/dl/dd/table/tbody"));
    	List<WebElement> tr_collection = tbody.findElements(By.tagName("tr"));
    	
    	for(WebElement tr : tr_collection) {
    		List<WebElement> td_collection = tr.findElements(By.tagName("td"));
    		System.out.println(td_collection.get(1).getText());
    	}
    	
    	// get 50 more words from aol top daily searches
    	driver.get(aolUrl);
    	
    	WebElement list = driver.findElement(By.xpath("//*[@id=\"trends\"]/div[3]"));
    	List<WebElement> li_collection = list.findElements(By.tagName("li"));
    	
    	for(WebElement li : li_collection) {
    		System.out.println(li.getText());
    	}
    }
    
    @After
    public void tearDown() {
        //driver.quit();
    }
        
    private boolean isElementPresent(By by) {
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
