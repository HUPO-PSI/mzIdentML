package psidev.psi.pi.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import psidev.psi.pi.rulefilter.RuleFilterManager;
import psidev.psi.pi.validator.objectrules.AdditionalSearchParamsObjectRule;
import psidev.psi.pi.validator.objectrules.MandatoryElementsObjectRule;
import psidev.psi.pi.validator.objectrules.ProteinAmbiguityGroupObjectRule;
import psidev.psi.pi.validator.objectrules.SearchTypeObjectRule;
import psidev.psi.pi.validator.objectrules.XLinkPeptideModificationObjectRule;
import psidev.psi.pi.validator.objectrules.XLinkSIIObjectRule;
import psidev.psi.tools.cvrReader.CvRuleReaderException;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccess;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.Validator;
import psidev.psi.tools.validator.ValidatorCvContext;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRuleManager;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.MzIdentMLObject;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;
import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

/**
 * @author Florian Reisinger Date: 25-Oct-2010 modified by Salvador Martínez, Gerhard Mayer
 * @since $version
 */
public class MzIdentMLValidator extends Validator {

    /**
     * Constants.
     */
    private final Logger LOGGER = LogManager.getLogger(MzIdentMLValidator.class);
    
    private final String STR_NOT_MATCHING_MSGS_RECV = "Not matching messages received: ";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String DOUBLE_NEW_LINE = NEW_LINE + NEW_LINE;
    private static final String TRIPLE_NEW_LINE = DOUBLE_NEW_LINE + NEW_LINE;
    private static final String TAB = "\t";
    private static final String DOUBLE_TAB = TAB + TAB;
    private static final String NEW_LINE_DOUBLE_TAB = NEW_LINE + DOUBLE_TAB;
    private final String STR_ELLIPSIS = "...";
    private static int progressSteps = 64;

    private final String STR_FILE_EXT_GZ    = ".gz";
    private final String STR_FILE_EXT_ZIP   = ".zip";
    private final String STR_FILE_EXT_7Z    = ".7z";
    
    /**
     * Enums.
     */
    public static enum MzIdVersion {
        _1_1, _1_2
    };

    /**
     * Members.
     */
    private MzIdentMLValidatorGUI gui = null;

    private MessageLevel msgLevel = MessageLevel.DEBUG;
    private HashMap<String, List<ValidatorMessage>> msgs = null;

    private URI schemaUri = null;
    private boolean skipSchemaValidation = false;

    private MzIdentMLUnmarshaller unmarshaller = null;
    private RuleFilterManager ruleFilterManager;
    private ExtendedValidatorReport extendedReport;

    public static MzIdVersion currentFileVersion = null;

    private int progress;
    private int cntMultipleClearedMessages;
    private int cntXMLSchemaValidatingMessages;
    private int cntUnanticipatedCVTerms = 0;
    private int cntXLInteractionScoringMessages = 0;
    
    /**
     * Constructor to initialise the validator with the custom ontology and cv-mapping without object rule settings.
     * 
     * @param ontoConfig
     *            the ontology configuration file.
     * @param aCvMappingFile
     *            the cv-mapping rule configuration file.
     * @param aCodedRuleFile
     *            the object rule configuration file
     * @param mzIdentMLValidatorGUI
     *            the GUI
     * @throws ValidatorException
     *             in case the validator encounters unexpected errors.
     * @throws OntologyLoaderException
     *             in case of problems while loading the needed ontologies.
     * @throws FileNotFoundException
     *             in case of any configuration file doesn't exist.
     * @throws CvRuleReaderException
     *             in case of problems while reading cv mapping rules.
     * 
     */
    public MzIdentMLValidator(InputStream ontoConfig, InputStream aCvMappingFile, InputStream aCodedRuleFile, MzIdentMLValidatorGUI mzIdentMLValidatorGUI)
            throws FileNotFoundException, ValidatorException, CvRuleReaderException, OntologyLoaderException {
        super(ontoConfig);

        this.gui = mzIdentMLValidatorGUI;
        this.checkOntologyAccess();

        final InputStream cvMappingFile = aCvMappingFile;
        final InputStream objectRuleFile = aCodedRuleFile;

        try {
            cvMappingFile.close();
            objectRuleFile.close();
        }
        catch (IOException e1) {
            e1.printStackTrace(System.err);
        }

        this.setObjectAndMappingRules(cvMappingFile, objectRuleFile);
        this.resetCountersAndGUI();
    }
    
    /**
     * Sets the object and mapping rules.
     * @param cvMappingFile
     * @param objectRuleFile 
     */
    private void setObjectAndMappingRules(InputStream cvMappingFile, InputStream objectRuleFile) {
        try {
            this.setObjectRules(objectRuleFile);
            this.setCvMappingRules(cvMappingFile);
        }
        catch (ValidatorException | CvRuleReaderException exc) {
            exc.printStackTrace(System.err);
        }
        
        this.LOGGER.info(this.getObjectRules().size() + " object rules");
        this.LOGGER.info(this.getCvRuleManager().getCvRules().size() + " cvMapping rules");
    }

    /**
     * 
     * Constructor to initialise the validator with the custom ontology.<br>
     * Default cv-mapping and object rule settings will be applied depending on
     * the "version" attribute of the file.<br>
     * 
     * @param ontoConfig
     *            the ontology configuration file.
     * @param mzIdentMLValidatorGUI
     *            the GUI
     * @throws ValidatorException
     *             in case the validator encounters unexpected errors.
     * @throws OntologyLoaderException
     *             in case of problems while loading the needed ontologies.
     * @throws FileNotFoundException
     *             in case of any configuration file doesn't exist.
     * @throws CvRuleReaderException
     *             in case of problems while reading cv mapping rules.
     * @throws UnknownHostException
     *             in case of problems locating the host for .obo loading.
     * 
     */
    public MzIdentMLValidator(InputStream ontoConfig, MzIdentMLValidatorGUI mzIdentMLValidatorGUI)
            throws OntologyLoaderException, FileNotFoundException, ValidatorException, CvRuleReaderException, UnknownHostException {
        super(ontoConfig);

        this.gui = mzIdentMLValidatorGUI;
        this.checkOntologyAccess();
        this.resetCountersAndGUI();
    }

    /**
     * Resets the counters and the GUI.
     * 
     * @param mzIdentMLValidatorGUI
     */
    private void resetCountersAndGUI() {
        this.resetCounters();
        this.resetAdditionalSearchParams();
        this.resetStaticObjectRuleMaps();
        this.setValidatorGUI(this.gui);
        this.msgs = new HashMap<>();
    }
    
    /**
     * Rsets the flags for all additional search parameters.
     */
    private void resetAdditionalSearchParams() {
        SearchTypeObjectRule.bIsDeNovoSearch = false;
        SearchTypeObjectRule.bIsSpectralLibrarySearch = false;
        
        AdditionalSearchParamsObjectRule.bIsPeptideLevelScoring = false;
        AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch = false;
        AdditionalSearchParamsObjectRule.bIsModificationLocalizationScoring = false;
        AdditionalSearchParamsObjectRule.bIsConsensusScoring = false;
        AdditionalSearchParamsObjectRule.bIsSamplePreFractionation = false;
        AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch = false;
        AdditionalSearchParamsObjectRule.bIsNoSpecialProcessing = false;
    }

    /**
     * Resets the (static) maps of the object rules.
     */
    private void resetStaticObjectRuleMaps() {
        ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.clear();
    }
    
    /**
     * Checks, if the ontology files can be accessed.
     */
    private void checkOntologyAccess() {
        String[] ontologies = { "GO", "MS", "MOD", "UO", "UNIMOD", "BTO", "PATO" };
        for (String ontology : ontologies) {
            final OntologyAccess ontologyAccess = this.ontologyMngr.getOntologyAccess(ontology);
            if (ontologyAccess == null) {
                this.LOGGER.error("Error loading " + ontology + " ontology");
            }
        }
    }

    /**
     * Gets the appropriate {@link URI} schema depending on parameter
     * 
     * @param version
     * @return the URI to the schema
     * @throws ValidatorException
     */
    private URI getMzIdentMLSchema(MzIdVersion version) throws ValidatorException {
        switch (version) {
            case _1_1:
                return this.getMzIdentMLSchema("mzIdentML1.1.0.xsd", "url.schema.1.1.0");
            case _1_2:
                return this.getMzIdentMLSchema("mzIdentML1.2.0.xsd", "url.schema.1.2.0");
            default:
                throw new ValidatorException("Not supported mzIdentML version: " + version);
        }
    }

    /**
     * Gets the URI for a XML schema.
     * Gets the {@link URI} of the XML schema looking for a local resource or for the remote URL in the validation.properties file.
     * 
     * @param schemaName
     * @param schemaProperty
     * @return the URI to the schema
     * @throws ValidatorException
     */
    private URI getMzIdentMLSchema(String schemaName, String schemaProperty) throws ValidatorException {
        URL url = this.getClass().getClassLoader().getResource(schemaName);
        if (url == null) {
            this.LOGGER.debug("Trying the remote official version of the schema");
            try {
                url = new URL(MzIdentMLValidatorGUI.getProperty(schemaProperty));
                if (!this.checkURLAvailability(url))
                    throw new ValidatorException("Could not reach URL: " + url);
            }
            catch (MalformedURLException e) {
                e.printStackTrace(System.err);
                throw new ValidatorException("Could not create URI for mzIdentML schema location! URI: " + this.schemaUri, e);
            }
        }
        try {
            return url.toURI();
        }
        catch (URISyntaxException e) {
            throw new ValidatorException("Could not create URI for mzIdentML schema location! URI: " + this.schemaUri, e);
        }
    }

    /**
     * Check {@link URL} availability checking the response connection.
     * 
     * @param url
     * @return true if the response is 200
     */
    private boolean checkURLAvailability(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true;
            }
        }
        catch (IOException e) {
            this.LOGGER.warn(e.getMessage());
        }
        
        return false;
    }

    /**
     * Sets the Graphical User Interface.
     * @param validatorGUI
     *            MzMLValidatorGUI that acts as the GUI parent of this
     *            validator. Can be 'null' if ran from the command-line.
     */
    public final void setValidatorGUI(MzIdentMLValidatorGUI validatorGUI) {
        this.gui = validatorGUI;
        this.progress = 0;
    }

    /**
     * Sets the message level for the reports.
     * @param level MessageLevel with the minimal message level to report.
     */
    public void setMessageReportLevel(MessageLevel level) {
        this.msgLevel = level;
    }

    /**
     * Get the reporting MessageLevel from which to report. Note: Only messages
     * produced by the validation process that are of equal or higher severity
     * than this MessageLevel will be reported. Others will not be reported,
     * even if produced during validation.
     * 
     * @return the current MessageLevel.
     */
    public MessageLevel getMessageReportLevel() {
        return this.msgLevel;
    }

    /**
     * Get the currently specified mzIdentML schema URI the instance files are
     * validated against.
     * 
     * @return the URI pointing to the mzIdentML schema.
     */
    public URI getSchemaUri() {
        return this.schemaUri;
    }

    /**
     * Use this to overwrite the default schema location and specify your own
     * mzIdentML schema to validate against.
     * 
     * @param schemaUri
     *            the URI that points to the schemas.
     */
    public void setSchemaUris(URI schemaUri) {
        this.schemaUri = schemaUri;
    }

    /**
     * Flag to skip the schema validation step.
     * 
     * @return true if the schema validaton step will be skipped with the
     *         current settings, false if a schema validation will be performed.
     */
    public boolean isSkipSchemaValidation() {
        return this.skipSchemaValidation;
    }

    /**
     * Flag to specify if a schema validation is to be performed.
     * 
     * @param skipSchemaValidation
     *            set to true if the schema validation step should be skipped.
     */
    public void setSkipSchemaValidation(boolean skipSchemaValidation) {
        this.skipSchemaValidation = skipSchemaValidation;
    }

    /**
     * Get extended report
     * 
     * @return the extended report
     */
    public ExtendedValidatorReport getExtendedReport() {
        if (this.extendedReport != null) {
            if (this.extendedReport.getTotalCvRules() == 0) {
                this.extendedReport.setCvRules(this.getCvRuleManager().getCvRules(), this.ruleFilterManager, this.msgs);
            }
        }
        
        return this.extendedReport;
    }
    
    /**
     * 
     * @param xmlFile the mzIdentML file to validate.
     * @param unzippedPath  the path to the unzipped file
     * @param extension the file extension (.gzip or .zip)
     * @return the unzipped XML file
     */
    private File unzipXMLFile(File xmlFile, String unzippedPath, String extension) {
        File unzippedFile = new File(unzippedPath);
        switch (extension) {
            case STR_FILE_EXT_GZ:
                ArchiveUnpacker.unGzipFile(xmlFile.getPath(), unzippedFile.getPath());
                break;
            case STR_FILE_EXT_ZIP:
                ArchiveUnpacker.unZipFile(xmlFile.getPath(), unzippedFile.getPath());
                break;
            case STR_FILE_EXT_7Z:
                ArchiveUnpacker.decompress7zFile(xmlFile.getPath(), unzippedFile.getPath());
                break;
            default:
                break;
        }
        
        xmlFile = unzippedFile;
        
        return xmlFile;
    }

    /**
     * Performs the actual validation, including schema validation (if not
     * turned off), validation against the CV-mapping rules and validation
     * against the registered ObjectRules.
     * 
     * @param xmlFile the mzIdentML file to validate.
     * @return a Collection of ValidatorMessages documenting the validation result.
     */
    public Collection<ValidatorMessage> startValidation(File xmlFile) {
        String unzippedPath;
        
        if (xmlFile.getName().endsWith(this.STR_FILE_EXT_GZ)) {
            unzippedPath = xmlFile.getPath().replace(this.STR_FILE_EXT_GZ, this.gui.STR_EMPTY);
            xmlFile = this.unzipXMLFile(xmlFile, unzippedPath, this.STR_FILE_EXT_GZ);
        }
        else if (xmlFile.getName().endsWith(this.STR_FILE_EXT_ZIP)) {
            unzippedPath = xmlFile.getPath().replace(this.STR_FILE_EXT_ZIP, this.gui.STR_EMPTY);
            xmlFile = this.unzipXMLFile(xmlFile, unzippedPath, this.STR_FILE_EXT_ZIP);
        }
        
        Collection<ValidatorMessage> locMsgs = this.makeBasicXMLFileChecks(xmlFile);
        if (locMsgs != null) {
            return locMsgs;
        }
        else {
            this.initGuiProgress();

            this.updateProgress("Indexing input file" + this. STR_ELLIPSIS);
            this.unmarshaller = new MzIdentMLUnmarshaller(xmlFile);
            String mzIdentMLVersion = this.unmarshaller.getMzIdentMLVersion();

            // flag if the version has changed
            MzIdVersion currentFileVersionTMP = this.getMzIdentMLVersion(mzIdentMLVersion);
            boolean versionChange = false;
            if (MzIdentMLValidator.currentFileVersion != null && MzIdentMLValidator.currentFileVersion != currentFileVersionTMP) {
                versionChange = true;
            }
            this.LOGGER.debug("MzIdentML file version set to :" + MzIdentMLValidator.currentFileVersion);
            MzIdentMLValidator.currentFileVersion = currentFileVersionTMP;

            try {
                // in case of not having a cvRule manager, load the rules depending
                // on the version of the file or if the version of the file has changed
                if (this.getCvRuleManager() == null || versionChange) {
                    this.loadRulesByMzIdentVersion();
                }

                // Reset old validation results. This will currently reset the status of all CvRules to a "not run" status
                super.resetCvRuleStatus();

                this.extendedReport = new ExtendedValidatorReport(this.getObjectRules());

                // reset the WhiteListHack (hack to find terms that are not covered by the CvMapping)
                ValidatorCvContext.getInstance().resetRecognised();
                ValidatorCvContext.getInstance().resetNotRecognised();

                // XML Schema validation
                this.schemaValidation(xmlFile);
                boolean bSchemaCompliant = this.logSchemaValidationErrors();
                if (!bSchemaCompliant) {
                    this.LOGGER.warn("Please make the .mzid file first schema compliant.");
                }
                
                // ---------------- Internal consistency check of the CvMappingRules
                // Validate CV Mapping Rules
                this.updateProgress("Checking internal consistency of CV rules" + this. STR_ELLIPSIS);
                if (this.gui != null && !this.gui.skipCvRulesChecking()) {
                    this.addMessages(this.checkCvMappingRules(), this.msgLevel);
                }
            }
            catch (ValidatorException ve) {
                this.LOGGER.error("Exceptions during validation!", ve);
                ve.printStackTrace(System.err);
            }

            System.out.println("Number of rules to check: " + this.getCvRuleManager().getCvRules().size());
            this.doValidationWork();

            this.updateProgress("Validation complete, compiling output" + this. STR_ELLIPSIS);
            this.checkForNonAnticipatedCvTerms();

            return this.filterAndClusterMessages();
        }
    }

    /**
     * Makes some simple test, e.g. if it's an non-empty and valid mzIdentML file
     * @param xmlFile the mzIdentML file to validate.
     * @return a Collection of ValidatorMessages documenting the validation result.
     */
    private Collection<ValidatorMessage> makeBasicXMLFileChecks(File xmlFile) {
        if (xmlFile.length() > 0) {
            if (this.LOGGER.isInfoEnabled()) {
                this.LOGGER.info(NEW_LINE + "Starting new validation, input file: " + xmlFile.getAbsolutePath());
            }
            try {
                BufferedReader fr = new BufferedReader(new FileReader(xmlFile));
                if (!fr.readLine().startsWith("<?xml ")) {
                    return this.getInvalidOrEmptyFileErrorMessages(xmlFile, " is not a XML file (XML declaration missing).");
                }
                else {
                    String line;
                    do { // skip comment lines
                        line = fr.readLine();
                    }
                    while (line.startsWith("<!--"));
                    
                    if (!line.startsWith("<MzIdentML ")) {
                        return this.getInvalidOrEmptyFileErrorMessages(xmlFile, " is not a mzIdentML file.");
                    }
                }
            }
            catch (IOException exc) {
                exc.printStackTrace(System.err);
            }
            catch (NullPointerException exc) {
                return this.getInvalidOrEmptyFileErrorMessages(xmlFile, " seems to be uncomplete or errorneous.");
            }
        }
        else {
            return this.getInvalidOrEmptyFileErrorMessages(xmlFile, " is empty.");
        }
        
        return null;
    }
    
    /**
     * Gets error messages in case of an empty or invalid file.
     * @param xmlFile the mzIdentML file to validate.
     * @param msgSuffix the end of the error message.
     * @return a Collection of ValidatorMessages documenting the validation result.
     */
    private Collection<ValidatorMessage> getInvalidOrEmptyFileErrorMessages(File xmlFile, String msgSuffix) {
        String errStr = "The file " + xmlFile.getName() + msgSuffix;
        System.err.println(errStr);

        final Collection<ValidatorMessage> clusteredMessages = new ArrayList<>();
        clusteredMessages.add(new ValidatorMessage(errStr, MessageLevel.ERROR));
        return clusteredMessages;
    }

    /**
     * Logs the errors from schema validation.
     * @return true, if validation should proceed; else false
     */
    private boolean logSchemaValidationErrors() {
        boolean bRet = true;
            
        if (!this.msgs.isEmpty()) {
            System.err.println(DOUBLE_NEW_LINE + "There were errors validating against the XML schema:" + NEW_LINE);
            String msg;
            for (ValidatorMessage lMessage : this.getMessageCollection()) {
                msg = TAB + " - " + lMessage;
                System.err.println(msg);
                this.LOGGER.warn(msg);
                this.extendedReport.addInvalidSchemaValidationMessage(msg);
                bRet = false;
            }
            this.cntXMLSchemaValidatingMessages++;
        }
        
        return bRet;
    }
    
    /**
     * Does the core validation work.
     */
    private void doValidationWork() {
        try {
            this.checkMandatoryElements();
            this.applyObjectRules();
            this.applyCVMappingRules();
        }
        catch (ValidatorException ve) {
            this.LOGGER.error("Exceptions during validation!", ve);
            ve.printStackTrace(System.err);
        }
    }

    /**
     * Filters and clusters the messages.
     * 
     * @return Collection<>
     */
    private Collection<ValidatorMessage> filterAndClusterMessages() {
        // If ruleFilterManager is enabled, filter the messages. Anyway, cluster the messages
        final Collection<ValidatorMessage> clusteredMessages;
        if (this.ruleFilterManager != null) {
            clusteredMessages = this.clusterByMessagesAndRules(this.ruleFilterManager.filterValidatorMessages(this.msgs, this.extendedReport));
        }
        else {
            // or return all messages for semantic validation
            clusteredMessages = this.clusterByMessagesAndRules(this.getMessageCollection());
        }
        
        return clusteredMessages;
    }
    
    /**
     * Check for terms that were not anticipated with the rules in the CV mapping file.
     */
    private void checkForNonAnticipatedCvTerms() {
        ValidatorMessage valMsg;
        List<ValidatorMessage> unrecognisedTermsForXPath = new ArrayList<>();
        String msgText;
        int cnt = 1;
        
        for (String xpath : ValidatorCvContext.getInstance().getNotRecognisedXpath()) {
            Set<String> list = ValidatorCvContext.getInstance().getNotRecognisedTerms(xpath);
            if (list != null && !list.isEmpty()) {
                
                Set<String> filteredList = this.filterOutUnanticipatedCvTerms(list);
                
                if (filteredList.size() > 0) {
                    msgText = "unanticipated terms for XPath '" + xpath + "' : " + filteredList;
                    System.out.println(msgText);
                    if (this.gui.jCheckBoxShowUnanticipatedCVTerms.isSelected()) {
                        valMsg = new ValidatorMessage(msgText, MessageLevel.WARN);
                        unrecognisedTermsForXPath.add(valMsg);
                        this.addMessages(unrecognisedTermsForXPath, MessageLevel.WARN);

                        List<ValidatorMessage> msgList = new ArrayList<>();
                        msgList.add(valMsg);
                        String ruleId = "Unanticipated CV term " + cnt++;
                        this.msgs.put(ruleId, msgList);
                        this.cntUnanticipatedCVTerms++;
                    }
                }
            }
        }
    }
    
    /**
     * Filters out unanticipated messages, that are allowed in sepcial cases..
     * @param list  the list of unanticipated messages
     * @return the filtered list of unanticipated messages
     */
    private Set<String> filterOutUnanticipatedCvTerms(Set<String> list) {
        Set<String> filteredList = new HashSet<>();
        
        if (AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch) {
            for (String split: list) 
                if (!split.equals("MS:1002635") &&
                    !split.equals("MS:1002637") &&
                    !split.equals("MS:1002638") &&
                    !split.equals("MS:1002640") &&
                    !split.equals("MS:1002641") &&
                    !split.equals("MS:1002642") &&
                    !split.equals("MS:1002643") &&
                    !split.equals("MS:1002644")) {
                filteredList.add(split);
            }
            this.LOGGER.debug("Filtered unanticipated CV terms for ProteoGenomicsSearch");
        }
        else if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            for (String split: list) 
                if (!split.startsWith("XLMOD:")) {
                filteredList.add(split);
            }
            this.LOGGER.debug("Filtered unanticipated CV terms for CrossLinkingSearch");
        }
        else {
            filteredList = list;
        }
        
        return filteredList;
    }
    
    /**
     * Load the appropriate rules (semantic or MIAPE, 1.1 or 1.2) according to
     * the mzIdentML version
     * 
     * @throws ValidatorException
     *             if not gui has been setup and if some problem occurs while
     *             loading rules from configuration files
     */
    private void loadRulesByMzIdentVersion() throws ValidatorException {
        this.LOGGER.info("Loading configuration files");
        this.updateProgress("Loading configuration files" + this. STR_ELLIPSIS);

        if (this.gui != null) {
            try {
                final InputStream objectRuleInputStream = this.gui.getRuleFileInputStream(MzIdentMLValidator.currentFileVersion, this.gui.STR_OBJECT);
                final InputStream mappingRuleInputStream = this.gui.getRuleFileInputStream(MzIdentMLValidator.currentFileVersion, this.gui.STR_MAPPING);
                
                this.setCvMappingRules(mappingRuleInputStream);
                this.setObjectRules(objectRuleInputStream);
            }
            catch (FileNotFoundException e) {
                throw new ValidatorException("Error loading configuration files.", e);
            }
            catch (CvRuleReaderException e) {
                throw new ValidatorException("Error loading cvMapping rules.", e);
            }
        }
        else {
            throw new ValidatorException("No GUI has been specified, which is needed to know which type of validation (semantic or MIAPE) is going to be performed.");
        }
    }

    /**
     * Validates a XML file against a XMl schema definition.
     * @param xmlFile
     * @return Collection<>
     */
    @SuppressWarnings("unchecked")
    private Collection<ValidatorMessage> schemaValidation(File xmlFile) {
        // first up we check if the file is actually valid against the schema (if not disabled)
        if (this.skipSchemaValidation) {
            this.updateProgress("Skipping schema validation!");
            try {
                // sleep for a second to give the user time to see this important message
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // if we are interrupted (which should not happen) we just go on.
            }
        }
        else { // validating
            this.updateProgress("Validating against schema (depending on the file size, this might take a while)" + this. STR_ELLIPSIS);
            boolean schemaValid = false;
            
            try {
                this.schemaUri = this.getMzIdentMLSchema(MzIdentMLValidator.currentFileVersion);
                schemaValid = this.isValidmzIdentMLXml(xmlFile, this.schemaUri);
            }
            catch (ValidatorException | SAXException e) {
                this.addSchemaValidationError(e);
            }

            // handle schema validation errors
            if (!schemaValid) {
                if (this.gui != null) {
                    return this.clusterByMessagesAndRules(this.getMessageCollection());
                }
                else {
                    System.err.println("The provided file is not valid against the mzIdentML schema!");
                    System.err.println("Input file     : " + xmlFile.getAbsolutePath());
                    System.err.println("Schema location: " + this.schemaUri);
                    this.getMessageCollection().stream().map((msg) -> {
                        System.out.println(msg.getMessage());
                        return msg;
                    }).forEach((msg) -> {
                        this.extendedReport.addInvalidSchemaValidationMessage(msg.getMessage());
                    });
                    System.exit(MzIdentMLValidatorGUI.EXIT_FAILURE);
                }
            }
            System.out.println("XML schema validation complete, file valid against .xsd schema.");
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * Adds a schema validation error message.
     * @param exc 
     */
    private void addSchemaValidationError(Exception exc) {
        this.LOGGER.error("ERROR during schema validation.", exc);
        
        String msg = "ERROR during schema validation: " + exc.getMessage();
        ValidatorMessage valMessage = new ValidatorMessage(msg, MessageLevel.ERROR);
        
        this.extendedReport.addInvalidSchemaValidationMessage(msg);
        this.addValidatorMessage("Schema Validation error", valMessage, this.msgLevel);
    }
    
    /**
     * Parse input string to know which mzIdentML version it is.
     * 
     * @param mzIdentMLVersion
     * @return the mzid version
     */
    private MzIdVersion getMzIdentMLVersion(String mzIdentMLVersion) {
        switch (mzIdentMLVersion) {
            case "1.1.0":
            case "1.1":
                return MzIdentMLValidator.MzIdVersion._1_1;
            case "1.2.0":
            case "1.2":
                return MzIdentMLValidator.MzIdVersion._1_2;
        }
        
        return null;
    }

    /**
     * Check for the presence of all mandatory elements required at this validation type.
     */
    private void checkMandatoryElements() {
        if (this.ruleFilterManager != null) {
            this.updateProgress("Checking mandatory elements" + this. STR_ELLIPSIS);
            final List<String> mandatoryElements = this.ruleFilterManager.getMandatoryElements();
            mandatoryElements.stream().map((elementName) -> this.getMzIdentMLElement(elementName)).forEach((mzMLElement) -> {
                // check if that element is present on the file
                try {
                    final MzIdentMLObject mzIdentMLObject = this.unmarshaller.unmarshal(mzMLElement);
                    if (mzIdentMLObject == null) {
                        final MandatoryElementsObjectRule mandatoryObjectRule = new MandatoryElementsObjectRule(this.ontologyMngr);
                        final ValidatorMessage validatorMessage = new ValidatorMessage(
                            "The element on xPath:'" + mzMLElement.getXpath() + "' is required for the current type of validation.",
                            MessageLevel.ERROR, new Context(mzMLElement.getXpath()), mandatoryObjectRule);
                        // extendedReport.objectRuleExecuted(mandatoryObjectRule, validatorMessage);
                        // this.addObjectRule(mandatoryObjectRule);
                        this.addValidatorMessage(validatorMessage.getRule().getId(), validatorMessage, this.msgLevel);
                    }
                }
                catch (IllegalStateException exc) {
                    this.LOGGER.debug("Can not unmarshall the .mzid file element " + mzMLElement.getClass().getName() + " because the XML file is not valid against the schema.");
                    exc.printStackTrace(System.err);
                }
            });
        }
    }

    /**
     * Gets a specified element from the mzIdentML file.
     * 
     * @param elementName
     * @return MzIdentMLElement
     */
    private MzIdentMLElement getMzIdentMLElement(String elementName) {
        for (MzIdentMLElement element : MzIdentMLElement.values()) {
            if (element.name().equals(elementName)) {
                return element;
            }
        }
        
        return null;
    }

    /**
     * Applies and checks all object rules.
     * 
     * @throws ValidatorException 
     */
    private void applyObjectRules() throws ValidatorException {
        int objectRulesChecked;
        
        long startTime = System.currentTimeMillis();

        this.checkElementObjectRule(MzIdentMLElement.SpectrumIdentificationProtocol); // should stand on the beginning
        
        this.checkElementObjectRule(MzIdentMLElement.CvList);
        this.checkElementObjectRule(MzIdentMLElement.CvParam);
        this.checkElementObjectRule(MzIdentMLElement.Param);
        
        if (this.gui.isMIAPEValidationSelected()) {
            this.checkElementObjectRule(MzIdentMLElement.AnalysisSoftware);
            this.checkElementObjectRule(MzIdentMLElement.Provider);
        }
        this.checkElementObjectRule(MzIdentMLElement.Person);
        this.checkElementObjectRule(MzIdentMLElement.Organization);
        this.checkElementObjectRule(MzIdentMLElement.PeptideEvidence);
        this.checkElementObjectRule(MzIdentMLElement.Peptide);
        this.checkElementObjectRule(MzIdentMLElement.SearchModification);
        if (this.gui.isMIAPEValidationSelected()) {
            this.checkElementObjectRule(MzIdentMLElement.Enzyme);
        }
        this.checkElementObjectRule(MzIdentMLElement.ProteinDetectionList);
        this.checkElementObjectRule(MzIdentMLElement.ProteinAmbiguityGroup);
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            this.checkXLInterActionScorePairing();
        }
        objectRulesChecked = this.checkElementObjectRule(MzIdentMLElement.SpectrumIdentificationItem);

        if (MzIdentMLValidator.currentFileVersion == MzIdentMLValidator.MzIdVersion._1_2) {
            this.checkElementObjectRule(MzIdentMLElement.DBSequence);
            //this.checkElementObjectRule(MzIdentMLElement.SpectrumIdentificationList);
            objectRulesChecked = this.checkElementObjectRule(MzIdentMLElement.SpectrumIdentificationResult);
            //objectRulesChecked = this.checkElementObjectRule(MzIdentMLElement.ProteinDetectionHypothesis);
        }

        this.LOGGER.debug(objectRulesChecked + " object rules checked.");
        this.LOGGER.debug("Object Rule validation done in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Checks the pairing of the cross-linking interaction scores (only in cross-linking case).
     */
    private void checkXLInterActionScorePairing() {
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            int cnt = 0;
            for (ImmutablePair<String, String> key: ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.keySet()) {
                HashMap<String, String> pagID2PDHID_Map = ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.get(key);
                
                if (pagID2PDHID_Map.size() != 2) {
                    String msgText = this.getXLInteractionScoreMsg(key, pagID2PDHID_Map);
                    this.addValidatorMessage("XL interaction scoring " + cnt++, new ValidatorMessage(msgText, MessageLevel.ERROR), this.msgLevel);
                    this.cntXLInteractionScoringMessages++;
                }
            }
            
            this.gui.bHasXLErrors = this.cntXLInteractionScoringMessages > 0;
        }
    }
    
    /**
     * Gets the message text for a cross-linking interaction score message.
     * @param key               mapping from xlInteractID to score
     * @param pagID2PDHID_Map   mapping from the PAG-ID to the PDH-ID
     * @return the error message
     */
    private String getXLInteractionScoreMsg(ImmutablePair<String, String> key, HashMap<String, String> pagID2PDHID_Map) {
        StringBuilder strB = new StringBuilder();
        
        String pagIDs_pdhIDs = this.gui.STR_EMPTY;
        String pdhID;
        for (String pagID : pagID2PDHID_Map.keySet()) {
            pdhID = pagID2PDHID_Map.get(pagID);
            pagIDs_pdhIDs += "PAG: " + pagID + " and PDH: " + pdhID + TAB;
        }                    
        strB.append("Interaction score is not paired for XL interaction ID ");
        strB.append(key.left).append(" and score ").append(key.right);
        strB.append(" (has only ").append(pagID2PDHID_Map.size()).append(" entries for : ").append(pagIDs_pdhIDs);

        return strB.toString();
    }
    
    /**
     * Applies all Cv mapping rules (except the ones for SII's).
     * Retrieve the XML snippets we want to check and validate them against the CV rules.
     * WARNING: if more element validations are added the GUI progress counter for initGuiProgress() (progressSteps) has to be updated!
     * 
     * @throws ValidatorException 
     */
    private void applyCVMappingRules() throws ValidatorException {
        this.LOGGER.debug("Validating against the CV mapping Rules" + this. STR_ELLIPSIS);
        long start = System.currentTimeMillis();

        this.checkElementCvMapping(MzIdentMLElement.CvList);
        this.checkElementCvMapping(MzIdentMLElement.AnalysisSoftware);
        this.checkElementCvMapping(MzIdentMLElement.Provider);
        this.checkElementCvMapping(MzIdentMLElement.Role);
        this.checkElementCvMapping(MzIdentMLElement.AuditCollection);
        this.checkElementCvMapping(MzIdentMLElement.Person);
        this.checkElementCvMapping(MzIdentMLElement.Organization);
        this.checkElementCvMapping(MzIdentMLElement.AnalysisSampleCollection);
        this.checkElementCvMapping(MzIdentMLElement.Sample);
        this.checkElementCvMapping(MzIdentMLElement.SequenceCollection);
        this.checkElementCvMapping(MzIdentMLElement.DBSequence);
        this.checkElementCvMapping(MzIdentMLElement.Peptide);
        this.checkElementCvMapping(MzIdentMLElement.PeptideEvidence);
        this.checkElementCvMapping(MzIdentMLElement.SpectrumIdentification);
        this.checkElementCvMapping(MzIdentMLElement.SpectrumIdentificationProtocol);
        this.checkElementCvMapping(MzIdentMLElement.Enzyme);
        this.checkElementCvMapping(MzIdentMLElement.MassTable);
        this.checkElementCvMapping(MzIdentMLElement.AmbiguousResidue);
        this.checkElementCvMapping(MzIdentMLElement.Filter);
        this.checkElementCvMapping(MzIdentMLElement.TranslationTable);
        this.checkElementCvMapping(MzIdentMLElement.ProteinDetectionProtocol);
        this.checkElementCvMapping(MzIdentMLElement.Inputs);
        this.checkElementCvMapping(MzIdentMLElement.SourceFile);
        this.checkElementCvMapping(MzIdentMLElement.SearchDatabase);
        this.checkElementCvMapping(MzIdentMLElement.SearchModification);
        this.checkElementCvMapping(MzIdentMLElement.SpectraData);
        this.checkElementCvMapping(MzIdentMLElement.SpectrumIDFormat);
        this.checkElementCvMapping(MzIdentMLElement.SpecificityRules);
        this.checkElementCvMapping(MzIdentMLElement.SpectrumIdentificationList);    // this includes SIR and SII
        this.checkElementCvMapping(MzIdentMLElement.FragmentationTable);
        this.checkElementCvMapping(MzIdentMLElement.Measure);
        if (MzIdentMLValidator.currentFileVersion == MzIdentMLValidator.MzIdVersion._1_2) {
            this.checkElementCvMapping(MzIdentMLElement.ProteinDetectionList);
        }
        this.checkElementCvMapping(MzIdentMLElement.ProteinAmbiguityGroup);
        // disabled because is included in the SIL
        // this.checkElementCvMapping(MzIdentMLElement.SpectrumIdentificationResult);
        
        if (MzIdentMLValidator.currentFileVersion == MzIdentMLValidator.MzIdVersion._1_2) {
            this.checkElementCvMapping(MzIdentMLElement.ProteinDetectionHypothesis);
        }
        
        this.applyCvMappingRulesForSIIsInParallel();
        
        this.LOGGER.info("CV mapping validation done in " + (System.currentTimeMillis() - start) + "ms.");
    }

    /**
     * Threaded application of all CV mapping rules for SpectrumIdentificationItem's in parallel.
     * 
     * @throws ValidatorException 
     */
    private void applyCvMappingRulesForSIIsInParallel() throws ValidatorException {
        // validation of each SpectrumIdentificationItem is done element by element potentially on multiple threads in parallel
        this.updateProgress("Validating /../../../../SpectrumIdentificationResult/SpectrumIdentificationItem" + this. STR_ELLIPSIS);
        Iterator<SpectrumIdentificationItem> mzIdentMLIter;
        mzIdentMLIter = this.unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SpectrumIdentificationItem);

        // check the first SII to see if some rule is applied to these objects
        try {
            final SpectrumIdentificationItem sii = mzIdentMLIter.next();
            List<SpectrumIdentificationItem> siis = new ArrayList<>();
            siis.add(sii);
            this.checkCvMapping(siis, MzIdentMLElement.SpectrumIdentificationItem.getXpath());
        }
        catch (IllegalArgumentException e) {
            this.LOGGER.info(e.getMessage());
            return;
        }

        // Get again the iterator over the SpectrumIdentificationItems.
        mzIdentMLIter = this.unmarshaller.unmarshalCollectionFromXpath(MzIdentMLElement.SpectrumIdentificationItem);
        // create synchronized List to which all threads can write their Validator messages
        Map<String, List<ValidatorMessage>> sync_msgs = Collections.synchronizedMap(new HashMap<>());

        // Create lock.
        InnerLock lock = new InnerLock();
        InnerIteratorSync<SpectrumIdentificationItem> iteratorSync = new InnerIteratorSync<>(mzIdentMLIter);
        // !! Note: this part is specific to the SpectrumIdentificationItem!
        // So the XPath is hard coded in the InnerSpecValidator!
        Collection<InnerSpecValidator> runners = new ArrayList<>();
        int processorCount = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processorCount; i++) {
            InnerSpecValidator<SpectrumIdentificationItem> runner = new InnerSpecValidator<>(iteratorSync, lock, i);
            runners.add(runner);
            new Thread(runner).start();
        }

        // Wait for it.
        lock.isDone(runners.size());
        
        // now we add all the collected messages from the spectra validators to the general message list
        this.addSyncMessages(sync_msgs, this.msgLevel);        
    }
    
    /**
     * Checks an object rule for an element.
     * @param element the element to check
     * @return the number of checked object rules
     * @throws ValidatorException 
     */
    private int checkElementObjectRule(MzIdentMLElement element) throws ValidatorException {
        if (this.gui != null) {
            this.gui.setProgress(++this.progress, "Validating " + element.getXpath() + this.STR_ELLIPSIS);
        }
        Iterator<MzIdentMLObject> mzIdentMLIter = this.unmarshaller.unmarshalCollectionFromXpath(element);

        Collection<ValidatorMessage> objectRuleResult = new ArrayList<>();
        if (element.getXpath() == null) {
            this.LOGGER.warn("XPath is null for element " + element.name());
        }
        else if (!mzIdentMLIter.hasNext()) {
            this.LOGGER.warn(element.getXpath() + " is not present. Maybe is because it is not indexed?");
        }
        else {
            while (mzIdentMLIter.hasNext()) {
                try {
                    final MzIdentMLObject next = mzIdentMLIter.next();
                    final Collection<ValidatorMessage> validationResult = this.validate(next);
                    if (validationResult != null && !validationResult.isEmpty())
                        objectRuleResult.addAll(validationResult);
                }
                catch (IllegalArgumentException e) {
                    this.LOGGER.warn(e.getMessage());
                    // if exception is thrown is because there is no rule to check this mzIdentML elements, so break the loop
                    break;
                }
            }
        }

        // Special handling: Now check the results for the cross-linking case
        if (MzIdentMLValidator.currentFileVersion == MzIdentMLValidator.MzIdVersion._1_2) {
            if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
                if (element.getClazz().getName().endsWith("SpectrumIdentificationResult")) {
                    objectRuleResult.addAll(XLinkSIIObjectRule.checkRulesWithHashMapContent());
                }
                else if (element.getClazz().getName().endsWith("Peptide")) {
                    objectRuleResult.addAll(XLinkPeptideModificationObjectRule.checkRulesWithHashMapContent());
                }
            }
        }
        
        this.addMessages(objectRuleResult, this.msgLevel);
        
        return this.progress;
    }

    /**
     * Validates an object.
     * @param objectToCheck the onject to check
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> validate(Object objectToCheck) throws ValidatorException {
        Collection<ValidatorMessage> messages = new ArrayList<>();
        boolean bSomeObjRuleCanCheck = false;
        
        for (ObjectRule rule : this.getObjectRules()) {
            if (rule.canCheck(objectToCheck)) {
                bSomeObjRuleCanCheck = true;
                
                @SuppressWarnings("unchecked")
                final Collection<ValidatorMessage> resultCheck = (Collection<ValidatorMessage>) rule.check(objectToCheck);
                this.extendedReport.objectRuleExecuted(rule, resultCheck);

                if (this.ruleFilterManager != null) {
                    boolean valid = true;
                    if (resultCheck != null && !resultCheck.isEmpty()) {
                        valid = false;
                    }
                    this.ruleFilterManager.updateRulesToSkipByARuleResult(rule, valid);
                }
                if (resultCheck != null) {
                    messages.addAll(resultCheck);
                }
            }
        }
        if (!bSomeObjRuleCanCheck) {
            throw new IllegalArgumentException("There are no object rules to check the object: " + objectToCheck + " at the severity level: " + this.getMessageReportLevel());
        }
        
        this.LOGGER.debug("Total number of validation messages: " + messages.size());
        return messages;
    }

    /**
     * Checks the CV mappings.
     * @param collection the collection
     * @param xPath the XPATH
     * @throws ValidatorException validator exception
     * @return collection of messages
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<ValidatorMessage> checkCvMapping(Collection<?> collection, String xPath) throws ValidatorException {
        Collection messages = new ArrayList();

        if (this.getCvRuleManager() != null) {
            boolean someMappingRuleCanCheck = false;
            for (CvRule rule : this.getCvRuleManager().getCvRules()) {
                if (rule.canCheck(xPath)) {
                    this.LOGGER.info(rule.getId() + " can check " + xPath);
                    someMappingRuleCanCheck = true;
                    for (Object obj : collection) {
                        final Collection<ValidatorMessage> resultCheck = rule.check(obj, xPath);
                        if (this.ruleFilterManager != null) {
                            boolean valid = true;
                            if (resultCheck != null && !resultCheck.isEmpty()) {
                                valid = false;
                            }
                            this.ruleFilterManager.updateRulesToSkipByARuleResult(rule, valid);
                        }
                        messages.addAll(resultCheck);
                    }
                }
            }
            if (!someMappingRuleCanCheck) {
                throw new IllegalArgumentException("There is no cvMapping rules to check the object with XPath: " + xPath + " at severity level: " + this.getMessageReportLevel());
            }
        }
        else {
            this.LOGGER.error("The CvRuleManager has not been set up yet.");
        }

        return messages;
    }

    /**
     * Adds a message
     * @param sync_msgs
     * @param aLevel 
     */
    private void addSyncMessages(Map<String, List<ValidatorMessage>> sync_msgs, MessageLevel aLevel) {
        sync_msgs.keySet().stream().forEach((key) -> {
            final List<ValidatorMessage> list = sync_msgs.get(key);
            
            list.stream().filter((aNewMessage) -> (aNewMessage.getLevel().isHigher(aLevel) || aNewMessage.getLevel().isSame(aLevel))).forEach((aNewMessage) -> {
                this.addValidatorMessage(key, aNewMessage, this.msgLevel);
            });
        });
    }

    /**
     * Gets the collection of messages.
     * @return Collection<>
     */
    private Collection<ValidatorMessage> getMessageCollection() {
        Collection<ValidatorMessage> ret = new HashSet<>();
        
        this.msgs.keySet().stream().map((key) -> this.msgs.get(key)).filter((list) -> (list != null)).forEach((list) -> {
            ret.addAll(list);
        });

        return ret;
    }

    /**
     * Adds a collection of messages.
     * @param aNewMessages  the collection of messages
     * @param aLevel        error level of the messages
     */
    private void addMessages(Collection<ValidatorMessage> aNewMessages, MessageLevel aLevel) {
        aNewMessages.stream().filter((aNewMessage) -> (aNewMessage.getLevel().isHigher(aLevel) || aNewMessage.getLevel().isSame(aLevel))).forEach((ValidatorMessage aNewMessage) -> {
            if (aNewMessage.getRule() != null) {
                MzIdentMLValidator.this.addValidatorMessage(aNewMessage.getRule().getId(), aNewMessage, MzIdentMLValidator.this.msgLevel);
            } else {
                MzIdentMLValidator.this.addValidatorMessage("unknown", aNewMessage, MzIdentMLValidator.this.msgLevel);
            }
        });
    }

    /**
     * Checks the validity of the mzIdentML file.
     * @param xmlFile
     * @param schemaUri
     * @return true if the XML file is valid
     * @throws SAXException 
     */
    private boolean isValidmzIdentMLXml(File xmlFile, URI schemaUri) throws SAXException {
        MzIdentMLSchemaValidator mzIdentMLValidator = new MzIdentMLSchemaValidator();

        try {
            mzIdentMLValidator.setSchema(schemaUri);
            MzIdentMLValidationErrorHandler errorHandler = mzIdentMLValidator.validateReader(new FileReader(xmlFile));
            
            if (errorHandler != null && errorHandler.noErrors()) {
                return true;
            }
            else {
                if (errorHandler != null) {
                    errorHandler.getErrorsAsValidatorMessages().stream().forEach((validatorMessage) -> {
                        String ruleId = this.gui.STR_EMPTY;
                        Rule rule = validatorMessage.getRule();
                        if (rule != null) {
                            ruleId = rule.getId();
                        }
                        this.addValidatorMessage(ruleId, validatorMessage, this.msgLevel);
                    });
                }

                // if we are here is because the file is not valid for any schema
                return false;
            }
        }
        catch (FileNotFoundException e) {
            this.LOGGER.fatal("FATAL: Could not find the MzIdentML instance file while trying to "
                + "validate it! Its existence should have been checked before!", e);
        }
        catch (MalformedURLException e) {
            this.LOGGER.fatal("FATAL: The mzIdentML schema URI (" + schemaUri + ") is not well formed!");
            // should not happen! since the PrideValidator should check
            // first if the schema URI is valid before trying to validate with it!
            e.printStackTrace(System.err);
        }
        
        System.exit(MzIdentMLValidatorGUI.EXIT_FAILURE);
        return false;
    }

    /**
     * Add a validator message to the report.
     * @param ruleId
     * @param validatorMessage
     * @param msgLevel 
     */
    private void addValidatorMessage(String ruleId, ValidatorMessage validatorMessage, MessageLevel msgLevel) {

        if (validatorMessage.getLevel().isHigher(msgLevel) || validatorMessage.getLevel().isSame(msgLevel)) {
            if (this.msgs.containsKey(ruleId)) {
                this.msgs.get(ruleId).add(validatorMessage);
            }
            else {
                List<ValidatorMessage> list = new ArrayList<>();
                list.add(validatorMessage);
                this.msgs.put(ruleId, list);
            }
            this.extendedReport.setObjectRuleAsInvalid(ruleId);
        }
        else {
            this.extendedReport.setObjectRuleAsSkipped(ruleId);
        }
    }

    /**
     * Checks the CV mapping.
     * @param element
     * @throws ValidatorException 
     */
    private void checkElementCvMapping(MzIdentMLElement element) throws ValidatorException {
        this.updateProgress("Validating " + element.getXpath() + this. STR_ELLIPSIS);
        Iterator<MzIdentMLObject> mzIdMLIter;
        try {
            mzIdMLIter = this.unmarshaller.unmarshalCollectionFromXpath(element);
            Collection<MzIdentMLObject> toValidate = new ArrayList<>();

            // before to iterate over all elements, check if the first element can be validated by some rule.
            // If an exception is thrown it is because it cannot be validated. So, return.
            if (!mzIdMLIter.hasNext()) {
                this.LOGGER.debug(element.getXpath() + " is not found. It can be because it is not indexed or just because is not found in the file" + this. STR_ELLIPSIS);
            }

            if (mzIdMLIter.hasNext()) {
                final MzIdentMLObject next = mzIdMLIter.next();
                toValidate.add(next);
                try {
                    final Collection<ValidatorMessage> cvMappingResult = this.checkCvMapping(toValidate, element.getXpath());
                    if (cvMappingResult.size() > 0) {
                        //if (!element.getTagName().equals("ProteinDetectionList")) { // ONLY A WORKAROUND
                            this.addMessages(cvMappingResult, this.msgLevel);
                        //}                        
                    }
                }
                catch (IllegalArgumentException e) {
                    this.LOGGER.debug(e.getMessage());
                    return;
                }
            }
            
            while (mzIdMLIter.hasNext()) {
                final MzIdentMLObject next = mzIdMLIter.next();
                toValidate.add(next);
            }
            try {
                final Collection<ValidatorMessage> cvMappingResult = this.checkCvMapping(toValidate, element.getXpath());
                this.addMessages(cvMappingResult, this.msgLevel);
            }
            catch (IllegalArgumentException e) {
                this.LOGGER.info(e.getMessage());
            }
        }
        catch (NullPointerException e) {
            // this is because the element has no XPath, and has to be validated in another way
            this.checkCvMapping(this.unmarshaller.unmarshal(element), null);
        }
        catch (NumberFormatException | IllegalStateException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Initializes the progress bar with start, end and current values.
     */
    private void initGuiProgress() {
        if (this.gui != null) {
            this.progress = 0;
            
            if (this.gui.isMIAPEValidationSelected()) {
                progressSteps += 3;
            }
            
            this.gui.initProgress(this.progress, progressSteps, this.progress);
        }
    }

    /**
     * Updates the progress bar.
     * @param message 
     */
    private void updateProgress(String message) {
        if (this.gui != null) {
            this.gui.setProgress(++this.progress, message);
        }
        else {
            System.out.println("----- PROGRESS: " + message);
        }
        
        this.LOGGER.info("PROGRESS: " + this.progress + "-> " + message);
    }

    /**
     * Resets all counters to zero.
     */
    private void resetCounters() {
        this.progress = 0;
        
        // resets the counters to zero
        this.cntMultipleClearedMessages = 0;
        this.cntXMLSchemaValidatingMessages = 0;
        this.cntUnanticipatedCVTerms = 0;
        this.cntXLInteractionScoringMessages = 0;
        this.gui.cntDoubledUnanticipatedCVTermMessages = 0;
        this.gui.cntFlawErrors = 0;
        this.gui.bHasXLErrors = false;
    }
    
    /**
     * Method to reset all the fields in this validator.
     * 
     * @param cvMappingRuleFile
     *            if null, a new cvMappingRuleFile will be loaded accordingly
     *            with mzIdVersion and GUI selection
     * @param objectRuleFile
     *            if null, a new objectRuleFile will be loaded accordingly with
     *            mzIdVersion and GUI selection
     * @throws CvRuleReaderException CvRule reader exception
     * @throws ValidatorException validator exception
     * @throws IOException IO exception
     */
    protected void reset(InputStream cvMappingRuleFile, InputStream objectRuleFile) throws CvRuleReaderException, ValidatorException, IOException {
        this.resetCountersAndGUI();
        this.resetCounters();

        // reset the collection of ValidatorMessages
        if (this.msgs != null) {
            this.msgs.clear();
        }

        // reset the message reporting level to the default
        this.msgLevel = MessageLevel.DEBUG;
        // reset the unmarshaller
        this.unmarshaller = null;
        // reset the progress counter
        this.progress = 0;

        // restart the rules to skip
        if (this.ruleFilterManager != null) {
            this.ruleFilterManager.restartRulesToSkip();
        }

        // delete all objectRules
        this.getObjectRules().clear();

        // delete all cvMappingRules
        final CvRuleManager cvRuleManager = this.getCvRuleManager();
        if (cvRuleManager != null) {
            cvRuleManager.getCvRules().clear();
        }

        // set the new cvMapping rules
        if (cvMappingRuleFile == null) {
            cvMappingRuleFile = this.gui.getRuleFileInputStream(MzIdentMLValidator.currentFileVersion, this.gui.STR_MAPPING);
        }
        this.setCvMappingRules(cvMappingRuleFile);
        cvMappingRuleFile.close();

        // set the new object rules
        if (objectRuleFile == null) {
            objectRuleFile = this.gui.getRuleFileInputStream(MzIdentMLValidator.currentFileVersion, this.gui.STR_OBJECT);
        }
        this.setObjectRules(objectRuleFile);
        objectRuleFile.close();
    }

    /**
     * Adds a conditional table row to the StringBuilder, i.e. add only, if count > 0.
     * 
     * @param sb
     * @param text
     * @param count 
     * @param color
     */
    private void addConditionalTableRow(StringBuilder sb, String text, int count, String color) {
        if (count > 0) {
            this.addPossiblyColouredRow(sb, text, count, color);
        }
    }
    
    /**
     * Adds a table row to the StringBuilder.
     * 
     * @param sb
     * @param text
     * @param count 
     */
    private void addTableRow(StringBuilder sb, String text, int count) {
        sb.append("<tr align='left'><td>").append(text).append("</td><td>").append(count).append("</td></tr>");
    }
    
    /**
     * Adds an empty table row to the StringBuilder.
     * 
     * @param sb 
     */
    private void addEmptyRow(StringBuilder sb) {
        sb.append("<tr align='left'><td></td></tr>");
    }

    /**
     * Adds a possibly red (if the count is > 0) table row to the StringBuilder.
     * 
     * @param sb
     * @param text
     * @param count 
     * @param color
     */
    private void addPossiblyColouredRow(StringBuilder sb, String text, int count, String color) {
        sb.append("<tr align='left'><td>");
        if (count > 0) {
            sb.append("<font color='").append(color).append("'>");
        }
        sb.append(text);
        if (count > 0) {
            sb.append("</font>");
        }
        sb.append("</td><td>");
        if (count > 0) {
            sb.append("<font color='").append(color).append("'>");
        }
        sb.append(count);
        if (count > 0) {
            sb.append("</font>");
        }
        sb.append("</td></tr>");
    }
    
    /**
     * Gets the HTML report.
     * @param messageNumber the message number
     * @return the HTML report as String
     */
    public String getHtmlStatisticsReport(int messageNumber) {
        if (this.extendedReport == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder("<html><body><table cellpadding='5'>");

        this.addConditionalTableRow(sb, "Invalid XML schema validation:", this.extendedReport.getInvalidSchemaValidation().size(), this.gui.COLOR_RED);
        this.addEmptyRow(sb);
        
        // CV Mapping rules
        this.addTableRow(sb, "CvMappingRule total count:", this.extendedReport.getTotalCvRules());
        this.addTableRow(sb, "CvMappingRules not run:", this.extendedReport.getNonCheckedCvRules().size());
        String cvMappingRuleColor = this.gui.getInvalidCvMappingColor();
        int noOfInvalidCvRules = this.extendedReport.getInvalidCvRules().size() - this.gui.cntFlawErrors;
        if (cvMappingRuleColor.equals(this.gui.COLOR_RED)) {
            this.addPossiblyColouredRow(sb, "Invalid CvMappingRules:", noOfInvalidCvRules, cvMappingRuleColor);
        }
        else {
            this.addPossiblyColouredRow(sb, "Not matching CvMappingRules:", noOfInvalidCvRules, cvMappingRuleColor);
        }
        this.addTableRow(sb, "CvMappingRules run & valid:", this.extendedReport.getValidCvRules().size());
        this.addEmptyRow(sb);
        
        // Object rules
        this.addTableRow(sb, "ObjectRules total count:", this.extendedReport.getTotalObjectRules());
        this.addTableRow(sb, "ObjectRules not run:", this.extendedReport.getObjectRulesNotChecked().size());
        String objRuleColor = this.gui.getInvalidObjectRuleColor();
        int noOfInvalidObjectRules = this.extendedReport.getObjectRulesInvalid().size();
        if (objRuleColor.equals(this.gui.COLOR_RED)) {
            this.addPossiblyColouredRow(sb, "ObjectRules run & invalid:", noOfInvalidObjectRules, objRuleColor);
        }
        else {
            this.addPossiblyColouredRow(sb, "ObjectRules run & not matching:", noOfInvalidObjectRules, objRuleColor);
        }
        this.addTableRow(sb, "ObjectRules run & valid:", this.extendedReport.getObjectRulesValid().size());
        this.addEmptyRow(sb);

        // Total number of rules
        if (this.gui.jCheckBoxShowUnanticipatedCVTerms.isSelected()) {
            this.addPossiblyColouredRow(sb, "Unanticipated CV terms:", this.cntUnanticipatedCVTerms, this.gui.getUnanticipatedCVColor(this.cntUnanticipatedCVTerms));
        }
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            this.addPossiblyColouredRow(sb, "XL interaction scoring messages:", this.cntXLInteractionScoringMessages, this.gui.getXLInteractionScoreColor(this.cntXLInteractionScoringMessages));
        }

        messageNumber = this.getTotalNumberOfInvalidRules(noOfInvalidCvRules, noOfInvalidObjectRules);
        this.addPossiblyColouredRow(sb, this.STR_NOT_MATCHING_MSGS_RECV, messageNumber, this.gui.getInvalidMsgColor());
        
        this.addConditionalTableRow(sb, "Messages not reported since they occur more than " + this.gui.jSpinner.getValue() + " times: ", this.cntMultipleClearedMessages + this.gui.cntDoubledUnanticipatedCVTermMessages, this.gui.COLOR_BLACK);

        sb.append("</table></body></html>");

        return sb.toString();
    }

    /**
     * Gets the statistics report
     * @param messageNumber the message number
     * @return the statistics report as String
     */
    public String getStatisticsReport(int messageNumber) {
        this.extendedReport = this.getExtendedReport();
        if (this.extendedReport == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        // CV Rules
        sb.append("Invalid XML schema validation: ").append(this.extendedReport.getInvalidSchemaValidation().size()).append(NEW_LINE);
        sb.append(NEW_LINE);
        
        sb.append("CvMappingRule total count: ").append(this.extendedReport.getTotalCvRules()).append(NEW_LINE);
        sb.append("CvMappingRules not run: ").append(this.extendedReport.getNonCheckedCvRules().size()).append(NEW_LINE);
        String cvMappingRuleColor = this.gui.getInvalidCvMappingColor();
        int noOfInvalidCvRules = this.extendedReport.getInvalidCvRules().size() - this.gui.cntFlawErrors;
        if (cvMappingRuleColor.equals(this.gui.COLOR_RED)) {
            sb.append("CvMappingRules run & invalid: ").append(noOfInvalidCvRules).append(NEW_LINE);
        }
        else {
            sb.append("CvMappingRules run & not matching: ").append(noOfInvalidCvRules).append(NEW_LINE);
        }
        sb.append("CvMappingRules invalid XPath: ").append(this.extendedReport.getCvRulesInvalidXpath().size()).append(NEW_LINE);
        sb.append("CvMappingRules run & valid: ").append(this.extendedReport.getValidCvRules().size()).append(NEW_LINE);
        sb.append(NEW_LINE);

        // Object rules
        sb.append("ObjectRules total count: ").append(this.extendedReport.getTotalObjectRules()).append(NEW_LINE);
        sb.append("ObjectRules not run: ").append(this.extendedReport.getObjectRulesNotChecked().size()).append(NEW_LINE);
        String objRuleColor = this.gui.getInvalidObjectRuleColor();
        int noOfInvalidObjectRules = this.extendedReport.getObjectRulesInvalid().size();
        if (objRuleColor.equals(this.gui.COLOR_RED)) {
            sb.append("ObjectRules run & invalid: ").append(noOfInvalidObjectRules).append(NEW_LINE);
        }
        else {
            sb.append("ObjectRules run & not matching: ").append(noOfInvalidObjectRules).append(NEW_LINE);
        }
        sb.append("ObjectRules run & valid: ").append(this.extendedReport.getObjectRulesValid().size()).append(NEW_LINE);
        sb.append(NEW_LINE);
        
        if (this.gui.jCheckBoxShowUnanticipatedCVTerms.isSelected()) {
            sb.append("Unanticipated CV terms: ").append(this.cntUnanticipatedCVTerms).append(NEW_LINE);
        }
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            sb.append("XL interaction scoring messages: ").append(this.cntXLInteractionScoringMessages).append(NEW_LINE);
        }
        
        // Total number of rules
        messageNumber = this.getTotalNumberOfInvalidRules(noOfInvalidCvRules, noOfInvalidObjectRules);
        sb.append(this.STR_NOT_MATCHING_MSGS_RECV).append(messageNumber);
        
        if (this.cntMultipleClearedMessages > 0) {
            sb.append(NEW_LINE);
            sb.append("Messages not reported since they occur more than ").append(this.gui.jSpinner.getValue()).append(" times: ").append(this.cntMultipleClearedMessages + this.gui.cntDoubledUnanticipatedCVTermMessages).append(NEW_LINE);
        }
    
        return sb.toString();
    }

    /**
     * Gets the total number of invalid rules.
     * @return the total number of invalid rules
     */
    private int getTotalNumberOfInvalidRules(int invalidCVRules, int invalidObjectRules) {
        return invalidCVRules + invalidObjectRules + this.cntUnanticipatedCVTerms + this.cntXLInteractionScoringMessages + this.extendedReport.getInvalidSchemaValidation().size() - this.gui.cntFlawErrors - (Integer) this.gui.jSpinner.getValue();
    }
    
    /**
     * Gets the CV context report.
     * @return the CV context report as String
     */
    public String getCvContextReport() {
        StringBuilder sb = new StringBuilder();

        if (ValidatorCvContext.getInstance() != null && !ValidatorCvContext.getInstance().getNotRecognisedXpath().isEmpty()) {
            sb.append(TRIPLE_NEW_LINE).append("---------- ---------- CvContext statistics ---------- ----------").append(DOUBLE_NEW_LINE);
            // check for terms that were not anticipated with the rules in the CV mapping file.
            ValidatorCvContext.getInstance().getNotRecognisedXpath().stream().map((xpath) -> {
                sb.append(TAB).append(xpath).append(NEW_LINE);
                return xpath;
            }).forEach((xpath) -> {
                sb.append(TAB).append("unrecognized terms:").append(NEW_LINE);
                ValidatorCvContext.getInstance().getNotRecognisedTerms(xpath).stream().forEach((term) -> {
                    sb.append(term).append("; ");
                });
            });
        }

        return sb.toString();
    }

    /**
     * Checks if a file exists and is not a folder.
     * 
     * @param fileNameArg
     * @param fileType 
     * @returns the File object
     */
    private static File checkFileExistence(String fileNameArg, String fileType) {
        File file = new File(fileNameArg);
        
        if (!file.exists()) {
            printError("The " + fileType + " file you specified '" + fileNameArg + "' does not exist!");
        }
        else if (file.isDirectory()) {
            printError("The " + fileType + " file you specified '" + fileNameArg + "' is a folder, not a file!");
        }
        
        return file;
    }
    
    /**
     * Main method for command line execution.
     * @param args the command line arguments
     * @throws ValidatorException validator exception
     * @throws OntologyLoaderException ontology loader exception
     * @throws URISyntaxException URI syntax exception
     */
    public static void main(String[] args) throws ValidatorException, OntologyLoaderException, URISyntaxException {
        if (args == null || args.length != 6) {
            printUsage();
        }
        
        // Validate existence of input files.
        File ontology = checkFileExistence(args[0], "ontology config");
        File cvMapping = checkFileExistence(args[1], "CV mapping config");
        File objectRules = checkFileExistence(args[2], "object rules config");
        File ruleFilterXMLFile = checkFileExistence(args[3], "rule filter");
        File mzIdML = checkFileExistence(args[4], "mzIdentML");

        // Validate messagelevel.
        MessageLevel msgLevel = getMessageLevel(args[5]);
        if (msgLevel == null) {
            System.err.println(DOUBLE_NEW_LINE + " *** Unknown message level '" + args[5] + "' ***" + NEW_LINE);
            System.err.println(TAB + "Try one of the following:");
            System.err.println(DOUBLE_TAB +" - DEBUG");
            System.err.println(DOUBLE_TAB +" - INFO");
            System.err.println(DOUBLE_TAB +" - WARN");
            System.err.println(DOUBLE_TAB +" - ERROR");
            System.err.println(DOUBLE_TAB +" - FATAL");
            System.err.println(" !!! Defaulting to 'INFO' !!!" + DOUBLE_NEW_LINE);
            msgLevel = MessageLevel.INFO;
        }

        // OK, all validated. Let's get going!
        Collection<ValidatorMessage> messages = new ArrayList<>();
        MzIdentMLValidator validator;

        try {
            InputStream ontInput = new FileInputStream(ontology);
            
            RuleFilterManager ruleFilterManager = new RuleFilterManager(new FileInputStream(ruleFilterXMLFile));

            validator = new MzIdentMLValidator(ontInput, new FileInputStream(cvMapping), new FileInputStream(objectRules), null);
            validator.setMessageReportLevel(msgLevel);
            validator.setRuleFilterManager(ruleFilterManager);

            Collection<ValidatorMessage> msgs = validator.startValidation(mzIdML);
            if (msgs != null) {
                messages.addAll(msgs);

                System.out.println(validator.getValidatorMessages(messages));
                System.out.println(NEW_LINE);
                System.out.println(validator.getStatisticsReport(messages.size()));
                System.out.println(NEW_LINE);
                System.out.println(validator.getCvContextReport());
                System.out.println(DOUBLE_NEW_LINE + "All done. Goodbye.");
            }
        }
        catch (FileNotFoundException | JAXBException | OntologyLoaderException | ValidatorException | CvRuleReaderException e) {
            System.err.println(DOUBLE_NEW_LINE + "Exception occurred: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Sets the rule filter manager
     * @param ruleFilterManager the rule filter manager
     */
    public void setRuleFilterManager(RuleFilterManager ruleFilterManager) {
        this.ruleFilterManager = ruleFilterManager;
    }

    /**
     * Clusters the ValidatorMessages
     * 
     * @param messages collection of messages
     * @return a collection of validator messages
     */
    public Collection<ValidatorMessage> clusterByMessagesAndRules(Collection<ValidatorMessage> messages) {
        Collection<ValidatorMessage> clusteredMessages = new ArrayList<>(messages.size());

        // build a first clustering by message and rule
        Map<String, Map<Rule, Set<ValidatorMessage>>> clustering = new HashMap<>();
        messages.stream().forEach((message) -> {
            // if the message doesn't have an associated rule, store it directly (comes from schema validation)
            if (message.getRule() == null) {
                clusteredMessages.add(message);
            }
            else {
                // if contains the same message, from the same rule, cluster it
                if (clustering.containsKey(message.getMessage())) {
                    Map<Rule, Set<ValidatorMessage>> messagesCluster = clustering.get(message.getMessage());

                    if (messagesCluster.containsKey(message.getRule())) {
                        messagesCluster.get(message.getRule()).add(message);
                    }
                    else {
                        Set<ValidatorMessage> validatorContexts = new HashSet<>();
                        validatorContexts.add(message);
                        messagesCluster.put(message.getRule(),validatorContexts);
                    }
                }
                else {
                    Map<Rule, Set<ValidatorMessage>> messagesCluster = new HashMap<>();

                    Set<ValidatorMessage> validatorContexts = new HashSet<>();
                    validatorContexts.add(message);
                    messagesCluster.put(message.getRule(), validatorContexts);

                    clustering.put(message.getMessage(), messagesCluster);
                }
            }
        });

        // build a second cluster by message level
        Map<MessageLevel, ClusteredContext> clusteringByMessageLevel = new EnumMap<>(MessageLevel.class);

        clustering.entrySet().stream().forEach((entry) -> {
            String message = entry.getKey();
            Map<Rule, Set<ValidatorMessage>> ruleCluster = entry.getValue();

            // cluster by message level and create proper validatorMessage
            ruleCluster.entrySet().stream().forEach((ruleEntry) -> {
                clusteringByMessageLevel.clear();

                Rule rule = ruleEntry.getKey();
                Set<ValidatorMessage> validatorMessages = ruleEntry.getValue();

                validatorMessages.stream().forEach((validatorMessage) -> {
                    if (clusteringByMessageLevel.containsKey(validatorMessage.getLevel())) {
                        ClusteredContext clusteredContext = clusteringByMessageLevel.get(validatorMessage.getLevel());

                        clusteredContext.getContexts().add(validatorMessage.getContext());
                    }
                    else {
                        ClusteredContext clusteredContext = new ClusteredContext();

                        clusteredContext.getContexts().add(validatorMessage.getContext());

                        clusteringByMessageLevel.put(validatorMessage.getLevel(), clusteredContext);
                    }
                });

                clusteringByMessageLevel.entrySet().stream().forEach((levelEntry) -> {
                    clusteredMessages.add(new ValidatorMessage(message, levelEntry.getKey(), levelEntry.getValue(), rule));
                });
            });
        });

        return clusteredMessages;
    }

    /**
     * Prints out an error message.
     * @param aMessage 
     */
    private static void printError(String aMessage) {
        System.err.println(DOUBLE_NEW_LINE + aMessage + DOUBLE_NEW_LINE);
        System.exit(MzIdentMLValidatorGUI.EXIT_FAILURE);
    }

    /**
     * Prints out usage hints.
     */
    private static void printUsage() {
        printError("Usage:" + DOUBLE_NEW_LINE + TAB  + MzIdentMLValidator.class.getName()
            + " <ontology_config_file> <cv_mapping_config_file> <coded_rules_config_file> <xml_file_filter_file> <mzml_file_to_validate> <message_level>" + NEW_LINE + NEW_LINE_DOUBLE_TAB + "Where message level can be:" + NEW_LINE_DOUBLE_TAB + " - DEBUG" + NEW_LINE_DOUBLE_TAB + " - INFO" + NEW_LINE_DOUBLE_TAB + " - WARN" + NEW_LINE_DOUBLE_TAB +" - ERROR" + NEW_LINE_DOUBLE_TAB +" - FATAL");
    }

    /**
     * Gets the validator messages.
     * @param aMessages collection of messages
     * @return the validator messages
     */
    public String getValidatorMessages(Collection<ValidatorMessage> aMessages) {
        StringBuilder sb = new StringBuilder();
        
        Collection<ValidatorMessage> clearedMsgs = this.clearMultipleMessages(aMessages);
        
        if (!clearedMsgs.isEmpty()) {
            sb.append(DOUBLE_NEW_LINE).append("The following ").append(clearedMsgs.size()).append(" messages were obtained during the validation of your XML file:").append(NEW_LINE);
            clearedMsgs.stream().forEach((valMsg) -> {
                sb.append(" * ").append(valMsg).append(NEW_LINE);
            });
        }
        else {
            sb.append(DOUBLE_NEW_LINE).append("Congratulations! Your XML file passed the semantic validation!").append(DOUBLE_NEW_LINE);
        }
        
        return sb.toString();
    }

    /**
     * Removes messages which occur more than a given number of times.
     * @param aMessages collection of messages
     * @return collection of cleared messages
     */
    public Collection<ValidatorMessage> clearMultipleMessages(Collection<ValidatorMessage> aMessages) {
        this.updateProgress("Clear multiple messages" + this. STR_ELLIPSIS);
        
        HashMap<ImmutablePair, Integer> msgID_msgLevelMap = new HashMap<>();
        
        MessageLevel locMsgLevel;
        final Collection<ValidatorMessage> clearedMultipleMessages = new ArrayList<>();
        ImmutablePair<String, MessageLevel> idLevelPair;
        int cnt = 1;
        for (ValidatorMessage msg: aMessages) {
            if (msg != null) { 
                locMsgLevel = msg.getLevel();
                
                if (msg.getRule() != null) {
                    idLevelPair = new ImmutablePair<>(msg.getRule().getId(), locMsgLevel);
                }
                else {
                    idLevelPair = new ImmutablePair<>(String.valueOf(cnt++), locMsgLevel);
                }
                
                if (msgID_msgLevelMap.containsKey(idLevelPair)) {
                    msgID_msgLevelMap.put(idLevelPair, msgID_msgLevelMap.get(idLevelPair) + 1);
                    if (msgID_msgLevelMap.get(idLevelPair) < (int) this.gui.jSpinner.getValue()) {
                        clearedMultipleMessages.add(msg);
                    }
                    else {
                        this.cntMultipleClearedMessages++;
                    }
                }
                else {
                    msgID_msgLevelMap.put(idLevelPair, 1);
                    clearedMultipleMessages.add(msg);
                }
            }
        }
        
        return clearedMultipleMessages;
    }

    /**
     * Gets the message level.
     * @param aLevel
     * @return the set message level.
     */
    private static MessageLevel getMessageLevel(String aLevel) {
        aLevel = aLevel.trim();
        MessageLevel result = null;
        switch (aLevel) {
            case "DEBUG":
                result = MessageLevel.DEBUG;
                break;
            case "INFO":
                result = MessageLevel.INFO;
                break;
            case "WARN":
                result = MessageLevel.WARN;
                break;
            case "ERROR":
                result = MessageLevel.ERROR;
                break;
            case "FATAL":
                result = MessageLevel.FATAL;
                break;
        }

        return result;
    }

/******************************************************************************/
/* INNER CLASSES to allow validation over element iterator in multiple threads*/
/******************************************************************************/
    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next() methods of the iterator.
     */
    private class InnerIteratorSync<T> {
        private Iterator<T> iter = null;

        /**
         * 
         * @param aIterator 
         */
        public InnerIteratorSync(Iterator<T> aIterator) {
            this.iter = aIterator;
        }

        /**
         * 
         * @return T
         */
        public synchronized T next() {
            T result = null;
            if (this.iter.hasNext()) {
                result = this.iter.next();
            }
            
            return result;
        }
    }

    /**
     * Simple lock class so the main thread can detect worker threads' completion.
     */
    private class InnerLock {
        private int doneCount = 0;

        /**
         * 
         */
        public synchronized void updateDoneCount() {
            this.doneCount++;
            notifyAll();
        }

        /**
         * 
         * @param totalCount
         * @return true, if the thread is done.
         */
        public synchronized boolean isDone(int totalCount) {
            while (this.doneCount < totalCount) {
                try {
                    wait();
                }
                catch (InterruptedException ie) {
                    System.err.println("I've been interrupted" + STR_ELLIPSIS);
                }
            }
            
            return true;
        }
    }

    /**
     * Runnable that requests the next spectrum from the synchronised iterator wrapper, and validates it.
     */
    private class InnerSpecValidator<T> implements Runnable {
        private InnerIteratorSync<T> iterat = null;
        private InnerLock iLock = null;
        private int counter = 0;

        /**
         * 
         * @param aIterator
         * @param aLock
         * @param aNumber 
         */
        public InnerSpecValidator(InnerIteratorSync<T> aIterator, InnerLock aLock, int aNumber) {
            this.iterat = aIterator;
            this.iLock = aLock;
        }

        /**
         * 
         */
        @Override
        public void run() {
            T element;
            while ((element = this.iterat.next()) != null) {
                try {
                    // check cvMapping rules
                    addMessages(checkCvMapping(element, MzIdentMLElement.SpectrumIdentificationItem.getXpath()), msgLevel); // hard coded
                    
                    // check object rules
                    addMessages(validate(element), msgLevel);
                }
                catch (ValidatorException ve) {
                    ve.printStackTrace(System.err);
                }
                catch (IllegalArgumentException e) {
                    LOGGER.debug(e.getMessage());
                }
                this.counter++;
            }
            this.iLock.updateDoneCount();
        }

        /**
         * Gets the counter.
         * @return the counter
         */
        public int getCount() {
            return this.counter;
        }
    }
}
