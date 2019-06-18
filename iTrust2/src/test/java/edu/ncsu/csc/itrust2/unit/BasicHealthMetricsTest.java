package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class BasicHealthMetricsTest {

    @Test
    public void testField () {
        final BasicHealthMetrics bhm1 = new BasicHealthMetrics();
        bhm1.setHeight( 1.2f );
        bhm1.setWeight( 21.4f );
        bhm1.setDiastolic( 2 );
        bhm1.setLdl( 3 );
        bhm1.setHdl( 4 );
        bhm1.setSystolic( 5 );
        bhm1.setTri( 500 );
        bhm1.setHeadCircumference( 4.5f );
        assertEquals( bhm1.getHeight().toString(), "1.2" );
        assertEquals( bhm1.getWeight().toString(), "21.4" );
        assertEquals( bhm1.getDiastolic().toString(), "2" );
        assertEquals( bhm1.getLdl().toString(), "3" );
        assertEquals( bhm1.getHdl().toString(), "4" );
        assertEquals( bhm1.getSystolic().toString(), "5" );
        assertEquals( bhm1.getTri().toString(), "500" );
        assertEquals( bhm1.getHeadCircumference().toString(), "4.5" );
    }

    @Test
    public void testOtherField () {
        final BasicHealthMetrics bhm1 = new BasicHealthMetrics();
        final User user1 = new User();
        bhm1.setHcp( user1 );
        assertTrue( bhm1.getHcp().equals( user1 ) );
        bhm1.setPatient( user1 );
        assertTrue( bhm1.getPatient().equals( user1 ) );
        final HouseholdSmokingStatus hss = HouseholdSmokingStatus.INDOOR;
        bhm1.setHouseSmokingStatus( hss );
        assertEquals( bhm1.getHouseSmokingStatus(), HouseholdSmokingStatus.INDOOR );
        final PatientSmokingStatus pss = PatientSmokingStatus.EVERYDAY;
        bhm1.setPatientSmokingStatus( pss );
        assertEquals( bhm1.getPatientSmokingStatus(), PatientSmokingStatus.EVERYDAY );
    }

    @Test
    public void testOtherMethod () throws ParseException {
        BasicHealthMetrics.getBasicHealthMetrics();
        BasicHealthMetrics.getBasicHealthMetricsForHCP( "hcp" );
        BasicHealthMetrics.getBasicHealthMetricsForHCPAndPatient( "hcp", "test" );
        BasicHealthMetrics.getBasicHealthMetricsForPatient( "test" );
        BasicHealthMetrics.getById( 1l );
        final OfficeVisitForm form = new OfficeVisitForm();
        final BasicHealthMetrics bhm0 = new BasicHealthMetrics( form );
        final BasicHealthMetrics bhm1 = new BasicHealthMetrics();
        bhm1.setHeight( 1.2f );
        bhm1.setWeight( 21.4f );
        bhm1.setDiastolic( 2 );
        bhm1.setLdl( 3 );
        bhm1.setHdl( 4 );
        bhm1.setSystolic( 5 );
        bhm1.setTri( 500 );
        final User user1 = new User();
        bhm1.setHcp( user1 );
        bhm1.setPatient( user1 );
        final HouseholdSmokingStatus hss = HouseholdSmokingStatus.INDOOR;
        bhm1.setHouseSmokingStatus( hss );
        final PatientSmokingStatus pss = PatientSmokingStatus.EVERYDAY;
        bhm1.setPatientSmokingStatus( pss );
        assertEquals( bhm1.hashCode(), bhm1.hashCode() );
        assertTrue( bhm1.equals( bhm1 ) );
        final BasicHealthMetrics bhm2 = new BasicHealthMetrics();
        assertTrue( bhm0.equals( bhm2 ) );
        bhm2.setHeight( 1.2f );
        bhm2.setWeight( 21.4f );
        bhm2.setDiastolic( 2 );
        bhm2.setLdl( 3 );
        bhm2.setHdl( 4 );
        bhm2.setSystolic( 5 );
        bhm2.setTri( 500 );
        bhm2.setHcp( user1 );
        bhm2.setPatient( user1 );
        bhm2.setHouseSmokingStatus( hss );
        bhm2.setPatientSmokingStatus( pss );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm1.setHeight( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm1.setWeight( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm1.setDiastolic( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm1.setLdl( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm2.setHdl( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm2.setSystolic( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm2.setTri( null );
        assertTrue( bhm1.equals( bhm2 ) );
        bhm2.setHcp( null );
        assertFalse( bhm1.equals( bhm2 ) );
        bhm2.setPatient( null );
        assertFalse( bhm1.equals( bhm2 ) );
        bhm2.setHouseSmokingStatus( null );
        assertFalse( bhm1.equals( bhm2 ) );
        bhm2.setPatientSmokingStatus( null );
        assertFalse( bhm1.equals( bhm2 ) );
    }

    @Test
    public void testHashCode () {
        final BasicHealthMetrics bhm1 = new BasicHealthMetrics();
        bhm1.setHeight( 1.2f );
        bhm1.setWeight( 21.4f );
        bhm1.setDiastolic( 2 );
        bhm1.setLdl( 3 );
        bhm1.setHdl( 4 );
        bhm1.setSystolic( 5 );
        bhm1.setTri( 500 );
        final User user1 = new User();
        bhm1.setHcp( user1 );
        bhm1.setPatient( user1 );
        final HouseholdSmokingStatus hss = HouseholdSmokingStatus.INDOOR;
        bhm1.setHouseSmokingStatus( hss );
        final PatientSmokingStatus pss = PatientSmokingStatus.EVERYDAY;
        bhm1.setPatientSmokingStatus( pss );

        final BasicHealthMetrics bhm2 = new BasicHealthMetrics();
        bhm2.setHeight( 1.2f );
        bhm2.setWeight( 21.4f );
        bhm2.setDiastolic( 2 );
        bhm2.setLdl( 3 );
        bhm2.setHdl( 4 );
        bhm2.setSystolic( 5 );
        bhm2.setTri( 500 );
        bhm2.setHcp( user1 );
        bhm2.setPatient( user1 );
        bhm2.setHouseSmokingStatus( hss );
        bhm2.setPatientSmokingStatus( pss );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setHeight( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setWeight( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setDiastolic( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setLdl( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setHdl( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setSystolic( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setTri( null );
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setHcp( null );
        assertNotEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setPatient( null );
        assertNotEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setHouseSmokingStatus( null );
        assertNotEquals( bhm1.hashCode(), bhm2.hashCode() );
        bhm2.setPatientSmokingStatus( null );
        assertNotEquals( bhm1.hashCode(), bhm2.hashCode() );
    }

}
