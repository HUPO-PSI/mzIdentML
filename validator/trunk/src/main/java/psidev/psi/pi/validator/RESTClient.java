package psidev.psi.pi.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Client class for accessing a REST API.
 * @author Gerhard Mayer, MPC, Ruhr-University of Bochum
 */

public class RESTClient {

    /**
     * Constants.
     */
    private final Logger LOGGER = LogManager.getLogger(RESTClient.class);
    private final String STR_APPLICATION_HTML = "application/html";
    private final String STR_ACCEPT = "Accept";
    private final String STR_GET = "GET";
    private final int RET_OK = 200;
    
    /**
     * Members.
     */

    /**
     * Standard constructor.
     */
    public RESTClient() {
        super();
    }
    
    /**
     * Calls a REST API via GET.
     * @param urlStr the complete REST API URL string
     * @return the respone string
     */
    public String callGET(String urlStr) {
        StringBuilder strB = new StringBuilder();
        this.LOGGER.debug("urlStr: {}" + urlStr);
        
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(this.STR_GET);
            conn.setRequestProperty(this.STR_ACCEPT, this.STR_APPLICATION_HTML);

            if (conn.getResponseCode() != this.RET_OK) {
                throw new RuntimeException("Failed with HTTP error code: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String output;
            while ((output = br.readLine()) != null) {
                strB.append(output);
            }

            conn.disconnect();
        }
        catch (MalformedURLException exc) {
            exc.printStackTrace(System.err);
        }
        catch (IOException exc) {
            exc.printStackTrace(System.err);
        }
        
        System.out.println("Output from Server .... \n");
        System.out.println(strB.toString());
        
        return strB.toString();
    }
}
