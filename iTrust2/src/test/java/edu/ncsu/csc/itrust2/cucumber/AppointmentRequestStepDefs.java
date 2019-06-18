package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

public class AppointmentRequestStepDefs {

    private WebDriver          driver;
    private final String       baseUrl = "http://localhost:8080/iTrust2";

    // Token for testing
    private PasswordResetToken token   = null;
    WebDriverWait              wait;

    @Before
    public void setup () {
        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 5 );

        HibernateDataGenerator.generateUsers();
    }

    @After
    public void tearDown () {
        driver.close();
    }

    @Given ( "There is a sample HCP and sample Patient in the database" )
    public void startingUsers () {
        final User hcp = new User( "hcp", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP,
                1 );
        hcp.save();

        final User patient = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        patient.save();
    }

    @When ( "I log in as patient" )
    public void loginPatient () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @When ( "I navigate to the Request Appointment page" )
    public void requestPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );
    }

    @When ( "I fill in values in the Appointment Request Fields" )
    public void fillFields () {
        final WebElement date = driver.findElement( By.id( "date" ) );
        date.clear();
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        date.sendKeys( sdf.format( future.getTime() ) );
        final WebElement time = driver.findElement( By.id( "time" ) );
        time.clear();
        time.sendKeys( "11:59 PM" );
        final WebElement comments = driver.findElement( By.id( "comments" ) );
        comments.clear();
        comments.sendKeys( "Test appointment please ignore" );
        driver.findElement( By.className( "btn" ) ).click();

    }

    @Then ( "The appointment is requested successfully" )
    public void requestedSuccessfully () {
        assertTrue( driver.getPageSource().contains( "Your appointment has been requested successfully" ) );
    }

    @Then ( "The appointment can be found in the list" )
    public void findAppointment () {
        driver.findElement( By.linkText( "iTrust2" ) ).click();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewrequests-patient').click();" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        final String dateString = sdf.format( future.getTime() );
        assertTrue( driver.getPageSource().contains( dateString ) );

    }

    @Given ( "An appointment request exists" )
    public void createAppointmentRequest () {
        DomainObject.deleteAll( AppointmentRequest.class );

        final AppointmentRequest ar = new AppointmentRequest();
        ar.setComments( "Test request" );
        ar.setPatient( User.getByNameAndRole( "patient", Role.ROLE_PATIENT ) );
        ar.setHcp( User.getByNameAndRole( "hcp", Role.ROLE_HCP ) );
        final Calendar time = Calendar.getInstance();
        time.setTimeInMillis( Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 60 * 24 * 14 );
        ar.setDate( time );
        ar.setStatus( Status.PENDING );
        ar.setType( AppointmentType.GENERAL_CHECKUP );
        ar.save();
    }

    @When ( "I log in as hcp" )
    public void loginHcp () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @When ( "I navigate to the View Requests page" )
    public void viewRequests () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewrequests').click();" );

    }

    @When ( "I approve the Appointment Request" )
    public void approveRequest () {
        driver.findElement( By.name( "appointment" ) ).click();
        driver.findElement( By.className( "btn" ) ).click();
    }

    @When ( "I receive an email with a confirmation of appointment status" )
    public void getEmail () throws InterruptedException {
        // wait for the email to be delivered
        Thread.sleep( 5 * 1000 );
        token = getTokenFromEmail();
        if ( token == null ) {
            fail( "Failed to receive email" );
        }
    }

    @Then ( "The request is successfully updated" )
    public void requestUpdated () {
        assertTrue( driver.getPageSource().contains( "Appointment request was successfully updated" ) );
    }

    @Then ( "The appointment is in the list of upcoming events" )
    public void upcomingEvents () {
        driver.findElement( By.linkText( "iTrust2" ) ).click();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('upcomingrequests').click();" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        final String dateString = sdf.format( future.getTime() );
        assertTrue( driver.getPageSource().contains( dateString ) );
        assertTrue( driver.getPageSource().contains( "patient" ) );
    }

    /*
     * Credit for checking email:
     * https://www.tutorialspoint.com/javamail_api/javamail_api_checking_emails.
     * htm
     */
    private PasswordResetToken getTokenFromEmail () {
        final String username = "cscteam04@gmail.com";
        final String password = "labcscteam04";
        final String host = "pop.gmail.com";
        PasswordResetToken token = null;
        try {
            // create properties field
            final Properties properties = new Properties();
            properties.put( "mail.store.protocol", "pop3" );
            properties.put( "mail.pop3.host", host );
            properties.put( "mail.pop3.port", "995" );
            properties.put( "mail.pop3.starttls.enable", "true" );
            final Session emailSession = Session.getDefaultInstance( properties );
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            final Store store = emailSession.getStore( "pop3s" );

            store.connect( host, username, password );

            // create the folder object and open it
            final Folder emailFolder = store.getFolder( "INBOX" );
            emailFolder.open( Folder.READ_WRITE );

            // retrieve the messages from the folder in an array and print it
            final Message[] messages = emailFolder.getMessages();
            Arrays.sort( messages, ( x, y ) -> {
                try {
                    return y.getSentDate().compareTo( x.getSentDate() );
                }
                catch ( final MessagingException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return 0;
            } );
            for ( final Message message : messages ) {
                // SUBJECT
                if ( message.getSubject() != null && message.getSubject().contains( "iTrust2 Password Reset" ) ) {
                    String content = (String) message.getContent();
                    content = content.replaceAll( "\r", "" ); // Windows
                    content = content.substring( content.indexOf( "?tkid=" ) );

                    final Scanner scan = new Scanner( content.substring( 6, content.indexOf( '\n' ) ) );
                    System.err.println( "token(" + content.substring( 6, content.indexOf( '\n' ) ) + ")end" );
                    final long tokenId = scan.nextLong();
                    scan.close();

                    content = content.substring( content.indexOf( "temporary password: " ) );
                    content = content.substring( 20, content.indexOf( "\n" ) );
                    content.trim();

                    if ( content.endsWith( "\n" ) ) {
                        content = content.substring( content.length() - 1 );
                    }

                    token = new PasswordResetToken();
                    token.setId( tokenId );
                    token.setTempPasswordPlaintext( content );
                    break;
                }
                else if ( message.getSubject() != null
                        && message.getSubject().contains( "iTrust2 Appointment Status" ) ) {
                    token = new PasswordResetToken();
                    break;
                }
            }

            // close the store and folder objects
            emailFolder.close( false );
            store.close();
            return token;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}
