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
import uk.ac.ebi.jmzidml.model.mzidml.ProteinAmbiguityGroup;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionHypothesis;

/**
 * This rule evaluates:<br>
 * <ul>
 * <li>only one PDH in a PAG can be labeled as "group representative"
 * (MS:1002403), and if found, the PDH must be labeled as a "leading protein"
 * (MS:1002401), otherwise, a level-ERROR message will be reported.</li>
 * <li>if all the PDHs labeled as "leading protein" in a PAG do not contain
 * "group representative", a level-INFO message will be reported.</li>
 * <li>there is not any PDH in a PAG with "group representative" term, report a
 * level-INFO message</li>
 * </ul>
 * 
 * @author Salva
 * 
 */
public class ProteinAmbiguityGroupObjectRule extends AObjectRule<ProteinAmbiguityGroup> {

    // Contexts
    private static final Context PAGContext = new Context(MzIdentMLElement.ProteinAmbiguityGroup.getXpath());
    private static final String GROUP_REPRESENTATIVE = "MS:1002403";
    private static final String LEADING_PROTEIN = "MS:1002401";

    private boolean groupRepresentativeError = false;
    private boolean groupRepresentativeError2 = false;

    // We had a problem with the default constructor. It was necessary to build a new one this way to call the ObjectRule
    // constructor (below):
   public ProteinAmbiguityGroupObjectRule() {
        this(null);
    }

    // Another constructor that calls to ObjectRule
    public ProteinAmbiguityGroupObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a ProteinAmbiguityGroup.
     * 
     * @param obj
     * @return true, if obj is a ProteinAmbiguityGroup
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof ProteinAmbiguityGroup);
    }

    @Override
    public Collection<ValidatorMessage> check(ProteinAmbiguityGroup pag) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        List<ProteinDetectionHypothesis> pdhs = pag.getProteinDetectionHypothesis();
        if (pdhs != null) {
            boolean anyGroupRepresentativeInPAGFound = false;
            boolean anyLeadingProteinInPAGFound = false;
            this.groupRepresentativeError2 = true;

            // until finding a PDH labeled as leading protein and group representative
            for (ProteinDetectionHypothesis pdh : pdhs) {
                boolean leadingPDH = false;
                boolean groupRepresentative = false;
                // to evaluate that ONLY one PDH in a PAG can be labeled as "group representative" (MS:1002403)

                // and if all the PDHs labeled as "leading protein" in a PAG are not also labeled do
                // not contain "group representative", a level-INFO message will be reported.
                List<CvParam> cvParams = pdh.getCvParam();
                if (cvParams != null) {
                    for (CvParam cvParam : cvParams) {
                        if (cvParam.getAccession().equals(LEADING_PROTEIN)) {
                            leadingPDH = true;
                        }

                        if (cvParam.getAccession().equals(GROUP_REPRESENTATIVE)) {
                            groupRepresentative = true;
                            if (anyGroupRepresentativeInPAGFound) {
                                this.groupRepresentativeError = true;
                                messages.add(new ValidatorMessage(
                                    "Only one ProteinDetectionHypothesis in the ProteinAmbiguityGroup '"
                                    + pag.getId()
                                    + "' can contain the CV Term "
                                    + GROUP_REPRESENTATIVE
                                    + " (group representative) at "
                                    + PAGContext.getContext(),
                                    MessageLevel.ERROR,
                                    PAGContext, this));
                                break;
                            }
                            anyGroupRepresentativeInPAGFound = true;
                        }
                    }
                }
                
                if (leadingPDH && groupRepresentative) {
                    this.groupRepresentativeError2 = false;
                }
            }
            
            if (this.groupRepresentativeError2 && anyLeadingProteinInPAGFound) {
                messages.add(new ValidatorMessage(
                    "At least one of the ProteinDetectionHypothesis labeled as 'leading protein' ("
                    + LEADING_PROTEIN
                    + ") may be a 'group representative' ("
                    + GROUP_REPRESENTATIVE
                    + ") in Protein Ambiguity Group id='"
                    + pag.getId() + "' at "
                    + PAGContext.getContext(),
                    MessageLevel.INFO, PAGContext, this));
            }
            else {
                this.groupRepresentativeError2 = false;
            }
        }

        return messages;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return Collection<>
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();
        
        if (this.groupRepresentativeError) {
            ret.add("Remove the CV term 'group representative' ("
                + GROUP_REPRESENTATIVE
                + ") from all the ProteinDetectionHypothesis in the Protein Ambiguity Group excepting one, at "
                + ProteinAmbiguityGroupObjectRule.PAGContext.getContext());
        }
        
        if (this.groupRepresentativeError2) {
            ret.add("Add the CV term 'group representative' ("
                + GROUP_REPRESENTATIVE
                + ") to at least one of the ProteinDetectionHypothesis labeled as 'leading protein' ("
                + LEADING_PROTEIN + ") in the Protein Ambiguity Group at "
                + PAGContext.getContext());
        }
        
        return ret;
    }
}
