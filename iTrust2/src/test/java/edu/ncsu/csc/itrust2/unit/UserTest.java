package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Unit tests for the User class.
 *
 * @author jshore
 *
 */
public class UserTest {

    /**
     * Tests equals comparison of two user objects. Also verifies getters and
     * setters of the used properties.
     */
    @Test
    public void testEqualsAndProperties () {
        final User u1 = new User();
        final User u2 = new User();

        assertFalse( u1.equals( new Object() ) );
        assertFalse( u1.equals( null ) );
        assertTrue( u1.equals( u1 ) );

        u1.setEnabled( 1 );
        assertTrue( 1 == u1.getEnabled() );
        u2.setEnabled( 1 );

        u1.setPassword( "abcdefg" );
        assertEquals( "abcdefg", u1.getPassword() );
        u2.setPassword( "abcdefg" );

        u1.setRole( Role.valueOf( "ROLE_PATIENT" ) );
        assertEquals( Role.valueOf( "ROLE_PATIENT" ), u1.getRole() );
        u2.setRole( Role.valueOf( "ROLE_PATIENT" ) );

        u1.setUsername( "abcdefg" );
        assertEquals( "abcdefg", u1.getUsername() );
        u2.setUsername( "abcdefg" );

        assertTrue( u1.equals( u2 ) );
    }

    @Test
    public void testFunctions () {
        User.getUsers();
        User.getByName( "test" );
        User.getByRole( Role.ROLE_PATIENT );
        User.getByNameAndRole( "test", Role.ROLE_PATIENT );
        User.getHCPs();
        User.getPatients();
        User.getUsers();
        final User u1 = new User( "test1", "123456", Role.ROLE_PATIENT, 1 );
        u1.getId();
        u1.delete();
        final User u2 = new User( "test2", "123456", Role.ROLE_PATIENT, 1 );
        assertEquals( u2.hashCode(), u2.hashCode() );
        final User u3 = new User( null, "123456", Role.ROLE_PATIENT, 1 );
        final User u4 = new User( "test3", null, Role.ROLE_PATIENT, 1 );
        final User u5 = new User( "test4", "123456", null, 1 );
        final User u6 = new User( "test5", "123456", Role.ROLE_PATIENT, null );
        assertFalse( u2.equals( u3 ) );
        assertFalse( u2.equals( u4 ) );
        assertFalse( u2.equals( u5 ) );
        assertFalse( u2.equals( u6 ) );
        final UserForm form = new UserForm( "test1", "123456", Role.ROLE_PATIENT, 1 );
        final User u7 = new User( form );
        assertFalse( u2.equals( u7 ) );
    }

}
