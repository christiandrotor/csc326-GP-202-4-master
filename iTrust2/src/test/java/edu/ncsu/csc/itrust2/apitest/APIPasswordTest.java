package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.forms.personnel.PasswordChangeForm;
import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIPasswordTest {

    private MockMvc               mvc;
    PasswordEncoder               pe = new BCryptPasswordEncoder();

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    private void changePassword ( final User user, final String password, final String newP ) throws Exception {
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( password );
        form.setNewPassword( newP );
        form.setNewPassword2( newP );
        mvc.perform( post( "/api/v1/changePassword" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) );
    }

    // Save auto-formatter wont let this be a javadoc comment
    // Create user. Starts with password 123456.
    // Changes to 654321.
    // Reset to 98765.
    // Delete user
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testValidPasswordChanges () throws Exception {

        final UserForm patient = new UserForm( "patientPW", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "patientPW" ); // ensure they exist

        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "csc326.201.1@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) );

        assertTrue( pe.matches( "123456", user.getPassword() ) );
        changePassword( user, "123456", "654321" );
        user = User.getByName( "patientPW" ); // reload so changes are visible
        assertFalse( pe.matches( "123456", user.getPassword() ) );
        assertTrue( pe.matches( "654321", user.getPassword() ) );

        final Personnel p = Personnel.getByName( user );
        p.delete();
        user.delete();

    }

    /**
     * This tests that invalid api requests fail. Invalid passwords and
     * expiration testing handled in unit tests.
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patientPW", roles = { "USER", "ADMIN" } )
    @Test
    public void testInvalidPasswordChanges () throws Exception {

        // test unknown user
        final String pw = "123456";
        final String newP = "654321";
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( pw );
        form.setNewPassword( newP );
        form.setNewPassword2( newP );
        mvc.perform( post( "/api/v1/changePassword" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );

        mvc.perform( post( "/api/v1/requestPasswordReset" ).contentType( MediaType.APPLICATION_JSON )
                .content( "patientPW" ) ).andExpect( status().isBadRequest() );

    }

    /**
     * This tests that valid api reset.
     *
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patient", roles = { "USER", "ADMIN" } )
    @Test
    public void testPasswordResetPersonnel () throws Exception {

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );

        User user = new User( patient );
        user.save();

        user = User.getByName( "patient" ); // ensure they exist

        // set personnel up off of user and save
        final PersonnelForm personnel = new PersonnelForm();
        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "cndrotor@gmail.com" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setSelf( user.getUsername() );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );
        mvc.perform( post( "/api/v1/personnel" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) );

        PasswordResetToken.deleteAll( PasswordResetToken.class );

        // request to reset password
        mvc.perform(
                post( "/api/v1/requestPasswordReset" ).contentType( MediaType.APPLICATION_JSON ).content( "patient" ) )
                .andExpect( status().isOk() );

        // grabs the last token
        final PasswordResetToken token = PasswordResetToken.lastToken;

        // make password reset form
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( token.getTempPasswordPlaintext() ); // changes
                                                                     // to token
                                                                     // value
        form.setNewPassword( "654321" ); // new pass
        form.setNewPassword2( "654321" );

        mvc.perform( post( "/api/v1/resetPassword/" + token.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );

        Personnel.deleteAll( Personnel.class ); // clean up the database

    }

    /**
     * This tests that valid api reset.
     *
     *
     * @throws Exception
     */
    @WithMockUser ( username = "patient", roles = { "USER", "ADMIN" } )
    @Test
    public void testPasswordResetPatient () throws Exception {

        Patient.deleteAll( Patient.class );
        // set in a different test
        // set patient up
        final User patient = new User( "patientTestPatient", "123456", Role.ROLE_PATIENT, 1 );
        patient.save();
        final User mom = new User( "patientTestMom", "123456", Role.ROLE_PATIENT, 1 );
        mom.save();
        final User dad = new User( "patientTestDad", "123456", Role.ROLE_PATIENT, 1 );
        dad.save();
        final PatientForm form = new PatientForm();
        form.setMother( mom.getUsername() );
        form.setFather( dad.getUsername() );
        form.setFirstName( "patient" );
        form.setPreferredName( "patient" );
        form.setLastName( "mcpatientface" );
        form.setEmail( "cndrotor@gmail.com" );
        form.setAddress1( "Some town" );
        form.setAddress2( "Somewhere" );
        form.setCity( "placecity" );
        form.setState( State.AL.getName() );
        form.setZip( "27606" );
        form.setPhone( "111-111-1111" );
        form.setDateOfBirth( "01/01/1901" );
        form.setDateOfDeath( "01/01/2001" );
        form.setCauseOfDeath( "Hit by a truck" );
        form.setBloodType( BloodType.ABPos.getName() );
        form.setEthnicity( Ethnicity.Asian.getName() );
        form.setGender( Gender.Male.getName() );
        form.setSelf( patient.getUsername() );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) );

        final Patient p = Patient.getPatient( patient.getUsername() );

        mvc.perform( post( "/api/v1/requestPasswordReset" ).contentType( MediaType.APPLICATION_JSON )
                .content( patient.getUsername() ) ).andExpect( status().isOk() );

        Patient.deleteAll( Patient.class );

    }

}
