/**
 * 
 */
package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import io.github.bonigarcia.wdm.ChromeDriverManager;

/**
 * Concrete Implementation of the Logs feature file.
 * Combination of cucumber and selenium test.
 * 
 * @author Timothy Figgins
 *
 */
public class LogStepDefs {
    //private static boolean initialized = false;

    private WebDriver      driver;
    private final String   baseUrl     = "http://localhost:8080/iTrust2";

    WebDriverWait          wait;

    /**
     * Setup for the new tests.
     * Uses Chrome drivers to retrieve angular binded data.
     */
    @Before
    public void setup () {
       // driver = new HtmlUnitDriver( true );
        ChromeDriverManager.getInstance().setup();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments( "headless" );
        options.addArguments( "window-size=1200x600" );
        options.addArguments( "blink-settings=imagesEnabled=false" );
        driver = new ChromeDriver( options );
        wait = new WebDriverWait( driver, 5 );
    }

    /**
     * Cleanup of tests
     */
    @After
    public void tearDown () {
        driver.close();
    }

    private void setTextField ( final By byVal, final Object value ) {
        final WebElement elem = driver.findElement( byVal );
        elem.clear();
        elem.sendKeys( value.toString() );
    }
    
    /**
     * Initializes the login of the current personnel
     * @param username name of user login
     * @throw an IllegalArgumentException on invalid login
     */
    @Given ( "^I am logged in as a (.+)$" )
    public void login(String username) {
        if (username.equals( "patient" )) {
            // login as patient
            patientLogin();
        } else if (username.equals( "hcp" )) {
            // login as hcp
            hcpLogin();
        } else if (username.equals( "admin" )) {
            // login as admin
            adminLogin();
        } else {
            throw new IllegalArgumentException( "Invalid user login" );
        }
    }
    
    /**
     * Checks the homepage to make sure table has loaded.
     */
    @And ( "^I can see my top 10 logs on the home page$" )
    public void checkHome() {
        final List<WebElement> logs = driver.findElements( By.cssSelector( "td[name=\"Name_of_Accessor\"]" ) );
        //should not have more than 10
        assertTrue(logs.size() <= 10);
    }
    
    /**
     * Moves to the View Access Logs page
     */
    @When ( "^I navigate to the View Logs page$" )
    public void navigate() {
        driver.findElement(By.linkText("View Access Logs")).click();
    }
    
    /**
     * Checks that the table of logs loaded, and make sure that it does not load logs from a user
     * that is not the currently logged in user.  Also checks to make sure it is updating constantly,
     * first two logs are guaranteed to be view log and successful login.
     * It is the end of Scenario one so it logs out the user afterwards.
     * Sometimes name of Accessor can be hcp or admin when logged in as a patient, so have special 
     * checks for that.
     * @param user current user
     * @throws InterruptedException if something happens during thread wait
     */
    @Then ( "^the top 2 logs are successful login and access of log page for (.+)$" )
    public void logPageCheck(String user) throws InterruptedException {
        wait = new WebDriverWait( driver, 5 );
        final List<WebElement> logNames = driver.findElements( By.cssSelector( "td[name=\"Name_of_Accessor\"]" ) );
        final List<WebElement> logSecondary = driver.findElements( By.cssSelector( "td[name=\"Role_of_Accessor\"]" ) );
        final List<WebElement> logsTypes = driver.findElements( By.cssSelector( "td[name=\"Transaction_Type\"]" ) );
        int index = 0;
        for (final WebElement we: logNames ) {
            if (!(logSecondary.get( index ).getText().equals( user ))) {
                assertEquals(we.getText(), user);
            }
            index++;
        }
        TransactionType v = TransactionType.VIEW_LOGS;
        TransactionType login = TransactionType.LOGIN_SUCCESS;
        assertTrue(logsTypes.get( 0 ).getText().equals(v.name()));
        assertTrue(logsTypes.get( 1 ).getText().equals( login.name()));
        
        //log out afterwards
        driver.findElement(By.id( "logout" )).click();
    }
    
    /**
     * Log in specifically as a patient.
     */
    @Given ( "^I am a patient that has logged in$" )
    public void patientSpecificLogin() {
        patientLogin();
    }
            
    /**
     * Puts in a range in the year 2019 to ensure no logs should be pulled up.
     * @throws InterruptedException if something happens during thread sleep
     */
    @And ( "^enter a date range of 2019/01/01 to 2019/01/02$" )
    public void enterInvalidRange() throws InterruptedException {
        //driver.wait(10);
        setTextField(By.xpath( "//input[@ng-model='start']" ), "2019/01/01");
        setTextField(By.xpath( "//input[@ng-model='end']" ), "2019/01/02");
        driver.findElement( By.className( "btn" ) ).click();
        Thread.sleep( 5000 );
    }
    
    /**
     * Checks to make sure the log table is empty.
     */
    @Then ( "^the log table is empty$" )
    public void emtpyCheck() {
        wait = new WebDriverWait( driver, 5 );
        final List<WebElement> logNames = driver.findElements( By.cssSelector( "td[name=\"Name_of_Accessor\"]" ) );
        assertTrue(logNames.size() == 0);
    }
    
    /**
     * Puts in a valid time range from the beginning of this year 2018 to 2019
     * to ensure all logs are loaded.
     * @throws InterruptedException if something happens during thread sleep
     */
    @When ( "^I enter a valid date range of 2018/01/01 to 2019/01/02$" )
    public void enterValidRange() throws InterruptedException {
        //driver.wait(10);
        setTextField(By.xpath( "//input[@ng-model='start']" ), "2018/01/01");
        setTextField(By.xpath( "//input[@ng-model='end']" ), "2019/01/02");
        driver.findElement( By.className( "btn" ) ).click();
        Thread.sleep( 5000 );
    }
    
    /**
     * Checks that the log table is re-populated.
     */
    @Then ( "^the log table re-populates$" )
    public void populateCheck() {
        wait = new WebDriverWait( driver, 5 );
        final List<WebElement> logNames = driver.findElements( By.cssSelector( "td[name=\"Name_of_Accessor\"]" ) );
        assertTrue(logNames.get(0).isDisplayed());
    }
    
    private void adminLogin () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "admin" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }
    
    private void hcpLogin () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "hcp" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }
    
    private void patientLogin () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "patient" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

}
