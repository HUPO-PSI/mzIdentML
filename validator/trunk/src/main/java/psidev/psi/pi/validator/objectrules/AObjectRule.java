package psidev.psi.pi.validator.objectrules;

import java.rmi.RemoteException;
import java.util.HashMap;
import javax.xml.rpc.ServiceException;
import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import uk.ac.ebi.ols.soap.Query;
import uk.ac.ebi.ols.soap.QueryServiceLocator;

/**
 * Abstract base class for all custom ObjectRules.
 * 
 * @author Gerhard
 * @param <T> 
 */
public abstract class AObjectRule<T extends Object> extends ObjectRule<T> {

    /**
     * Members.
     */
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * Constructor.
     */
    public AObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager 
     */
    public AObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
    }

    /**
     * Gets the ID.
     * @return the Id (name) for this object.
     */
    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * Calls OLS webserver and gets child terms for a termId.
     * @param ontology
     * @param termId
     * @return HashMap of child terms - key is termId, value is termName.
     */
    protected HashMap<String, String> getTermChildren(String ontology, String termId) {
        HashMap<String, String> retMap = new HashMap<>();
        
        try {
            Query olsQuery = new QueryServiceLocator().getOntologyQuery();
            @SuppressWarnings("unchecked")
            HashMap<String, String> terms = olsQuery.getTermChildren(ontology, termId, -1, null);
            if (terms != null){
                retMap.putAll(terms);
            } 
        }
        catch (ServiceException | RemoteException ex) {
            ex.printStackTrace(System.err);
        }

        return retMap;
    }
}
