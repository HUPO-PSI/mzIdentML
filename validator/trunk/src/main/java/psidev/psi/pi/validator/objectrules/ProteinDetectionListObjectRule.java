package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import psidev.psi.pi.validator.MzIdentMLValidator;

import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinAmbiguityGroup;
import uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionList;

/**
 * Iterating over all PAGs and if one of them contains a "cluster identifier"
 * CV, all the rest also must have it. Check also that the value of the CV is an integer.
 * 
 * @author Salva
 * 
 */
public class ProteinDetectionListObjectRule extends AObjectRule<ProteinDetectionList> {

    /**
     * Constants.
     */
    private static final Context PAG_CONTEXT = new Context(MzIdentMLElement.ProteinAmbiguityGroup.getXpath());
    private static final Context PDL_CONTEXT = new Context(MzIdentMLElement.ProteinDetectionList.getXpath());
    
    private static final String COUNT_OF_IDENTIFIED_PROTEINS    = "MS:1002404";
    private static final String PROTEIN_CLUSTER_IDENTIFIER_CV   = "MS:1002407";

    /**
     * Members.
     */
    private boolean clusterIdentifierTypeError = false;
    private boolean clusterIdentifierUseError = false;
    public static boolean bContainsCountsOfIdentifiedProteins = false;

    /**
     * Constructor.
     */
    public ProteinDetectionListObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ProteinDetectionListObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a ProteinDetectionList.
     * 
     * @param obj   the object to check
     * @return true, if obj is a ProteinDetectionList
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof ProteinDetectionList);
    }

    /**
     * 
     * @param pdl the ProteinDetectionList element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(ProteinDetectionList pdl) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        List<ProteinAmbiguityGroup> proteinAmbiguityGroups = pdl.getProteinAmbiguityGroup();
        // check if one PAG contains a protein cluster
        if (proteinAmbiguityGroups != null) {
            boolean aClusterIdentifierFound = false;
            for (ProteinAmbiguityGroup pag : proteinAmbiguityGroups) {
                if (pag.getCvParam() != null) {
                    for (CvParam cvParam : pag.getCvParam()) {
                        if (cvParam.getAccession().equals(ProteinDetectionListObjectRule.PROTEIN_CLUSTER_IDENTIFIER_CV)) {
                            aClusterIdentifierFound = true;
                            break;
                        }
                    }
                    if (aClusterIdentifierFound) {
                        break;
                    }
                }
            }
            
            if (aClusterIdentifierFound) {
                // iterate again over all the PAGs to check that ALL of them contain the protein identifier cluster
                for (ProteinAmbiguityGroup pag : proteinAmbiguityGroups) {
                    aClusterIdentifierFound = false;
                    if (pag.getCvParam() != null) {
                        for (CvParam cvParam : pag.getCvParam()) {
                            if (cvParam.getAccession().equals(ProteinDetectionListObjectRule.PROTEIN_CLUSTER_IDENTIFIER_CV)) {
                                aClusterIdentifierFound = true;
                                try {
                                    Integer.valueOf(cvParam.getValue());
                                }
                                catch (NumberFormatException e) {
                                    this.clusterIdentifierTypeError = true;
                                    messages.add(new ValidatorMessage(
                                        "The value associated with the CV term 'cluster identifier' ("
                                        + PROTEIN_CLUSTER_IDENTIFIER_CV
                                        + ") is not a valid integer value (id="
                                        + pag.getId()
                                        + "') element at "
                                        + ProteinDetectionListObjectRule.PAG_CONTEXT.getContext(),
                                        MessageLevel.ERROR,
                                        ProteinDetectionListObjectRule.PAG_CONTEXT, this));
                                }
                            }
                        } // rof
                    }

                    if (!aClusterIdentifierFound) {
                        // just add the message once
                        if (!this.clusterIdentifierUseError) {
                            messages.add(new ValidatorMessage(
                                "The use of the CV term 'cluster identifier' ("
                                + PROTEIN_CLUSTER_IDENTIFIER_CV
                                + ") has been detected in some Protein Ambiguity Group (PAG) but not in all in the PDL "
                                + pdl.getId()
                                + "') element at "
                                + ProteinDetectionListObjectRule.PDL_CONTEXT.getContext(),
                                MessageLevel.ERROR,
                                ProteinDetectionListObjectRule.PAG_CONTEXT, this));
                        }

                        this.clusterIdentifierUseError = true;
                    }
                } // rof
            }
        } // fi PAG's != null
        
        // Quick and dirty hack; unfortunately sometimes no cvParams are found, even if they are present
        List<CvParam> cvParamList = pdl.getCvParam();
        for (CvParam cvp: cvParamList) {
            if (cvp.getAccession().equals(ProteinDetectionListObjectRule.COUNT_OF_IDENTIFIED_PROTEINS)) {
                ProteinDetectionListObjectRule.bContainsCountsOfIdentifiedProteins = true;
                break;
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
        
        if (this.clusterIdentifierUseError) {
            ret.add("Add the appropriate CV term 'cluster identifier' ("
                + ProteinDetectionListObjectRule.PROTEIN_CLUSTER_IDENTIFIER_CV
                + ") to all the Protein Ambiguity Groups.");
        }
        
        if (this.clusterIdentifierTypeError) {
            ret.add("Introduce a valid Integer value on the CV term 'cluster identifier' ("
                + ProteinDetectionListObjectRule.PROTEIN_CLUSTER_IDENTIFIER_CV + ").");
        }
        
        if (!ProteinDetectionListObjectRule.bContainsCountsOfIdentifiedProteins && MzIdentMLValidator.currentFileVersion.equals(MzIdentMLValidator.MzIdVersion._1_2)) {
            ret.add("ProteinDetectionList must contain a CV term MS:1002404 (count of identified proteins).");
        }
        
        return ret;
    }
}
