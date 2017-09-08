package psidev.psi.pi.validator;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import psidev.psi.pi.validator.objectrules.AObjectRule;

/**
 * Test for encoding an URL for querying the new OLS REST API.
 * @author Gerhard Mayer
 */
public class URLEncodingTest {

    /**
     * Constants.
     */
    private static final Logger LOGGER = Logger.getLogger(URLEncodingTest.class.getName());

    /**
     * Members.
     */
    private static RESTClient restClient;
    
    /**
     * Constructor.
     */
    public URLEncodingTest() {
        super();
    }
    
    /**
     * Time-intensive initializations executed ONCE before the start of all tests.
     */
    @BeforeClass
    public static void setUpClass() {
        restClient = new RESTClient();
    }
    
    /**
     * Clean-up executed ONCE after all tests have finished.
     */
    @AfterClass
    public static void tearDownClass() {
        restClient = null;
    }
    
    /**
     * Prepare test environment before each test.
     */
    @Before
    public void setUp() {
    }
    
    /**
     * Cleanup test environment after each test.
     */
    @After
    public void tearDown() {
    }

    /**
     * Test for checking the URL encoding.
     */
    @Test
    public void testURLEncoding() {
        String olsAPIStr = AObjectRule.urlEncode("1001143");
        LOGGER.debug("olsAPIStr: " + olsAPIStr);

        /*
        String response = restClient.callGET(olsAPIStr);
        LOGGER.debug(response);
        */
    }

    /**
     * Main class for unit testing.
     * @param args 
     */
    public static void main(String[] args) {
        Result res = JUnitCore.runClasses(psidev.psi.pi.validator.URLEncodingTest.class);
        
        res.getFailures().forEach((fail) -> {
            LOGGER.error(fail.toString());
        });
    }
}
