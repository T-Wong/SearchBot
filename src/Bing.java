import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class Bing {

	// holds account info
    private Map<String, char[]> accounts = new HashMap<String, char[]>();
    
    // declare variables for selenium
    private WebDriver driver;
    private String baseUrl;
    
    public Bing(Map<String, char[]> accounts2) {
    	this.accounts = accounts2;
    }
  
    // execute whole script
    public void execute() {
        try {
            setUp();
            search();
            tearDown();
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
        driver = new FirefoxDriver();
        baseUrl = "http://www.bing.com/rewards/dashboard";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }
    
    @Test
    public void search() {
    	for(Map.Entry<String, char[]> account : accounts.entrySet()) {
            driver.get(baseUrl);

            // navigate to sign in
            driver.findElement(By.id("id_s")).click();
            driver.findElement(By.linkText("Connect")).click();
            
            // login
            driver.findElement(By.name("login")).sendKeys(account.getKey());
            driver.findElement(By.name("passwd")).sendKeys(new String(account.getValue()));
            driver.findElement(By.id("idSIButton9")).click();
            driver.switchTo().alert().accept();
            
            // gets the number of "earn and explore" rewards there are
            WebElement ulNum = driver.findElement(By.xpath("//*[@id=\"dashboard_wrapper\"]/div[1]/div[1]/ul"));
            List<WebElement> lisNum = ulNum.findElements(By.tagName("li"));
            int size = lisNum.size();
            
            for(int i = 0; i < size; i++) {
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
    	}
    }
    
    @After
    public void tearDown() {
        driver.quit();
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
