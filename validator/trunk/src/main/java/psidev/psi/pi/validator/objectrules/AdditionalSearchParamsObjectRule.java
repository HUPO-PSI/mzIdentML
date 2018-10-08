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
import uk.ac.ebi.jmzidml.model.mzidml.SpectrumIdentificationProtocol;

/**
 * Checks if a term for special processing is present in AdditionalSearchParams of the SpectrumIdentificationProtocol.
 * 
 * @author Gerhard
 * 
 */
public class AdditionalSearchParamsObjectRule extends AObjectRule<SpectrumIdentificationProtocol> {

    /**
     * Constants.
     */
    private static final Context SIP_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationProtocol.getXpath());
    private final int CNT_MAX = 5;
    
    /**
     * Members.
     */
    public static boolean bIsPeptideLevelScoring                = false;
    public static boolean bIsProteoGenomicsSearch               = false;
    public static boolean bIsModificationLocalizationScoring    = false;
    public static boolean bIsConsensusScoring                   = false;
    public static boolean bIsSamplePreFractionation             = false;
    public static boolean bIsCrossLinkingSearch                 = false;
    public static boolean bIsNoSpecialProcessing                = false;
    
    private boolean bThresholdFoundForModPositionScoring = false;

    /**
     * Constructors.
     */
    public AdditionalSearchParamsObjectRule() {
        this(null);
    }

    /**
     * 
     * @param ontologyManager  the ontology manager
     */
    public AdditionalSearchParamsObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SpectrumIdentificationProtocol.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SpectrumIdentificationProtocol
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SpectrumIdentificationProtocol);
    }

    /**
     * Checks, what special kind of mzIdentML file we have.
     * 
     * @param sip the SpectrumIdentificationProtocol element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SpectrumIdentificationProtocol sip) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        // check for the special processing types
        int cnt = 0;
        if (sip.getAdditionalSearchParams()!=null) {
            for (CvParam cvp: sip.getAdditionalSearchParams().getCvParam()) {
                if (cvp != null) {
                    switch (cvp.getAccession()) {
                        case "MS:1002490":  // peptide-level scoring
                            cnt++;
                            bIsPeptideLevelScoring = true;
                            break;
                        case "MS:1002491":  // modification localization scoring
                            cnt++;
                            bIsModificationLocalizationScoring = true;
                            break;
                        case "MS:1002492":  // consensus scoring
                            cnt++;
                            bIsConsensusScoring = true;
                            break;
                        case "MS:1002493":  // sample pre-fractionation
                            cnt++;
                            bIsSamplePreFractionation = true;
                            break;
                        case "MS:1002494":  // cross-linking search
                            cnt++;
                            bIsCrossLinkingSearch = true;
                            break;
                        case "MS:1002635":  // proteogenomics search
                            cnt++;
                            bIsProteoGenomicsSearch = true;
                            break;
                        case "MS:1002495":  // no special processing
                            cnt++;
                            bIsNoSpecialProcessing = true;
                            break;
                    }
                }
            } // rof
        
            if (cnt == 0) {
            messages.add(new ValidatorMessage("At least one child term of 'special processing' must occur in the AdditionalSearchParams of the SpectrumIdentificationProtocol (id='"
            + sip.getId() + "') element at " + AdditionalSearchParamsObjectRule.SIP_CONTEXT.getContext(),
                    MessageLevel.ERROR, AdditionalSearchParamsObjectRule.SIP_CONTEXT, this));
            }
            else if (cnt > this.CNT_MAX) {
            messages.add(new ValidatorMessage("Found more than " + this.CNT_MAX + " childs term of 'special processing' in the AdditionalSearchParams of the SpectrumIdentificationProtocol (id='"
            + sip.getId() + "') element at " + AdditionalSearchParamsObjectRule.SIP_CONTEXT.getContext(),
            MessageLevel.INFO, AdditionalSearchParamsObjectRule.SIP_CONTEXT, this));
            }
        }
        // check for threshold element in case of modification position scoring
        if (bIsModificationLocalizationScoring) {
            for (CvParam cvp: sip.getThreshold().getCvParam()) {
                if (cvp != null) {
                    switch (cvp.getAccession()) {
                        case "MS:1002556":  // Ascore threshold
                        case "MS:1002557":  // D-Score threshold
                        case "MS:1002558":  // MD-Score threshold
                        case "MS:1002559":  // H-Score threshold
                        case "MS:1002560":  // DeBunker:score threshold
                        case "MS:1002561":  // Mascot:PTM site assignment confidence threshold
                        case "MS:1002562":  // MSQuant:PTM-score threshold
                        case "MS:1002563":  // MaxQuant:PTM Score threshold
                        case "MS:1002564":  // MaxQuant:P-site localization probability threshold
                        case "MS:1002565":  // MaxQuant:PTM Delta Score threshold
                        case "MS:1002566":  // MaxQuant:Phospho (STY) Probabilities threshold
                        case "MS:1002567":  // phosphoRS score threshold
                        case "MS:1002668":  // phosphoRS site probability threshold
                        case "MS:1002672":  // no modification threshold
                            this.bThresholdFoundForModPositionScoring = true;
                            break;
                    }
                }
            }
                
            if (!this.bThresholdFoundForModPositionScoring) {
                messages.add(new ValidatorMessage("At child term of 'PTM localization score threshold' must occur for modification position scoring in the Threshold element under the SpectrumIdentificationProtocol (id='"
                + sip.getId() + "') element at " + AdditionalSearchParamsObjectRule.SIP_CONTEXT.getContext(),
                MessageLevel.ERROR, AdditionalSearchParamsObjectRule.SIP_CONTEXT, this));
            }
        }
        
        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("The AdditionalSearchParams element of SpectrumIdentificationProtocol must contain a CV term for the type of 'special processing'.");

        return ret;
    }
}
