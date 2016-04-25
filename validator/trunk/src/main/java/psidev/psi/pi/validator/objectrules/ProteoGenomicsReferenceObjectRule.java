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
import uk.ac.ebi.jmzidml.model.mzidml.SearchDatabase;

/**
 * Check if the heptapletMsg of mandatory CV terms is present for a proteogenomics search.
 * 
 * @author Gerhard
 * 
 */
public class ProteoGenomicsReferenceObjectRule extends AObjectRule<SearchDatabase> {

    /**
     * Constants.
     */
    private static final Context SDB_CONTEXT = new Context(MzIdentMLElement.SearchDatabase.getXpath());
    private final String msg = " genome reference version (MS:1002644) ";

    /**
     * Members.
     */
    private boolean genomeReferenceVersionTerm = false;
    
    /**
     * Constructors.
     */
    public ProteoGenomicsReferenceObjectRule() {
        this(null);
    }

    /**
     * 
     * @param ontologyManager the ontology manager
     */
    public ProteoGenomicsReferenceObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Checks, if the object is a SearchDatabase.
     * 
     * @param obj   the object to check
     * @return true, if obj is a SearchDatabase
     */
    @Override
    public boolean canCheck(Object obj) {
        return (obj instanceof SearchDatabase);
    }

    /**
     * Checks, if the required heptapletMsg of terms is present in case of a proteogenomics search.
     * 
     * @param pev the SearchDatabase element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(SearchDatabase pev) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();

        if (AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch) {
            String acc;
            boolean bFound = false;
            
            for (CvParam cv: pev.getCvParam()) {
                acc = cv.getAccession();
                switch (acc) {
                    case "MS:1002644":  // genome reference version
                        this.genomeReferenceVersionTerm = true;
                        bFound = true;
                        break;
                }
                if (bFound) {
                    break;
                }
            }
            
            if (!this.genomeReferenceVersionTerm) {
                this.addMessageToCollection(pev, messages);
            }
        }

        return messages;
    }
    
    /**
     * Adds a message to the messages collection.
     * @param sdb
     * @param messages 
     */
    private void addMessageToCollection(SearchDatabase sdb, List<ValidatorMessage> messages) {
        messages.add(new ValidatorMessage("The Searchdatabase (id='"
        + sdb.getId() + "') element at " + ProteoGenomicsReferenceObjectRule.SDB_CONTEXT.getContext()
        + " doesn't contain the " + this.msg + "required in case of a proteogenomics search", MessageLevel.ERROR, ProteoGenomicsReferenceObjectRule.SDB_CONTEXT, this));
    }
    
    /**
     * Gets the tips how to fix the error.
     * 
     * @return collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        ret.add("Add the" + this.msg + "to each SearchDatabase in case of a proteogenomics search.");
        
        return ret;
    }
}
