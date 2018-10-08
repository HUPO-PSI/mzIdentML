package psidev.psi.pi.validator.objectrules;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import psidev.psi.pi.validator.RESTClient;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;

/**
 * Abstract base class for all custom ObjectRules.
 * 
 * @author Gerhard Mayer
 * @param <T> type of jmzIdentML object
 */
public abstract class AObjectRule<T extends Object> extends ObjectRule<T> {

    /**
     * Constants.
     */
    protected final Logger LOGGER = LogManager.getLogger(AObjectRule.class);
    private static final String STR_ENCODING = "UTF-8";
    private final HashMap<String, String> childOf1001143 = new HashMap<String, String>() {  // PSM-level search engine specific statistic
        {
            put("MS:1001154", "SEQUEST:probability");
            put("MS:1001155", "SEQUEST:xcorr");
            put("MS:1001156", "SEQUEST:deltacn");
            put("MS:1001160", "SEQUEST:sf");
            put("MS:1001161", "SEQUEST:matched ions");
            put("MS:1001162", "SEQUEST:total ions");
            put("MS:1001171", "Mascot:score");
            put("MS:1001172", "Mascot:expectation value");
            put("MS:1001173", "Mascot:matched ions");
            put("MS:1001174", "Mascot:total ions");
            put("MS:1001215", "SEQUEST:PeptideSp");
            put("MS:1001217", "SEQUEST:PeptideRankSp");
            put("MS:1001218", "SEQUEST:PeptideNumber");
            put("MS:1001219", "SEQUEST:PeptideIdnumber");
            put("MS:1001328", "OMSSA:evalue");
            put("MS:1001329", "OMSSA:pvalue");
            put("MS:1001330", "X\\!Tandem:expect");
            put("MS:1001331", "X\\!Tandem:hyperscore");
            put("MS:1001370", "Mascot:homology threshold");
            put("MS:1001371", "Mascot:identity threshold");
            put("MS:1001393", "Phenyx:Auto");
            put("MS:1001394", "Phenyx:User");
            put("MS:1001395", "Phenyx:Pepzscore");
            put("MS:1001396", "Phenyx:PepPvalue");
            put("MS:1001397", "Phenyx:NumberOfMC");
            put("MS:1001398", "Phenyx:Modif");
            put("MS:1001417", "SpectraST:dot");
            put("MS:1001418", "SpectraST:dot_bias");
            put("MS:1001419", "SpectraST:discriminant score F");
            put("MS:1001420", "SpectraST:delta");
            put("MS:1001491", "percolator:Q value");
            put("MS:1001492", "percolator:score");
            put("MS:1001493", "percolator:PEP");
            put("MS:1001495", "ProteinScape:SearchResultId");
            put("MS:1001496", "ProteinScape:SearchEventId");
            put("MS:1001497", "ProteinScape:ProfoundProbability");
            put("MS:1001498", "Profound:z value");
            put("MS:1001499", "Profound:Cluster");
            put("MS:1001500", "Profound:ClusterRank");
            put("MS:1001501", "MSFit:Mowse score");
            put("MS:1001502", "Sonar:Score");
            put("MS:1001503", "ProteinScape:PFFSolverExp");
            put("MS:1001504", "ProteinScape:PFFSolverScore");
            put("MS:1001505", "ProteinScape:IntensityCoverage");
            put("MS:1001506", "ProteinScape:SequestMetaScore");
            put("MS:1001568", "Scaffold:Peptide Probability");
            put("MS:1001569", "IdentityE Score");
            put("MS:1001570", "ProteinLynx:Log Likelihood");
            put("MS:1001571", "ProteinLynx:Ladder Score");
            put("MS:1001572", "SpectrumMill:Score");
            put("MS:1001573", "SpectrumMill:SPI");
            put("MS:1001580", "SpectrumMill:Discriminant Score");
            put("MS:1001589", "MyriMatch:MVH");
            put("MS:1001590", "MS:1001590");
            put("MS:1001874", "FDRScore");
            put("MS:1001887", "SQID:score");
            put("MS:1001888", "SQID:deltaScore");
            put("MS:1001950", "PEAKS:peptideScore");
            put("MS:1001952", "ZCore:probScore");
            put("MS:1001974", "DeBunker:score");
            put("MS:1002044", "ProteinProspector:score");
            put("MS:1002045", "ProteinProspector:expectation value");
            put("MS:1002049", "MS-GF:RawScore");
            put("MS:1002051", "MS-GF:Energy");
            put("MS:1002052", "MS-GF:SpecEValue");
            put("MS:1002053", "MS-GF:EValue");
            put("MS:1002054", "MS-GF:QValue");
            put("MS:1002055", "MS-GF:PepQValue");
            put("MS:1002056", "MS-GF:PEP");
            put("MS:1002125", "combined FDRScore");
            put("MS:1002248", "SEQUEST:spscore");
            put("MS:1002249", "SEQUEST:sprank");
            put("MS:1002250", "SEQUEST:deltacnstar");
            put("MS:1002252", "Comet:xcorr");
            put("MS:1002253", "Comet:deltacn");
            put("MS:1002254", "Comet:deltacnstar");
            put("MS:1002255", "Comet:spscore");
            put("MS:1002256", "Comet:sprank");
            put("MS:1002258", "Comet:matched ions");
            put("MS:1002259", "Comet:total ions");
            put("MS:1002262", "Byonic:Score");
            put("MS:1002263", "Byonic:Delta Score");
            put("MS:1002264", "Byonic:DeltaMod Score");
            put("MS:1002265", "Byonic:PEP");
            put("MS:1002266", "Byonic:Peptide LogProb");
            put("MS:1002309", "Byonic: Peptide AbsLogProb");
            put("MS:1002311", "Byonic: Peptide AbsLogProb2D");
            put("MS:1002319", "Amanda:AmandaScore");
            put("MS:1002338", "Andromeda:score");
            put("MS:1002355", "PSM-level FDRScore");
            put("MS:1002356", "PSM-level combined FDRScore");
            put("MS:1002448", "PEAKS:inChorusPeptideScore");
            put("MS:1002466", "PeptideShaker PSM score");
            put("MS:1002467", "PeptideShaker PSM confidence");
            put("MS:1002534", "ProLuCID:xcorr");
            put("MS:1002535", "ProLuCID:deltacn");
            put("MS:1002545", "xi:score");
            put("MS:1002662", "Morpheus:Morpheus score");            
            put("MS:1002681", "OpenXQuest:combined score");
            put("MS:1002682", "OpenXQuest:xcorr xlink");
            put("MS:1002683", "OpenXQuest:xcorr common");
            put("MS:1002684", "OpenXQuest:match-odds");
            put("MS:1002685", "OpenXQuest:intsum");
            put("MS:1002686", "OpenXQuest:wTIC");
        }
    };
    
    private final HashMap<String, String> childOf1002358 = new HashMap<String, String>() {  // search engine specific peptide sequence-level identification statistic
        {
            put("MS:1001396", "Phenyx:PepPvalue");
            put("MS:1002360", "distinct peptide-level FDRScore");
            put("MS:1002361", "distinct peptide-level combined FDRScore");
            put("MS:1002368", "PeptideShaker peptide score");
            put("MS:1002499", "peptide level score");
            put("MS:1002541", "PeptideShaker peptide confidence type");
        }
    };
    
    private final HashMap<String, String> childOf1002664= new HashMap<String, String>() {   // interaction score derived from cross-linking
        {
            put("MS:1002676", "protein-pair-level global FDR");
            put("MS:1002677", "residue-pair-level global FDR");
        }
    };
    
    /**
     * Members.
     */
    protected RESTClient restClient = new RESTClient();
    
    /**
     * Constructor.
     */
    public AObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public AObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Gets the ID.
     * @return the Id (name) for this object.
     */
    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Calls OLS webserver and gets child terms for a termID.
     * @param termID    the ID of the CV term
     * @param ontology  the ontology
     * @return map with the child terms.
D     */
    protected HashMap<String, String> getTermChildren(String termID, String ontology) {
        HashMap<String, String> retMap = new HashMap<>();
        
        /*
        try {
        Query olsQuery = new QueryServiceLocator().getOntologyQuery();
        @SuppressWarnings("unchecked")
        HashMap<String, String> terms = olsQuery.getTermChildren(termID, ontology, -1, null);
        if (terms != null){
        retMap.putAll(terms);
        }
        }
        catch (ServiceException | RemoteException ex) {
        ex.printStackTrace(System.err);
        }
         */
        // Quick and dirty hardcoded hack - replace later by OLS REST API calls
        switch (termID) {
            case "MS:1001143":
                return this.childOf1001143;
            case "MS:1002358":
                return this.childOf1002358;
            case "MS:1002664":
                return this.childOf1002664;
            default:
                break;
        }
     
        // TODO: Use the new OLS REST API
        /*
        String olsAPIStr = AObjectRule.urlEncode(termID);
        String response = this.restClient.callGET(olsAPIStr);
        // TODO: parse the response and fill the map with the child terms from the response
        */
        
        return retMap;
    }
    
    /**
     * Encodes an URL for use as IRI in a OLS getTermChildrenRequest.
     * Remark: api/ontologies/{ontology name}/terms/{term IRI}/children
     * @param termID the ID of the term from the psi-ms.obo
     * @return the encoded URL
     * @see "http://www.ebi.ac.uk/ols/roadmap.html"
     * @see "http://www.ebi.ac.uk/ols/docs/api"
     */
    public static String urlEncode(String termID) {
        String urlToEncode = "http://purl.obolibrary.org/obo/";
        StringBuilder strB = new StringBuilder("http://www.ebi.ac.uk/ols/ontologies/ms/terms/");
        
        try {
            // double encode the URL
            strB.append(URLEncoder.encode(URLEncoder.encode(urlToEncode, AObjectRule.STR_ENCODING), AObjectRule.STR_ENCODING));
            strB.append(termID);
            strB.append("/children");
        }
        catch (UnsupportedEncodingException exc) {
            exc.printStackTrace(System.err);
        }
        
        return strB.toString();
    }
}
