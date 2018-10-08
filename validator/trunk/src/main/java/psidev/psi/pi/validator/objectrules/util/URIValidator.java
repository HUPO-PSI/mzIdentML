package psidev.psi.pi.validator.objectrules.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for validating URI's (Uniform Resource Identifier).
 * @author Gerhard Mayer, MPC, Bochum
 */
public class URIValidator {
    /**
     * Constants.
     */
    private final static Set<String> PROTOCOLS          = new HashSet<String>(Arrays.asList(new String[] {"mailto", "news", "urn"}));
    private final static Set<String> PROTOCOLS_WITH_HOST= new HashSet<String>(Arrays.asList(new String[] {"file", "ftp", "http", "https"}));
    static {
        PROTOCOLS.addAll(PROTOCOLS_WITH_HOST);
    }
    private static final char CHR_COLON = ':';
    private static final int NOT_FOUND  = -1;


    /**
     * Tests, if a string is a valid URI.
     * @param uriStr    the strring to test
     * @return true, if <code>str</code> is a valid URI; else false
     */
    public static boolean isURI(String uriStr) {
        int colonPos = uriStr.indexOf(URIValidator.CHR_COLON);
        if (colonPos < 3) {
            return false;
        }

        String proto = uriStr.substring(0, colonPos).toLowerCase();
        if (!URIValidator.PROTOCOLS.contains(proto)) {
            return false;
        }

        try {
            URI uri = new URI(uriStr);
            
            if (URIValidator.PROTOCOLS_WITH_HOST.contains(proto)) {
                if (uri.getHost() == null) {
                    return false;
                }

                String path = uri.getPath();
                if (path != null) {
                    for (int i=path.length()-1; i >= 0; i--) {
                        if ("?<>:|\"".indexOf(path.charAt(i)) > URIValidator.NOT_FOUND) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
        catch (URISyntaxException ex ) {
            ex.printStackTrace(System.err);
        }

        return false;
    }
}
