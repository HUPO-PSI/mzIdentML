package psidev.psi.pi.validator;

import java.util.List;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;


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
    @BeforeAll
    public static void setUpClass() {
        URLEncodingTest.restClient = new RESTClient();
    }
    
    /**
     * Clean-up executed ONCE after all tests have finished.
     */
    @AfterAll
    public static void tearDownClass() {
        URLEncodingTest.restClient = null;
    }
    
    /**
     * Prepare test environment before each test.
     */
    @BeforeEach
    public void setUp() {
    }
    
    /**
     * Cleanup test environment after each test.
     */
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test for checking the URL encoding.
     */
    @Test
    public void testURLEncoding() {
        String olsAPIStr = AObjectRule.urlEncode("1001143");
        URLEncodingTest.LOGGER.debug("olsAPIStr: " + olsAPIStr);

        /*
        String response = restClient.callGET(olsAPIStr);
        URLEncodingTest.LOGGER.debug(response);
        */
    }

    /**
     * Main class for unit testing.
     * @param args 
     */
    public static void main(String[] args) {
        final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request().selectors(selectClass(URLEncodingTest.class)).build();
        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        
        TestExecutionSummary summary = listener.getSummary();
        URLEncodingTest.LOGGER.info(summary.getTestsFoundCount() + " Unit tests executed, " + summary.getTestsSucceededCount() + " of them were successful.");
        
        List<Failure> failures = summary.getFailures();
        failures.forEach(failure -> URLEncodingTest.LOGGER.error("failure - " + failure.getException().getMessage()));
    }
}
