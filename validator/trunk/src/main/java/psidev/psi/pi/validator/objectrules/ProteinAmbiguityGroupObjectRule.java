package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinAmbiguityGroup;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis;

/**
 * This rule evaluates:<br>
 * Only one PDH in a PAG can be labeled as "group representative"
 * (MS:1002403), and if found, the PDH must be labeled as a "leading protein"
 * (MS:1002401), otherwise, a level-ERROR message will be reported.
 * 
 * If all the PDHs labeled as "leading protein" in a PAG do not contain
 * "group representative", a level-INFO message will be reported.
 * 
 * If there is not any PDH in a PAG with "group representative" term, report a
 * level-INFO message.
 * 
 * In case of a cross-linking file it is checked, that there are cross-linking
 * interaction scores sharing the same ID and score within different
 * ProteinAmbiguityGroup (PAG) elements.
 * 
 * @author Salva, Gerhard
 */
public class ProteinAmbiguityGroupObjectRule extends AObjectRule<ProteinAmbiguityGroup> {

    /**
     * Constants.
     */
    private static final Context PAG_CONTEXT = new Context(MzIdentMLElement.ProteinAmbiguityGroup.getXpath());
    private static final String GROUP_REPRESENTATIVE    = "MS:1002403"; // group representative
    private static final String LEADING_PROTEIN         = "MS:1002401"; // leading protein
    private static final String XL_INTERACTION_SCORE    = "MS:1002664"; // protein interaction score derived from cross-linking
    private static final String XL_INTERACTION_SCORE_PPL= "MS:1002676"; // protein-pair-level global FDR
    private static final String XL_INTERACTION_SCORE_RPL= "MS:1002677"; // residue-pair-level global FDR
    private final String STR_REGEXP_XL_INTERACTION_SCORE= "(\\d+[.][a|b]:(\\d+|null):\\d+[.]\\d+([Ee][+-][0-9]+)*:(true|false]\\{1\\}))";

    private final String STR_COLON = ":";
    private final String STR_POINT = ".";

    /**
     * Members.
     */
    private boolean groupRepresentativeError    = false;
    private boolean leadingProteinError         = false;
    private boolean xlInteractionScoreRegExError= false;
    public static final HashMap<ImmutablePair<String, String>, HashMap<String, String>> XL_ID_SCORE_PAIR_TO_PAGID2PDHID = new HashMap<>();
    private HashMap<String, String> map_1002664 = null; // maps accession to term name
    private final int NOT_FOUND = -1;
    private static final HashMap<String, Character> INTERACT_ID_TO_SUFFIX_MAP = new HashMap<>(); // for detecting loop-links (maps XL interaction ID to 'a' or 'b'

    /**
     * Constructor.
     */
    public ProteinAmbiguityGroupObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ProteinAmbiguityGroupObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a ProteinAmbiguityGroup.
     * 
     * @param obj   the object to check
     * @return true, if obj is a ProteinAmbiguityGroup
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof ProteinAmbiguityGroup);
    }

    /**
     * Checks a ProteinAmbiguityGroup element.
     * @param pag the ProteinAmbiguityGroup element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(ProteinAmbiguityGroup pag) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        List<ProteinDetectionHypothesis> pdhs = pag.getProteinDetectionHypothesis();
        if (pdhs != null) {
            boolean anyGroupRepresentativeInPAGFound = false;
            boolean anyLeadingProteinInPAGFound = false;
            this.leadingProteinError = true;

            // until finding a PDH labeled as leading protein and group representative
            for (ProteinDetectionHypothesis pdh : pdhs) {
                boolean bLeadingPDH = false;
                boolean bGroupRepresentative = false;
                // to evaluate that ONLY one PDH in a PAG can be labeled as "group representative" (MS:1002403)
                // and if all the PDHs labeled as "leading protein" in a PAG are not also labeled do
                // not contain "group representative", a level-INFO message will be reported.
                List<CvParam> cvParams = pdh.getCvParam();
                if (cvParams != null) {
                    OUTER:
                    for (CvParam cvParam : cvParams) {
                        switch (cvParam.getAccession()) {
                            case ProteinAmbiguityGroupObjectRule.LEADING_PROTEIN:
                                bLeadingPDH = true;
                                break;
                            case ProteinAmbiguityGroupObjectRule.GROUP_REPRESENTATIVE:
                                bGroupRepresentative = true;
                                if (anyGroupRepresentativeInPAGFound) {
                                    this.groupRepresentativeError = true;
                                    messages.add(this.getValidatorNotUniqueGroupRepresentativeMsg(pag));
                                    break OUTER;
                                }
                                anyGroupRepresentativeInPAGFound = true;
                                break;
                            // Check for protein interaction evidence (only in cross-linking case)
                            case ProteinAmbiguityGroupObjectRule.XL_INTERACTION_SCORE_PPL:
                            case ProteinAmbiguityGroupObjectRule.XL_INTERACTION_SCORE_RPL:
                                this.checkXLinteractionScores(cvParam, pag, pdh, messages);
                                break;
                            default:
                                break;
                        }
                    }
                }
                
                if (bLeadingPDH && bGroupRepresentative) {
                    this.leadingProteinError = false;
                }
            }
            
            // Check for group representative
            if (this.leadingProteinError && anyLeadingProteinInPAGFound) {
                messages.add(this.getValidatorMustContainGroupRepresentativeMsg(pag));
            }
            else {
                this.leadingProteinError = false;
            }
        }

        return messages;
    }

    /**
     * Checks the cross-linking interaction scores.
     * @param cvParam   the CvParam object of the ProteinDetectionHypothesis element
     * @param pag       the ProteinAmbiguityGroup element
     * @param pdh       the ProteinDetectionHypothesis element
     * @param messages  collection of messages
     */
    private void checkXLinteractionScores(CvParam cvParam, ProteinAmbiguityGroup pag, ProteinDetectionHypothesis pdh, List<ValidatorMessage> messages) {
        String cvValueXLInteractionScore;
        
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            String acc = cvParam.getAccession();
            if (this.isAXLInteractionScore(acc)) {                           
                cvValueXLInteractionScore = cvParam.getValue();
                if (!cvValueXLInteractionScore.matches(this.STR_REGEXP_XL_INTERACTION_SCORE)) {
                    this.xlInteractionScoreRegExError = true;
                    this.addRegexViolationMessageToCollection(cvParam, pag, pdh, messages);
                }
                else {
                    int pos = cvValueXLInteractionScore.indexOf(this.STR_COLON);
                    int posPoint = cvValueXLInteractionScore.indexOf(this.STR_POINT);
                    if (posPoint > this.NOT_FOUND && pos > posPoint) {
                        String xlInteractID = cvValueXLInteractionScore.substring(0, posPoint);
                        Character xlInteractIDSuffix = cvValueXLInteractionScore.substring(posPoint + 1, pos).charAt(0);
                        
                        String restStr = cvValueXLInteractionScore.substring(pos + 1);
                        int posNext = restStr.indexOf(this.STR_COLON);
                        int posLast = restStr.lastIndexOf(this.STR_COLON);
                        if (posNext > this.NOT_FOUND && posLast > posNext) {
                            boolean bIsLoopLink = false;
                            
                            String score = restStr.substring(posNext + 1, posLast);
                            ImmutablePair<String, String> key = new ImmutablePair<>(xlInteractID, score);
                            if (!ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.containsKey(key)) {
                                ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.put((ImmutablePair<String, String>) key, new HashMap<>());
                                ProteinAmbiguityGroupObjectRule.INTERACT_ID_TO_SUFFIX_MAP.put(xlInteractID, xlInteractIDSuffix);
                            }
                            else { // special handling for loop links
                                if (!ProteinAmbiguityGroupObjectRule.INTERACT_ID_TO_SUFFIX_MAP.get(xlInteractID).equals(xlInteractIDSuffix)) {
                                    ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.remove(key);    // special handling for loop-link
                                    bIsLoopLink = true;
                                }
                            }
                            
                            // only if key was not removed (because it is a loop-link, e.g. no proteolytic site exists between the intra-molecular cross-linked residues
                            if (!bIsLoopLink) {
                            //if (ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.containsKey(key)) {
                                HashMap<String, String> key2HashMap = ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.get(key);
                                if (key2HashMap.containsKey(pag.getId())) {
                                    String pdhIDFromMap = key2HashMap.get(pag.getId());
                                    if (!pdh.getId().equals(pdhIDFromMap)) {
                                        key2HashMap.put(pag.getId(), pdh.getId());
                                        ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.put(key, key2HashMap);
                                    }
                                }
                                else {
                                    key2HashMap.put(pag.getId(), pdh.getId());
                                    ProteinAmbiguityGroupObjectRule.XL_ID_SCORE_PAIR_TO_PAGID2PDHID.put(key, key2HashMap);
                                }
                            }
                        }
                    }
                    else {
                        messages.add(this.getValidatorWrongInteractionScoreMsg(pag, pdh));
                    }
                }
            }
            else {
                this.LOGGER.debug("Is not an XL interaction score: " + acc);
            }
        }
    }

    /**
     * Initializes the map for XL interaction score terms
     */
    private void initMap() {
        if (AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
            this.map_1002664 = this.getTermChildren("MS:1002664", "MS");
        }
    }
    
    /**
     * Checks, if a term is a child of MS:1002675.
     * @param acc
     * @return true, if the accession belongs to a CV term, which is a child of MS:1002675 ("cross-linking result details")
     */
    private boolean isAXLInteractionScore(String acc) {
        if (this.map_1002664 == null) {
            this.initMap();
        }
        
        return this.map_1002664.containsKey(acc);
    }
                        
    /**
     * Adds a message to the messages collection.
     * @param cvParam   the CvParam object
     * @param pdh       the ProteinDetectionHypothesis object
     * @param pag       the ProteinAmbiguityGroup object
     * @param messages  the list of messages
     */
    private void addRegexViolationMessageToCollection(CvParam cvParam, ProteinAmbiguityGroup pag, ProteinDetectionHypothesis pdh, List<ValidatorMessage> messages) {
        StringBuilder strB = new StringBuilder();
        
        strB.append("The regular expression for the cross-linking interaction score in ProteinDetectionHypothesis (id='");
        strB.append(pdh.getId());
        strB.append("' of ProteinAmbiguityGroup (id='");
        strB.append(pag.getId());
        strB.append("') element at ");
        strB.append(ProteinAmbiguityGroupObjectRule.PAG_CONTEXT.getContext());
        strB.append("/cvParam ('");
        strB.append(cvParam.getName());
        strB.append("') is not valid.");
        
        messages.add(new ValidatorMessage(strB.toString(), MessageLevel.ERROR, ProteinAmbiguityGroupObjectRule.PAG_CONTEXT, this));
    }
    
    /**
     * Gets the message for unique group representative.
     * @param pag the ProteinAmbiguityGroup element
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorNotUniqueGroupRepresentativeMsg(ProteinAmbiguityGroup pag) {
        StringBuilder strB = new StringBuilder();
        
        strB.append("Only one ProteinDetectionHypothesis in the ProteinAmbiguityGroup '");
        strB.append(pag.getId());
        strB.append("' can contain the CV Term ");
        strB.append(ProteinAmbiguityGroupObjectRule.GROUP_REPRESENTATIVE);
        strB.append(" (group representative) at ");
        
        return new ValidatorMessage(strB.toString() + ProteinAmbiguityGroupObjectRule.PAG_CONTEXT.getContext(), MessageLevel.ERROR, ProteinAmbiguityGroupObjectRule.PAG_CONTEXT, this);
    }
    
    /**
     * Gets the message for unique group representative.
     * @param pag the ProteinAmbiguityGroup element
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorMustContainGroupRepresentativeMsg(ProteinAmbiguityGroup pag) {
        StringBuilder strB = new StringBuilder();
        
        strB.append("At least one of the ProteinDetectionHypothesis labeled as 'leading protein' (");
        strB.append(ProteinAmbiguityGroupObjectRule.LEADING_PROTEIN);
        strB.append(") may be a 'group representative' (");
        strB.append(ProteinAmbiguityGroupObjectRule.GROUP_REPRESENTATIVE);
        strB.append(") in ProteinAmbiguityGroup id='");
        strB.append(pag.getId());
        strB.append("' at ");
        
        return new ValidatorMessage(strB.toString() + ProteinAmbiguityGroupObjectRule.PAG_CONTEXT.getContext(), MessageLevel.INFO, ProteinAmbiguityGroupObjectRule.PAG_CONTEXT, this);       
    }
    
    /**
     * Gets the message for a wrong interaction score.
     * @param pag the ProteinAmbiguitygroup element
     * @param pdh the ProteinDetectionHypothesis element
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorWrongInteractionScoreMsg(ProteinAmbiguityGroup pag, ProteinDetectionHypothesis pdh) {
        StringBuilder strB = new StringBuilder();
        
        strB.append("ProteinDetectionHypothesis contains an invalid cross-linking interaction score in ProteinDetectionHypothesis id='");
        strB.append(pdh.getId());
        strB.append("' of ProteinAmbiguityGroup id='");
        strB.append(pag.getId());
        strB.append("' at ");
        
        return new ValidatorMessage(strB.toString() + ProteinAmbiguityGroupObjectRule.PAG_CONTEXT.getContext(), MessageLevel.INFO, ProteinAmbiguityGroupObjectRule.PAG_CONTEXT, this);       
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();
        
        if (this.groupRepresentativeError) {
            ret.add("Remove the CV term 'group representative' (");
            ret.add(ProteinAmbiguityGroupObjectRule.GROUP_REPRESENTATIVE);
            ret.add(") from all the ProteinDetectionHypothesis in the Protein Ambiguity Group excepting one.");
        }
        
        if (this.leadingProteinError) {
            ret.add("Add the CV term 'group representative' (");
            ret.add(ProteinAmbiguityGroupObjectRule.GROUP_REPRESENTATIVE);
            ret.add(") to at least one of the ProteinDetectionHypothesis labeled as 'leading protein' (");
            ret.add(ProteinAmbiguityGroupObjectRule.LEADING_PROTEIN + ") in the ProteinAmbiguityGroup.");
        }
        
        if (this.xlInteractionScoreRegExError) {
            ret.add("The value for the CV term of the cross-linking interaction score must have the format 'int_ID.a|b:POS|null:SCORE_OR_VALUE:PASS_THRESHOLD' for child of (");
            ret.add(ProteinAmbiguityGroupObjectRule.XL_INTERACTION_SCORE);
            ret.add(".)");
        }
        
        return ret;
    }
}
