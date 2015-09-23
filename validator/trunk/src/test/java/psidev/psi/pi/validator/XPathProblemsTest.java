package psidev.psi.pi.validator;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionList;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

/**
 * Test for checking if it is possible to get the cvParams from PDL which was
 * not possible due to the jmzIdentML library was not able to correctly process mzIdentML 1.2 schema version.
 * That's why we are currently using a custom version of jmzIdentML 1.1.10-SNAPSHOT (not released by EBI) May-8-2014
 * 
 * @author Salva
 */
public class XPathProblemsTest {

    /**
     * Constants.
     */
    private final String STR_FILE_SEPARATOR = System.getProperty("file.separator");
    private final String STR_MZID_TEST_FILE_NAME = STR_FILE_SEPARATOR + "src" + STR_FILE_SEPARATOR + "test" + STR_FILE_SEPARATOR + "resources" + STR_FILE_SEPARATOR + "mzidLib_rosetta_2a_uniprot_proteogrouped.mzid";

    /**
     * Members.
     */
    //private static final Logger logger = LoggerFactory.getLogger(XPathProblemsTest.class);
    
    /**
     * Constructor.
     */
    public XPathProblemsTest() {
        
    }
    
    /**
     * 
     */
    @BeforeClass
    public static void setUpClass() {
    }
    
    /**
     * 
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * 
     */
    @Before
    public void setUp() {
    }
    
    /**
     * 
     */
    @After
    public void tearDown() {
    }

    /**
     * Test for checking if it is possible to get the cvParams from PDL.
     */
    @Test
    public void testjMzIdentMLLib() {
        String workingDir = System.getProperty("user.dir");
        System.out.println("Working directory: " + workingDir);

        File mzIdentMLFile = new File(workingDir + STR_MZID_TEST_FILE_NAME);

        if (mzIdentMLFile.exists()) {
            MzIdentMLUnmarshaller unmarshaller = new MzIdentMLUnmarshaller(mzIdentMLFile);
            final Iterator<ProteinDetectionList> collection = unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.ProteinDetectionList);
            assertNotNull(collection);

            while (collection.hasNext()) {
                final ProteinDetectionList pdl = collection.next();
                System.out.println("PDL id=" + pdl.getId());
                assertEquals("PDL_1", pdl.getId());

                final List<CvParam> cvParam = pdl.getCvParam();
                assertTrue(cvParam.size() > 0);
                for (CvParam cvParam2 : cvParam) {
                    System.out.println(cvParam2.getAccession() + " " + cvParam2.getName());
                }
            }
        }
    }

    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        Result res = JUnitCore.runClasses(psidev.psi.pi.validator.XPathProblemsTest.class);
        
        for (Failure fail : res.getFailures()) {
            //logger.error(fail.toString());
        }
    }
}
