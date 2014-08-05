import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class Bing {
    
	// holds the username and the passwords of the accounts
    private String firstAccount;
    private String firstPassword;
    private String secondAccount;
    private String secondPassword;
    private String thirdAccount;
    private String thirdPassword;
    private String fourthAccount;
    private String fourthPassword;
    private String fifthAccount;
    private String fifthPassword;

    private WebDriver driver;
    private String baseUrl;
    
    public Bing(ArrayList<String> accounts) {
    	for(String account : accounts) {
    		
    	}
    }
  
    // execute whole script
    public void execute() {
        try {
            setUp();
            search();
            tearDown();
        }
        catch(Exception e) {

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
        driver.get(baseUrl);
        
        driver.findElement(By.linkText("Sign in")).click();
        driver.findElement(By.linkText("Connect")).click();
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
        
    private boolean isElementPresent(By by) {
        driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
