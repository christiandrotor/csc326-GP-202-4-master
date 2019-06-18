package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with log entries.
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APILogEntryTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests LogEntryAPI
     *
     * @throws Exception
     */
    @SuppressWarnings ( "unchecked" )
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    @Test
    public void testLogEntryAPI () throws Exception {
        // Ensure there is at least one log entry by viewing users
        mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) ).andReturn().getResponse()
                .getContentAsString();

        mvc.perform( get( "/api/v1/logentries" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        final Long id = LogEntry.getLogEntries().get( 0 ).getId();
        // Test getting a specific log entry.
        mvc.perform( get( "/api/v1/logentries/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Test getting a non-existent log entry
        mvc.perform( get( "/api/v1/logentries/-1" ) ).andExpect( status().isNotFound() );

        assertNotNull( mvc.perform( get( "/api/v1/logentriesforcurrentuser" ) ) );

        mvc.perform( get( "/api/v1/logentriesfordate/2018/02/14/2018/02/21" ) ).andExpect( status().isOk() );

        assertNotNull( mvc.perform( get( "/api/v1/toplogentriesforcurrentuser" ) ) );

        // (student) Test getting a top 10 log entries for user
        // objects = mvc.perform( get( "/api/v1/logentries/topTen/" +
        // "api_test_patient" ) ).andExpect( status().isOk() )
        // .andExpect( content().contentType(
        // MediaType.APPLICATION_JSON_UTF8_VALUE ) ).andReturn().getResponse()
        // .getContentAsString();
        //
        // objects.length(); // for checkstyle
    }

}
