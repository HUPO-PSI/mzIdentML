package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import psidev.psi.pi.validator.objectrules.util.ObjectRuleUtil;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis;

/**
 * Check if the values of the terms 'number of matched peaks', 'number of
 * unmatched peaks' or 'number of submitted peaks' are empty or not
 * 
 * @author Salva
 * 
 */
public class MatchedUnmatchedSubmittedPeaksValueObjectRule extends AObjectRule<ProteinDetectionHypothesis> {

    /**
     * Constants.
     */
    private static final Context PDH_CONTEXT = new Context(MzIdentMLElement.ProteinDetectionHypothesis.getXpath());
    private static final String MATCHED_PEAKS_ACC = "MS:1001121";
    private static final String UNMATCHED_PEAKS_ACC = "MS:1001362";
    private static final String SUBMITTED_PEAKS_ACC = "MS:1001124";
    
    /**
     * Members.
     */
    private boolean matchedValueError = false;
    private boolean unmatchedValueError = false;
    private boolean submittedValueError = false;

    /**
     * Constructor.
     */
    public MatchedUnmatchedSubmittedPeaksValueObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public MatchedUnmatchedSubmittedPeaksValueObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a ProteinDetectionHypothesis.
     * 
     * @param obj   the object to check
     * @return true, if obj is a ProteinDetectionHypothesis
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof ProteinDetectionHypothesis);
    }

    /**
     * 
     * @param proteinDetectionHypotesis the ProteinDetectionHypothesis element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(ProteinDetectionHypothesis proteinDetectionHypotesis) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        // number of matched peaks
        final CvParam matchedPeaksTerm = ObjectRuleUtil.checkAccessionsInCVParams(proteinDetectionHypotesis.getCvParam(), MATCHED_PEAKS_ACC);
        if (matchedPeaksTerm != null) {
            if (matchedPeaksTerm.getValue() == null || matchedPeaksTerm.getValue().isEmpty()) {
                this.matchedValueError = true;
                messages.add(new ValidatorMessage(
                    "There is not a value for the term 'number of matched peaks' ('"
                    + MATCHED_PEAKS_ACC + "') in the ProteinDetectionHypotesis (id='"
                    + proteinDetectionHypotesis.getId() + "') element at "
                    + MatchedUnmatchedSubmittedPeaksValueObjectRule.PDH_CONTEXT.getContext() + "/cvParam",
                    MessageLevel.ERROR, MatchedUnmatchedSubmittedPeaksValueObjectRule.PDH_CONTEXT, this));
            }
        }

        // number of unmatched peaks
        final CvParam unmatchedPeaksTerm = ObjectRuleUtil.checkAccessionsInCVParams(proteinDetectionHypotesis.getCvParam(), UNMATCHED_PEAKS_ACC);
        if (unmatchedPeaksTerm != null) {
            if (unmatchedPeaksTerm.getValue() == null || unmatchedPeaksTerm.getValue().isEmpty()) {
                this.unmatchedValueError = true;
                messages.add(new ValidatorMessage(
                    "There is not a value for the term 'number of unmatched peaks' ('"
                    + UNMATCHED_PEAKS_ACC + "') in the ProteinDetectionHypotesis (id='"
                    + proteinDetectionHypotesis.getId() + "') element at "
                    + MatchedUnmatchedSubmittedPeaksValueObjectRule.PDH_CONTEXT.getContext() + "/cvParam",
                    MessageLevel.ERROR, MatchedUnmatchedSubmittedPeaksValueObjectRule.PDH_CONTEXT, this));
            }
        }

        // number of peaks submitted
        final CvParam submittedPeaksTerm = ObjectRuleUtil.checkAccessionsInCVParams(proteinDetectionHypotesis.getCvParam(), SUBMITTED_PEAKS_ACC);
        if (submittedPeaksTerm != null) {
            if (submittedPeaksTerm.getValue() == null || submittedPeaksTerm.getValue().isEmpty()) {
                this.submittedValueError = true;
                
                messages.add(new ValidatorMessage(
                    "There is not a value for the term 'number of peaks submitted' ('"
                    + SUBMITTED_PEAKS_ACC + "') in the ProteinDetectionHypotesis (id='"
                    + proteinDetectionHypotesis.getId() + "') element at "
                    + MatchedUnmatchedSubmittedPeaksValueObjectRule.PDH_CONTEXT.getContext() + "/cvParam",
                    MessageLevel.ERROR, MatchedUnmatchedSubmittedPeaksValueObjectRule.PDH_CONTEXT, this));
            }
        }

        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return  collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();
        
        if (this.matchedValueError) {
            ret.add("Add a non-empty value to the term the element 'number of matched peaks' ('" + MATCHED_PEAKS_ACC + "') for ProteinDetectionHypothesis.");
        }
        if (this.unmatchedValueError) {
            ret.add("Add a non-empty value to the term the element 'number of unmatched peaks' ('" + UNMATCHED_PEAKS_ACC + "') for ProteinDetectionHypothesis.");
        }
        if (this.submittedValueError) {
            ret.add("Add a non-empty value to the term the element 'number of peaks submitted' ('" + SUBMITTED_PEAKS_ACC + "') for ProteinDetectionHypothesis.");
        }
        
        return ret;
    }
}
