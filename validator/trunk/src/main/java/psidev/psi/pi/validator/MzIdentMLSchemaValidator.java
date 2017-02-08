package psidev.psi.pi.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Florian Reisinger
 *         Date: 14-Sep-2010
 * @since $version
 */
public class MzIdentMLSchemaValidator {
    /**
     * Constants.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String DOUBLE_NEW_LINE = NEW_LINE + NEW_LINE;
    private static final String TRIPLE_NEW_LINE = DOUBLE_NEW_LINE + NEW_LINE;
    private static final String STR4_INDENTATION= "    ";
    private static final String STR_FILE_EXT_MZID   = ".mzid";
    private static final String STR_FILE_EXT_XML    = ".xml";
    private static final int EXIT_FAILURE  = -1;

    /**
     * This static object is used to create the Schema object used for validation.
     */
    private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

    /**
     * The schema to validate against.
     */
    private Schema schema = null;

    /**
     * This method carries out the work of validating the XML file passed in through
     * 'inputStream' against the compiled XML schema 'schema'. This method is a helper
     * method called by the implementation of this abstract class.
     * 
     * @param reader
     *            being a java.io.Reader from the complete XML file being validated.
     * @param schema
     *            being a compiled schema object built from the appropriate xsd
     *            (performed by the implementing sub-class of this abstract class.)
     * @return an XMLValidationErrorHandler that can be queried for details of any
     *            parsing errors to retrieve plain text or HTML
     * @throws org.xml.sax.SAXException
     */
    private MzIdentMLValidationErrorHandler validateAgainstSchema(Reader reader, Schema schema) throws SAXException {
        final MzIdentMLValidationErrorHandler mzMLValidationErrorHandler = new MzIdentMLValidationErrorHandler();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(mzMLValidationErrorHandler);
        
        try {
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
            validator.validate(new StAXSource(xmlStreamReader));
        } catch (IOException e) {
            mzMLValidationErrorHandler.fatalError(e);
        } catch (SAXParseException e) {
            mzMLValidationErrorHandler.fatalError(e);
        } catch (XMLStreamException | FactoryConfigurationError e) {
            e.printStackTrace(System.err);
        }
        
        return mzMLValidationErrorHandler;
    }

    /**
     * Sets the XML schema.
     * @param aSchemaUri    the URI (Uniform Resource Identifier) of the schema
     * @throws SAXException SAX parsing exception
     * @throws MalformedURLException malformed URl exception
     */
    public void setSchema(URI aSchemaUri) throws SAXException, MalformedURLException {
        this.schema = SCHEMA_FACTORY.newSchema(aSchemaUri.toURL());
    }

    /**
     * Gets the XML schema.
     * @return Schema
     */
    public Schema getSchema() {
        return this.schema;
    }

    /**
     * This method must be implemented to create a suitable Schema object for the .xsd file in question.
     * 
     * @param reader
     *            the XML file being validated as a Stream (Reader)
     * @return an XMLValidationErrorHandler that can be queried to return all of the
     *         error in the XML file as plain text or HTML.
     * @throws SAXException SAX parsing exception
     */
    protected MzIdentMLValidationErrorHandler validateReader(Reader reader) throws SAXException {
        if (this.schema == null) {
            throw new IllegalStateException("You need to set a schema to validate against first! use the 'setSchema(File aSchemaFile)' method for this!");
        }
        
        return this.validateAgainstSchema(reader, this.schema);
    }

    /**
     * Main program for XML schema validation.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MzIdentMLSchemaValidator validator = new MzIdentMLSchemaValidator();

        if (args == null || args.length != 2) {
            printUsage();
            System.exit(EXIT_FAILURE);
        }
        
        // Check schema file.
        File schemaFile = new File(args[0]);
        if (!schemaFile.exists()) {
            System.err.println(NEW_LINE + "Unable to find the schema file you specified: '" + args[0] + "'!" + NEW_LINE);
            System.exit(EXIT_FAILURE);
        }
        
        if (schemaFile.isDirectory()) {
            System.err.println(NEW_LINE + "The schema file you specified ('" + args[0] + "') was a folder, not a file!" + NEW_LINE);
            System.exit(EXIT_FAILURE);
        }
        
        if (!schemaFile.getName().toLowerCase().endsWith(".xsd")) {
            System.err.println("Warning: your schema file does not carry the extension '.xsd'!");
        }

        // Check input folder.
        File inputFolder = new File(args[1]);
        if (!inputFolder.exists()) {
            System.out.println(NEW_LINE + "Unable to find the input folder you specified: '" + args[1] + "'!" + NEW_LINE);
            System.exit(EXIT_FAILURE);
        }
        if (!inputFolder.isDirectory()) {
            System.out.println(NEW_LINE + "The input folder you specified ('" + args[1] + "') was a file, not a folder!" + NEW_LINE);
            System.exit(EXIT_FAILURE);
        }

        BufferedReader br = null;
        try {
            // Set the schema.
            validator.setSchema(schemaFile.toURI());
            System.out.println(NEW_LINE + "Retrieving files from '" + inputFolder.getAbsolutePath() + "'...");
            File[] inputFiles = inputFolder.listFiles((File dir, String name) -> name.toLowerCase().endsWith(STR_FILE_EXT_MZID) || name.toLowerCase().endsWith(STR_FILE_EXT_XML) /**
             * Tests, if the file has a valid extension.
             * @param dir
             * @param name
             * @return true, if it's a .mzid or .xml file
             */ );
            
            System.out.println("Found " + inputFiles.length + " input files." + NEW_LINE);
            System.out.println("Validating files...");
            for (File inputFile : inputFiles) {
                System.out.println(TRIPLE_NEW_LINE + "  - Validating file '" + inputFile.getAbsolutePath() + "'...");
                br = new BufferedReader(new FileReader(inputFile));
                MzIdentMLValidationErrorHandler xveh = validator.validateReader(br);
                if (xveh.noErrors()) {
                    System.out.println(MzIdentMLSchemaValidator.STR4_INDENTATION + "File is valid!");
                }
                else {
                    System.out.println(MzIdentMLSchemaValidator.STR4_INDENTATION + "* Errors detected: ");
                    xveh.getErrorsAsValidatorMessages().forEach((vMsg) -> {
                        System.out.println(vMsg.getMessage());
                    });
                }
                br.close();
            }
            
            System.out.println(NEW_LINE + "All done!" + NEW_LINE);
        }
        catch (SAXException | IOException e) {
            e.printStackTrace(System.err);
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (IOException ioe) {
                // Do nothing.
            }
        }
    }

    /**
     * Prints a usage message.
     */
    private static void printUsage() {
        StringBuilder out = new StringBuilder();
        out.append(DOUBLE_NEW_LINE).append("Usage: java ").append(MzIdentMLSchemaValidator.class.getName());
        out.append(" <schema_file> <inputfolder> ");
        System.out.println(out.toString());
    }
}
