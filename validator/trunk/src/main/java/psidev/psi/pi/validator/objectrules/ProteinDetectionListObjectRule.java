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
import uk.ac.ebi.jmzidml.model.mzidml.ProteinDetectionList;

/**
 * iterating over all PAGs and if one of them contains a "cluster identifier"
 * CV, all the rest will also must have it. Check also that the value of the CV is an integer.
 * 
 * @author Salva
 * 
 */
public class ProteinDetectionListObjectRule extends AObjectRule<ProteinDetectionList> {

    // Contexts
    private static final Context PAGContext = new Context(MzIdentMLElement.ProteinAmbiguityGroup.getXpath());
    private static final Context PDLContext = new Context(MzIdentMLElement.ProteinDetectionList.getXpath());
    private static final String PROTEIN_CLUSTER_IDENTIFIER_CV = "MS:1002407";

    private boolean clusterIdentifierTypeError = false;
    private boolean clusterIdentifierUseError = false;

    // We had a problem with the default constructor. It was necessary to build a new one this way to call the ObjectRule
    public ProteinDetectionListObjectRule() {
        this(null);
    }

    // Another constructor that calls to ObjectRule
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
     * @throws ValidatorException 
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
                        if (cvParam.getAccession().equals(PROTEIN_CLUSTER_IDENTIFIER_CV)) {
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
                            if (cvParam.getAccession().equals(PROTEIN_CLUSTER_IDENTIFIER_CV)) {
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
                                        + PAGContext.getContext(),
                                        MessageLevel.ERROR,
                                        PAGContext, this));
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
                                + PDLContext.getContext(),
                                MessageLevel.ERROR,
                                PAGContext, this));
                        }

                        this.clusterIdentifierUseError = true;
                    }
                } // rof
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
                + PROTEIN_CLUSTER_IDENTIFIER_CV
                + ") to all the Protein Ambiguity Groups at "
                + ProteinDetectionListObjectRule.PAGContext.getContext());
        }
        
        if (this.clusterIdentifierTypeError) {
            ret.add("Introduce a valid Integer value on the CV term 'cluster identifier' ("
                + PROTEIN_CLUSTER_IDENTIFIER_CV
                + ") at "
                + ProteinDetectionListObjectRule.PAGContext.getContext());
        }
        
        return ret;
    }
}
