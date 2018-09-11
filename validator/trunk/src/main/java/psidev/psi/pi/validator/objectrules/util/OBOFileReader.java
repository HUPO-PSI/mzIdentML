package psidev.psi.pi.validator.objectrules.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for reading OBO files from local resource.
 * @author Gerhard Mayer, MPC, Ruhr-University of Bochum
 */
public class OBOFileReader {
    /**
     * Constants.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OBOFileReader.class);

    private static final String USER_DIR                = System.getProperty("user.dir");   // User's current working directory
    private static final String STR_TERM_LINE           = "[Term]";
    private static final String STR_ID_LINE             = "id: ";
    private static final String STR_NAME_LINE           = "name: ";
    private static final String STR_XREF_VALUE_TYPE_LINE= "xref: value-type:xsd\\:";
    
    private static final String STR_RESOURCES_OBOFILES  = "\\resources\\";
    private static final String STR_ONT_ID_MS   = "MS";
    private static final String STR_ONT_ID_XLMOD= "XLMOD";
    private static final String STR_POINT_OBO   = ".obo";
    private static final String STR_PSI_MS      = "psi-ms" + OBOFileReader.STR_POINT_OBO;
    private static final String STR_XLMOD       = "XLMOD" + OBOFileReader.STR_POINT_OBO;
    private static final String STR_EMPTY       = "";

    private static final char CHR_COLON     = ':';
    private static final int EXIT_FAILURE   = -1;
    
    /**
     * Members.
     */
    private static final HashMap<String, String> ID2_TERM_MAP       = new HashMap<>();  // contains the CV term names for the ID's
    private static final HashMap<String, Boolean> HAS_VALUE_TYPE_MAP= new HashMap<>();  // indicates, if a CV term should have a value
    private static boolean ALREADY_READ   = false;

    /**
     * Constructor.
     */
    public OBOFileReader() {
        OBOFileReader.initMaps();
    }
    
    /**
     * Initializes the maps.
     */
    private static void initMaps() {
        if (!OBOFileReader.ALREADY_READ) {
            OBOFileReader.readOboFileFromLocalFilePath(OBOFileReader.STR_ONT_ID_MS);
            OBOFileReader.readOboFileFromLocalFilePath(OBOFileReader.STR_ONT_ID_XLMOD);
            
            OBOFileReader.ALREADY_READ = true;
        }
    }

    /**
     * Reads in the .obo file from a local file path.
     * @param ontID
     * @param oboFilePath
     * @return BufferedReader
     */
    private static BufferedReader readOboFileFromLocalFilePath(String ontID) {
        String oboFilePath = OBOFileReader.getOBOFilePath(ontID);
        
        try {
            if (!oboFilePath.isEmpty()) {
                BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(oboFilePath)));
                OBOFileReader.readOBOFromBufferedReader(buff, ontID);
                return buff;
            }
            else {
                OBOFileReader.LOGGER.error("Unhandled case in readOboFileFromLocalFilePath(): {}", ontID);
                System.exit(OBOFileReader.EXIT_FAILURE);
            }
        }
        catch (IOException ioexc) {
            ioexc.printStackTrace(System.err);
        }
        
        return null;
    }
    
    /**
     * Gets the path to the .obo file
     * @param ontID         the ontology ID
     * @return the path to the .obo file
     */
    private static String getOBOFilePath(String ontID) {
        String localFilePath = OBOFileReader.USER_DIR + STR_RESOURCES_OBOFILES;
        
        if (ontID.equals(OBOFileReader.STR_ONT_ID_MS)) {
            localFilePath += OBOFileReader.STR_PSI_MS;
            return localFilePath;
        }
        if (ontID.equals(OBOFileReader.STR_ONT_ID_XLMOD)) {
            localFilePath += OBOFileReader.STR_XLMOD;
            return localFilePath;
        }
        else {
            return OBOFileReader.STR_EMPTY;
        }
    }
    
    /**
     * Reads an OBO file content from a BufferedReader.
     * @param buff  BufferedReader object
     * @param ontID the ontology ID
     */
    private static void readOBOFromBufferedReader(BufferedReader buff, String ontID) {
        try {
            boolean bHasValue = false;
            String line;
            String termID = OBOFileReader.STR_EMPTY;
            String cvTermName = OBOFileReader.STR_EMPTY;

            while ((line = buff.readLine()) != null) {
                if (line.startsWith(OBOFileReader.STR_TERM_LINE) && !termID.isEmpty()) {
                    OBOFileReader.storeCVInfos(ontID, termID, cvTermName, bHasValue);
                    termID = OBOFileReader.STR_EMPTY;
                }
                else if (line.startsWith(OBOFileReader.STR_NAME_LINE)) {
                    cvTermName = line.substring(OBOFileReader.STR_NAME_LINE.length()).trim();
                }
                else if (line.startsWith(OBOFileReader.STR_ID_LINE)) {
                    termID = line.substring(OBOFileReader.STR_ID_LINE.length()).trim();
                }
                else if (line.startsWith(OBOFileReader.STR_XREF_VALUE_TYPE_LINE)) {
                    bHasValue = true;
                }
            } // while
            buff.close();
            OBOFileReader.LOGGER.info(".obo file for {} read from local resource.", ontID);
        }
        catch(IOException ioexc) {
            ioexc.printStackTrace(System.err);
            OBOFileReader.LOGGER.debug("Error reading the .obo file with BufferedReader for {}", ontID); 
        }
    }

    /**
     * Stores the infos in the Hasmaps.
     * @param ontID         the ontology ID
     * @param termID        the CV term ID
     * @param cvTermName    the CV term name
     * @param bHasValue     true, if a CV term should have a value; else false
     */
    private static void storeCVInfos(String ontID, String termID, String cvTermName, boolean bHasValue) {
        String key = ontID + OBOFileReader.CHR_COLON + termID;

        OBOFileReader.ID2_TERM_MAP.put(key, cvTermName);
        OBOFileReader.HAS_VALUE_TYPE_MAP.put(key, bHasValue);
    }
    
    /**
     * Gets a CV term name for a the given ID's.
     * @param ontID     the ontology ID
     * @param termID    the CV term ID
     * @return the CV term name
     */
    public static String getCVTermNameFromID(String ontID, String termID) {
        OBOFileReader.initMaps();
        
        String key = ontID + OBOFileReader.CHR_COLON + termID;
        if (OBOFileReader.ID2_TERM_MAP.containsKey(key)) {
            return OBOFileReader.ID2_TERM_MAP.get(key);
        }
        else {
            return OBOFileReader.STR_EMPTY;
        }
    }

    /**
     * Checks, if a CV term name is correct.
     * @param ontID     the ontology ID
     * @param termID    the CV term ID
     * @param cvTermName    the CV term name
     * @return true, if the CV term name is correct; else false
     */
    public static boolean isValidCVTermName(String ontID, String termID, String cvTermName) {
        return cvTermName.equals(OBOFileReader.ID2_TERM_MAP.get(ontID + OBOFileReader.CHR_COLON + termID));
    }
    
    /**
     * Checks, if a CV term should have a value.
     * @param ontID     the ontology ID
     * @param termID    the CV term ID
     * @return true, if the CV term has a value; else false
     */
    public static boolean hasCVTermAValue(String ontID, String termID) {
        OBOFileReader.initMaps();
        
        String key = ontID + OBOFileReader.CHR_COLON + termID;
        return OBOFileReader.HAS_VALUE_TYPE_MAP.containsKey(key) && OBOFileReader.HAS_VALUE_TYPE_MAP.get(key);
    }
}
