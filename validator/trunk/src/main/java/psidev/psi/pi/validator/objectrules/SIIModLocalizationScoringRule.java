package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationItem;

/**
 * Check if there is a regular expression in the cvParam 'modification rescoring:false localization rate'.
 * 
 * @author Gerhard
 * 
 */
public class SIIModLocalizationScoringRule extends AObjectRule<SpectrumIdentificationItem> {

    /**
     * Constants.
     */
    private static final Context SII_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationItem.getXpath());
    private final String STR_REGEXP_MOD_LOCALIZATION = "(\\d+:\\d+.\\d+[Ee]{0,1}[+-]{0,1}\\d*:\\d+[|]*\\d*:(true|false){1})";

    /**
     * Constructors.
     */
    public SIIModLocalizationScoringRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public SIIModLocalizationScoringRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SpectrumIdentificationItem.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SpectrumIdentificationItem
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SpectrumIdentificationItem);
    }

    /**
     * Checks, if the modification localization is compatible with the regex.
     * 
     * @param sii the SpectrumIdentificationItem element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationItem sii) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsModificationLocalizationScoring) {
            for (CvParam cv: sii.getCvParam()) {
                if (cv != null) {
                    switch (cv.getAccession()) {
                        case "MS:1001969":  // phosphoRS score
                        case "MS:1001970":  // phosphoRS sequence probability
                        case "MS:1001971":  // phosphoRS site probability
                        case "MS:1001974":  // DeBunker:score
                        case "MS:1001978":  // MSQuant:PTM-score
                        case "MS:1001979":  // MaxQuant:PTM Score
                        case "MS:1001980":  // MaxQuant:Phospho (STY) Probabilities
                        case "MS:1001982":  // MaxQuant:P-site localization probability
                        case "MS:1001983":  // MaxQuant:PTM Delta Score
                        case "MS:1001985":  // Ascore
                        case "MS:1001986":  // H-Score
                        case "MS:1002507":  // modification rescoring:false localization rate
                        case "MS:1002536":  // D-Score
                        case "MS:1002537":  // MD-Score
                        case "MS:1002550":  // peptide:phosphoRS score
                        case "MS:1002551":  // peptide:Ascore
                        case "MS:1002552":  // peptide:H-Score
                        case "MS:1002553":  // peptide:D-Score
                        case "MS:1002554":  // peptide:MD-Score
                            if (!cv.getValue().matches(this.STR_REGEXP_MOD_LOCALIZATION)) {
                                this.addMessageToCollection(cv, sii, messages);
                            }
                            break;
                    }
                }
            }
        }

        return messages;
    }

    /**
     * Adds a message to the messages collection.
     * @param cv
     * @param sii
     * @param messages 
     */
    private void addMessageToCollection(CvParam cv, SpectrumIdentificationItem sii, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The regular expression in SpectrumIdentificationItem (id='"
        + sii.getId() + "') element at " + SIIModLocalizationScoringRule.SII_CONTEXT.getContext()
        + "/cvParam ('" + cv.getName() + "') is not valid.", MessageLevel.ERROR, SIIModLocalizationScoringRule.SII_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("The value for the cvParam 'modification localization scoring' under the SpectrumIdentificationItem must have the format MOD_ORDER:SCORE:POSITION:PASS_THRESHOLD at" + SIIModLocalizationScoringRule.SII_CONTEXT.getContext());
        ret.add("with MOD_ORDER = <Modification> element order in the referenced <Peptide> object");
        ret.add("SCORE = Score or statistical measure associated with the modification position");
        ret.add("POSITION = Position of the modification on the peptide (N-terminus = 0, C-terminus = peptide length + 1). If the score pertains to grouped positions, different positions MUST be separated by '|'");
        ret.add("PASS_THRESHOLD = true | false with regards to the threshold specified in Feature A. If no Threshold has been specified, this MUST always be true");
        ret.add("e.g. '1:0.03:2|3:true' or '1:0.97:8|9:false'");
        ret.add("according to the regular expression ");
        ret.add(this.STR_REGEXP_MOD_LOCALIZATION);

        return ret;
    }
}
