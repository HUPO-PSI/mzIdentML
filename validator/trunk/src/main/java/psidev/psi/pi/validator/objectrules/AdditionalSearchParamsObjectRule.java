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

        int cnt = 0;
        for (CvParam cvp: sip.getAdditionalSearchParams().getCvParam()) {
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

        ret.add("The AdditionalSearchParams element of SpectrumIdentificationProtocol must contain a CV term for the type of 'special processing' at" + AdditionalSearchParamsObjectRule.SIP_CONTEXT.getContext());

        return ret;
    }
}
