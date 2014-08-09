import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WordList {
    
	// holds the words. using a treeSet for fast searching as it goes by natural order
	SortedSet<String> wordSet = new TreeSet<String>();
	
	private String filePath = (System.getProperty("user.dir") + "\\WordList.txt");
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
        
        // load current word list
        try {
        	Scanner s = new Scanner(new File(filePath));
        	s.useDelimiter("\n");
        	while(s.hasNext()) {
        		wordSet.add(s.next().trim());
        	}
        	s.close();
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    @Test
    public void getWords() {
    	// get top 5000 atricle titles on wikipedia
    	driver.get(wikiUrl);
    	
    	WebElement tbody = driver.findElement(By.xpath("//*[@id=\"mw-content-text\"]/dl[2]/dd/dl/dd/table/tbody"));
    	List<WebElement> tr_collection = tbody.findElements(By.tagName("tr"));
    	
    	for(WebElement tr : tr_collection) {
    		List<WebElement> td_collection = tr.findElements(By.tagName("td"));
    		
    		String word = td_collection.get(1).getText().trim();
    		if(!wordSet.contains(word)) {
    			wordSet.add(word);
    		}
    	}
    	
    	// get 50 more words from aol top daily searches
    	driver.get(aolUrl);
    	
    	WebElement list = driver.findElement(By.xpath("//*[@id=\"trends\"]/div[3]"));
    	List<WebElement> li_collection = list.findElements(By.tagName("li"));
    	
    	for(WebElement li : li_collection) {
    		String word = li.getText().trim();
    		
    		if(!wordSet.contains(word)) {
    			wordSet.add(word);
    		}
    	}
    }
    
    @After
    public void tearDown() {
        driver.quit();
        
        // write to text file
        try {
			FileWriter writer = new FileWriter(filePath);
			
			for(String word : wordSet) {
				writer.append(word + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
