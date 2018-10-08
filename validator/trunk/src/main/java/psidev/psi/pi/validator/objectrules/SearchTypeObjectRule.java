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
 * Checks if a term for special processing (de 'novo search' or 'spectral library search' is present in SearchType of the SpectrumIdentificationProtocol.
 * 
 * @author Gerhard
 * 
 */
public class SearchTypeObjectRule extends AObjectRule<SpectrumIdentificationProtocol> {

    /**
     * Constants.
     */
    private static final Context ST_CONTEXT = new Context(MzIdentMLElement.SpectrumIdentificationProtocol.getXpath());
    
    /**
     * Members.
     */
    public static boolean bIsDeNovoSearch           = false;
    public static boolean bIsSpectralLibrarySearch  = false;

    /**
     * Constructors.
     */
    public SearchTypeObjectRule() {
        this(null);
    }

    /**
     * 
     * @param ontologyManager  the ontology manager
     */
    public SearchTypeObjectRule(OntologyManager ontologyManager) {
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
        CvParam cvp = sip.getSearchType().getCvParam();
        switch (cvp.getAccession()) {
            case "MS:1001010":  // de novo search
                cnt++;
                bIsDeNovoSearch = true;
                break;
            case "MS:1001031":  // spectral library search
                cnt++;
                bIsSpectralLibrarySearch = true;
                break;
            case "MS:1001081":  // pmf search
            case "MS:1001082":  // tag search
            case "MS:1001083":  // ms-ms search
            case "MS:1001584":  // combined pmf + ms-ms search
                cnt++;
        }
        
        if (cnt == 0) {
            messages.add(new ValidatorMessage("At least one child term of 'search type' must occur in SearchType of the SpectrumIdentificationProtocol (id='"
            + sip.getId() + "') element at " + SearchTypeObjectRule.ST_CONTEXT.getContext(),
            MessageLevel.ERROR, SearchTypeObjectRule.ST_CONTEXT, this));
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

        ret.add("The SearchType element of SpectrumIdentificationProtocol must contain a CV term.");

        return ret;
    }
}
