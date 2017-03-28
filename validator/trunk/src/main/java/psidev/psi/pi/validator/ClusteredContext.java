package psidev.psi.pi.validator;

import java.util.ArrayList;
import java.util.List;

import psidev.psi.tools.validator.Context;

/**
 * A context which cluster the different contexts for a same error message.
 * 
 */
public class ClusteredContext extends Context {

    /**
     * The list of Context's.
     */
    private final List<Context> contexts = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param context the context
     */
    public ClusteredContext(String context) {
        super(context);
    }

    /**
     * Constructor.
     */
    public ClusteredContext() {
        super(null);
    }

    /**
     * Gets a list of Context's.
     * 
     * @return the list of Context's
     */
    public List<Context> getContexts() {
        return this.contexts;
    }

    /**
     * Gets the number of Context's.
     * 
     * @return the number of Context's
     */
    public int getNumberOfContexts() {
        return this.contexts.size();
    }

    /**
     * 
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);

        if (!this.contexts.isEmpty()) {
            sb.append(this.contexts.iterator().next());
            if (this.contexts.size() > 1) {
                sb.append(" in ").append(this.getNumberOfContexts()).append(" locations");
            }
        }

        return sb.toString();
    }
}
