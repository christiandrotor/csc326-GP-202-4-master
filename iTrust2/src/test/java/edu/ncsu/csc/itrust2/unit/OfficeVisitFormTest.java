package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * This tests the OfficeVisit functionalities
 *
 * @author Anuraag Agarwal
 *
 */
public class OfficeVisitFormTest {

    @Test
    public void testOfficeVisit () {
        final OfficeVisit ov = new OfficeVisit();

        // set appointment
        final AppointmentRequest appointment = new AppointmentRequest();
        appointment.setComments( "appointment1" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy hh:mm aaa", Locale.ENGLISH );
        Date parsedDate = new Date();
        try {
            parsedDate = sdf.parse( "10/02/2018" + " " + "12:30 PM" );
        }
        catch ( final ParseException e ) {
            e.printStackTrace();
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime( parsedDate );
        assertEquals( calendar.getTime(), parsedDate );
        appointment.setDate( calendar );
        assertEquals( appointment.getDate(), calendar );
        final User uHcp = new User();
        uHcp.setRole( Role.ROLE_HCP );
        assertEquals( uHcp.getRole(), Role.ROLE_HCP );
        uHcp.setUsername( "hcp1" );
        assertEquals( uHcp.getUsername(), "hcp1" );
        appointment.setHcp( uHcp );
        final User uPatient = new User();
        uPatient.setRole( Role.ROLE_PATIENT );
        assertEquals( uPatient.getRole(), Role.ROLE_PATIENT );
        uPatient.setUsername( "patient1" );
        assertEquals( uPatient.getUsername(), "patient1" );
        appointment.setPatient( uPatient );
        assertEquals( appointment.getPatient(), uPatient );
        appointment.setStatus( Status.PENDING );
        assertEquals( appointment.getStatus(), Status.PENDING );
        appointment.setType( AppointmentType.GENERAL_CHECKUP );
        assertEquals( appointment.getType(), AppointmentType.GENERAL_CHECKUP );
        ov.setAppointment( appointment );
        assertEquals( ov.getAppointment(), appointment );

        // set basic health metrics
        final BasicHealthMetrics bhm = new BasicHealthMetrics();
        bhm.setDiastolic( 120 );
        bhm.setHcp( uHcp );
        assertEquals( bhm.getHcp(), uHcp );
        bhm.setHdl( 75 );
        bhm.setHeadCircumference( 110.1f );
        bhm.setHeight( 150.3f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        assertEquals( bhm.getHouseSmokingStatus(), HouseholdSmokingStatus.INDOOR );
        bhm.setLdl( 111 );
        bhm.setPatient( uPatient );
        assertEquals( bhm.getPatient(), uPatient );
        bhm.setPatientSmokingStatus( PatientSmokingStatus.SOMEDAYS );
        assertEquals( bhm.getPatientSmokingStatus(), PatientSmokingStatus.SOMEDAYS );
        bhm.setSystolic( 111 );
        bhm.setTri( 110 );
        bhm.setWeight( 120.5f );
        ov.setBasicHealthMetrics( bhm );
        assertEquals( ov.getBasicHealthMetrics(), bhm );

        // set diagnoses
        final List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();
        final Diagnosis dg = new Diagnosis();
        final ICDCode base = new ICDCode();
        base.setCode( "T11" );
        assertEquals( base.getCode(), "T11" );
        base.setDescription( "Testing" );
        assertEquals( base.getDescription(), "Testing" );
        base.setId( 1L );
        dg.setCode( base );
        assertEquals( dg.getCode(), base );
        dg.setId( 12L );
        dg.setNote( "diagnosis1" );
        assertEquals( dg.getNote(), "diagnosis1" );
        dg.setVisit( ov );
        assertEquals( dg.getVisit(), ov );
        ov.setDiagnoses( diagnoses );
        assertEquals( ov.getDiagnoses(), diagnoses );

        ov.setHcp( uHcp );
        // set hospital
        final Hospital hp = new Hospital();
        hp.setAddress( "happy lane" );
        assertEquals( hp.getAddress(), "happy lane" );
        hp.setName( "Hope" );
        assertEquals( hp.getName(), "Hope" );
        hp.setState( State.NC );
        assertEquals( hp.getState(), State.NC );
        hp.setZip( "27510" );
        assertEquals( hp.getZip(), "27510" );
        ov.setHospital( hp );
        assertEquals( ov.getHospital(), hp );

        ov.setId( 111L );
        ov.setNotes( "This is an office visit" );
        assertEquals( ov.getNotes(), "This is an office visit" );
        ov.setPatient( uPatient );
        assertEquals( ov.getPatient(), uPatient );

        // set prescriptions
        final List<Prescription> prescriptions = new ArrayList<Prescription>();
        final Prescription p = new Prescription();
        p.setDosage( 13 );
        final Drug d = new Drug();
        d.setCode( "1000-1000-11" );
        assertEquals( d.getCode(), "1000-1000-11" );
        d.setDescription( "description" );
        assertEquals( d.getDescription(), "description" );
        d.setId( 111L );
        d.setName( "Drug" );
        assertEquals( d.getName(), "Drug" );
        p.setDrug( d );
        assertEquals( p.getDrug(), d );
        final SimpleDateFormat sdf1 = new SimpleDateFormat( "MM/dd/yyyy" );
        Date d1 = null;
        try {
            d1 = sdf1.parse( "01/28/2018" );
        }
        catch ( final ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Calendar c1 = Calendar.getInstance();
        c1.setTime( d1 );
        p.setEndDate( c1 );
        Date d2 = null;
        try {
            d2 = sdf1.parse( "02/30/2018" );
        }
        catch ( final ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        c1.setTime( d2 );
        p.setStartDate( c1 );
        p.setId( 100L );
        p.setPatient( uPatient );
        p.setRenewals( 3 );
        prescriptions.add( p );
        ov.setPrescriptions( prescriptions );
        assertEquals( ov.getPrescriptions(), prescriptions );

        ov.setDate( calendar );

        final OfficeVisitForm ofnew = new OfficeVisitForm( ov );
        ofnew.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        assertEquals( ofnew.getType(), AppointmentType.GENERAL_CHECKUP.toString() );
        assertEquals( ofnew.getTime(), "12:30 PM" );
        assertEquals( ofnew.getDate(), "10/02/2018" );

        ofnew.setPreScheduled( "yes" );

        OfficeVisit off = null;

        // set appointment
        final AppointmentRequest appointment1 = new AppointmentRequest();
        appointment.setComments( "appointment2" );

        final SimpleDateFormat sdf11 = new SimpleDateFormat( "MM/dd/yyyy hh:mm aaa", Locale.ENGLISH );
        Date parsedDate1 = new Date();
        try {
            parsedDate1 = sdf11.parse( "10/02/2018" + " " + "12:30 PM" );
        }
        catch ( final ParseException e ) {
            e.printStackTrace();
        }

        final Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime( parsedDate1 );
        assertEquals( calendar1.getTime(), parsedDate1 );
        appointment1.setDate( calendar1 );
        assertEquals( appointment1.getDate(), calendar1 );
        final User uHcp1 = new User();
        uHcp1.setRole( Role.ROLE_HCP );
        assertEquals( uHcp1.getRole(), Role.ROLE_HCP );
        uHcp1.setUsername( "hcp2" );
        assertEquals( uHcp1.getUsername(), "hcp2" );
        appointment1.setHcp( uHcp1 );
        final User uPatient1 = new User();
        uPatient1.setRole( Role.ROLE_PATIENT );
        assertEquals( uPatient1.getRole(), Role.ROLE_PATIENT );
        uPatient1.setUsername( "patient1" );
        assertEquals( uPatient1.getUsername(), "patient1" );
        appointment1.setPatient( uPatient1 );
        assertEquals( appointment1.getPatient(), uPatient1 );
        appointment1.setStatus( Status.PENDING );
        assertEquals( appointment1.getStatus(), Status.PENDING );
        appointment1.setType( AppointmentType.GENERAL_CHECKUP );
        assertEquals( appointment1.getType(), AppointmentType.GENERAL_CHECKUP );

        try {
            off = new OfficeVisit( ofnew );
            off.setAppointment( appointment1 );
            assertEquals( off.getAppointment(), appointment1 );

        }
        catch ( final NumberFormatException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( final ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( final IllegalArgumentException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
