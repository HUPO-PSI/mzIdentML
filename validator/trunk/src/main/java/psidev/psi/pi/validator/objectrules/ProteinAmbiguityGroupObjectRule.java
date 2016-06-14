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

    /**
     * Constants.
     */
    private static final Context PAG_CONTEXT = new Context(MzIdentMLElement.ProteinAmbiguityGroup.getXpath());
    private static final String GROUP_REPRESENTATIVE = "MS:1002403";
    private static final String LEADING_PROTEIN = "MS:1002401";

    /**
     * Members.
     */
    private boolean groupRepresentativeError = false;
    private boolean groupRepresentativeError2 = false;

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
                                messages.add(this.getValidatorNotUniqueGroupRepresentativeMsg(pag));
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
            
            // Check for group representative
            if (this.groupRepresentativeError2 && anyLeadingProteinInPAGFound) {
                messages.add(this.getValidatorMustContainGroupRepresentativeMsg(pag));
            }
            else {
                this.groupRepresentativeError2 = false;
            }
            
            // Check for protein interaction evidence (only in cross-linking case)
            // TODO: implement
            
        }

        return messages;
    }

    /**
     * Gets the message for unique group representative.
     * @param pag the ProteinAmbiguityGroup element
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorNotUniqueGroupRepresentativeMsg(ProteinAmbiguityGroup pag) {
        String strB = "Only one ProteinDetectionHypothesis in the ProteinAmbiguityGroup '" +
                       pag.getId() + "' can contain the CV Term " + GROUP_REPRESENTATIVE + " (group representative) at ";
        return new ValidatorMessage(strB + PAG_CONTEXT.getContext(), MessageLevel.ERROR, PAG_CONTEXT, this);
    }
    
    /**
     * Gets the message for unique group representative.
     * @param pag the ProteinAmbiguityGroup element
     * @return the ValidatorMessage
     */
    private ValidatorMessage getValidatorMustContainGroupRepresentativeMsg(ProteinAmbiguityGroup pag) {
        String strB = "At least one of the ProteinDetectionHypothesis labeled as 'leading protein' (" +
                      LEADING_PROTEIN + ") may be a 'group representative' (" + GROUP_REPRESENTATIVE +
                      ") in Protein Ambiguity Group id='" + pag.getId() + "' at ";
        return new ValidatorMessage(strB + PAG_CONTEXT.getContext(), MessageLevel.INFO, PAG_CONTEXT, this);       
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
            ret.add("Remove the CV term 'group representative' ("
                + GROUP_REPRESENTATIVE
                + ") from all the ProteinDetectionHypothesis in the Protein Ambiguity Group excepting one, at "
                + ProteinAmbiguityGroupObjectRule.PAG_CONTEXT.getContext());
        }
        
        if (this.groupRepresentativeError2) {
            ret.add("Add the CV term 'group representative' ("
                + GROUP_REPRESENTATIVE
                + ") to at least one of the ProteinDetectionHypothesis labeled as 'leading protein' ("
                + LEADING_PROTEIN + ") in the Protein Ambiguity Group at "
                + PAG_CONTEXT.getContext());
        }
        
        return ret;
    }
}
