package psidev.psi.pi.validator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import psidev.psi.pi.rulefilter.CleavageRuleCondition;
import psidev.psi.pi.rulefilter.DatabaseTypeCondition;
import psidev.psi.pi.rulefilter.MassSpectraTypeCondition;
import psidev.psi.pi.rulefilter.RuleFilterAgent;
import psidev.psi.pi.rulefilter.RuleFilterManager;
import psidev.psi.pi.validator.MzIdentMLValidator.MzIdVersion;
import psidev.psi.pi.validator.objectrules.AdditionalSearchParamsObjectRule;
import psidev.psi.pi.validator.objectrules.SearchTypeObjectRule;
import psidev.psi.pi.validator.swingworker.SwingWorker;
import psidev.psi.tools.cvrReader.CvRuleReaderException;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;

/**
 * The GUI of the MzIdentML validator software.
 * @author __USER__
 * @see "http://www.ebi.ac.uk/Tools/maven/repos/content/groups/"
 * @see "http://www.ebi.ac.uk/Tools/maven/repos/content/groups/ebi-repo/psidev/psi/tools/"
 * @see "http://www.ebi.ac.uk/Tools/maven/repos/content/groups/ebi-repo/uk/ac/ebi/jmzidml/jmzidentml/"
 */
public class MzIdentMLValidatorGUI extends javax.swing.JPanel implements RuleFilterAgent {
    /**
     * Constants.
     */
    private static final Logger LOGGER      = LogManager.getLogger(MzIdentMLValidatorGUI.class);
    private static final String NEW_LINE    = System.getProperty("line.separator");
    private static final String STR_FILE_SEPARATOR  = System.getProperty("file.separator");
    private static final String STR_RESOURCE_FOLDER = System.getProperty("user.dir") + STR_FILE_SEPARATOR + "resources" + STR_FILE_SEPARATOR;
    private static final String STR_ELLIPSIS= "...";
    private static final String DEFAULT_PROGRESS_MESSAGE    = "Select a file and press validate" + STR_ELLIPSIS;
    private static final String STR_VALIDATION_PROPERTIES   = "validation.properties";
    private static final String STR_LAF_WINDOWS    = "Windows";
    
    private final String STR_4_INDENTATION  = "    ";
    private final String STR_FLAW_IN_RULE   = "Flaw in the rule definition: ";
    private final int DEFAULT_MAX_NUMBER_TO_REPORT_SAME_MESSAGE = 1;
    private static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAILURE  = -1;
    private final ClassLoader cl;
   
    private final String STR_FILE_EXT_MZID_GZ   = ".mzid.gz";
    private final String STR_FILE_EXT_MZID_ZIP  = ".mzid.zip";
    private final String STR_FILE_EXT_MZID      = ".mzid";
    private final String STR_FILE_EXT_XML       = ".xml";
    private final String STR_FILE_EXT_7Z        = ".7z";
    public final String STR_EMPTY       = "";
    
    public final String STR_MAPPING    = "mapping";
    public final String STR_OBJECT     = "object";
    public final String COLOR_RED      = "red";
    public final String COLOR_ORANGE   = "orange";
    public final String COLOR_GREEN    = "green";
    public final String COLOR_BLACK    = "black";
    
    /**
     * Members.
     */
    private Exception errorException;
    private long runStartTime = -1L;
    private SwingWorker sw;

    protected RuleFilterManager ruleFilterManager;
    private String lastSelectedPath = this.STR_EMPTY;
    private boolean bHasCvErrors;
    public boolean bHasXLErrors;
    private boolean bHasCvWarnings;
    private boolean bHasObjErrors;
    private boolean bHasObjWarnings;
    public int cntDoubledUnanticipatedCVTermMessages = 0;
    public int cntFlawErrors = 0;
    
    /**
     * Constructor: Creates new form mzIdentMLValidatorGUI
     */
    public MzIdentMLValidatorGUI() {
        this.initComponents();
        this.enableRadioButtons(false);
        this.setSpinnerModel();
        this.cl = this.getClass().getClassLoader();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupMessageLevel = new javax.swing.ButtonGroup();
        buttonGroupPMForPFF = new javax.swing.ButtonGroup();
        buttonGroupDecoy = new javax.swing.ButtonGroup();
        buttonGroupCleavageRule = new javax.swing.ButtonGroup();
        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextInputFile = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jRadioInfoLevel = new javax.swing.JRadioButton();
        jRadioWarnLevel = new javax.swing.JRadioButton();
        jRadioErrorLevel = new javax.swing.JRadioButton();
        jRadioDebugLevel = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jRadioPMF = new javax.swing.JRadioButton();
        jRadioPFF = new javax.swing.JRadioButton();
        jRadioPMFPFF = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        jRadioDecoy = new javax.swing.JRadioButton();
        jRadioNoDecoy = new javax.swing.JRadioButton();
        jPanel10 = new javax.swing.JPanel();
        jRadioCleavageRule = new javax.swing.JRadioButton();
        jRadioNoCleavageRule = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneMessages = new javax.swing.JTextPane();
        jPanel6 = new javax.swing.JPanel();
        jProgressBar = new javax.swing.JProgressBar();
        jPanel8 = new javax.swing.JPanel();
        jComboValidationType = new javax.swing.JComboBox();
        jCheckBoxSkipSchemaValidation = new javax.swing.JCheckBox();
        jCheckBoxCheckCVRules = new javax.swing.JCheckBox();
        jCheckBoxUseRemoteOntologies = new javax.swing.JCheckBox();
        jCheckBoxShowUnanticipatedCVTerms = new javax.swing.JCheckBox();
        jCBShowFlawErrors = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jButtonValidate = new javax.swing.JButton();
        jLabelSpinner = new javax.swing.JLabel();
        jSpinner = new javax.swing.JSpinner();

        jTextField1.setText("jTextField1");

        setAutoscrolls(true);
        setMaximumSize(new java.awt.Dimension(1200, 1980));
        setMinimumSize(new java.awt.Dimension(768, 800));
        setName("MzIdentMLValidator-GUI"); // NOI18N
        setPreferredSize(new java.awt.Dimension(900, 1024));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("File selection"));

        jLabel1.setText("mzIdentML file to validate:");

        jTextInputFile.setToolTipText("Specify here the mzIdentML file to validate");

        jButtonBrowse.setText("Browse...");
        jButtonBrowse.setToolTipText("Browse for .mzid files ...");
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextInputFile, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBrowse)
                .addContainerGap(104, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jButtonBrowse)
                .addComponent(jTextInputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Select minimal message level to display"));

        buttonGroupMessageLevel.add(jRadioInfoLevel);
        jRadioInfoLevel.setText("Info");

        buttonGroupMessageLevel.add(jRadioWarnLevel);
        jRadioWarnLevel.setText("Warn");

        buttonGroupMessageLevel.add(jRadioErrorLevel);
        jRadioErrorLevel.setText("Error");

        buttonGroupMessageLevel.add(jRadioDebugLevel);
        jRadioDebugLevel.setSelected(true);
        jRadioDebugLevel.setText("Debug");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jRadioInfoLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioDebugLevel)
                .addGap(4, 4, 4)
                .addComponent(jRadioWarnLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioErrorLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioInfoLevel)
                    .addComponent(jRadioWarnLevel)
                    .addComponent(jRadioErrorLevel)
                    .addComponent(jRadioDebugLevel))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("User selection (for MIAPE validation only)"));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("PMF or PFF"));
        jPanel4.setToolTipText("State if the mass spectra data is Peptide Mass Fingerprinting, Peptide Fragment Fingerprinting or both");

        buttonGroupPMForPFF.add(jRadioPMF);
        jRadioPMF.setText("PMF");
        jRadioPMF.setToolTipText("Peptide Mass Fingerprint (MS1)");

        buttonGroupPMForPFF.add(jRadioPFF);
        jRadioPFF.setSelected(true);
        jRadioPFF.setText("PFF");
        jRadioPFF.setToolTipText("Peptide Fragment Fingerprint (MS2)");

        buttonGroupPMForPFF.add(jRadioPMFPFF);
        jRadioPMFPFF.setText("PMF+ PFF");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioPMF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioPFF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioPMFPFF)
                .addGap(40, 40, 40))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jRadioPMF)
                .addComponent(jRadioPFF)
                .addComponent(jRadioPMFPFF))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Use of a decoy database"));
        jPanel9.setToolTipText("State if a decoy database has been used or not in the database search engine");

        buttonGroupDecoy.add(jRadioDecoy);
        jRadioDecoy.setText("yes");

        buttonGroupDecoy.add(jRadioNoDecoy);
        jRadioNoDecoy.setSelected(true);
        jRadioNoDecoy.setText("no");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioDecoy)
                .addGap(18, 18, 18)
                .addComponent(jRadioNoDecoy)
                .addGap(75, 75, 75))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jRadioDecoy)
                .addComponent(jRadioNoDecoy))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Use of a specific cleavage rule"));
        jPanel10.setToolTipText("State if a specific cleavege rule has been used in the database search engine");

        buttonGroupCleavageRule.add(jRadioCleavageRule);
        jRadioCleavageRule.setText("yes");

        buttonGroupCleavageRule.add(jRadioNoCleavageRule);
        jRadioNoCleavageRule.setSelected(true);
        jRadioNoCleavageRule.setText("no");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioCleavageRule)
                .addGap(18, 18, 18)
                .addComponent(jRadioNoCleavageRule)
                .addGap(75, 75, 75))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jRadioCleavageRule)
                .addComponent(jRadioNoCleavageRule))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        jScrollPane1.setViewportView(jTextPaneMessages);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 29, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress"));

        jProgressBar.setToolTipText("");
        jProgressBar.setString("Select a file and press validate...");
        jProgressBar.setStringPainted(true);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Type of validation"));

        jComboValidationType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MIAPE-compliant validation", "Semantic validation" }));
        jComboValidationType.setSelectedIndex(1);
        jComboValidationType.setToolTipText("Select the type of validation");
        jComboValidationType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboValidationTypeActionPerformed(evt);
            }
        });

        jCheckBoxSkipSchemaValidation.setText("Skip schema validation");
        jCheckBoxSkipSchemaValidation.setToolTipText("<html>Select this option in order to skip schema validation.<br>\nThe schema validation can take a lot of time for large input files,<br> so it can be avoided by selecting this option.</html>");

        jCheckBoxCheckCVRules.setSelected(true);
        jCheckBoxCheckCVRules.setText("Check CV rules");
        jCheckBoxCheckCVRules.setToolTipText("<html>Select this option in order to check the internal consistency of CV rules.<br>\nThis is just useful if you want to check new rules edited on <b>'miape-msi-rules.1.x.0.xml'</b> or <b>'mzIdentML-mapping_1.x.0.xml'</b> files</html>");

        jCheckBoxUseRemoteOntologies.setText("Use remote (OLS) ontologies");
        jCheckBoxUseRemoteOntologies.setToolTipText("<html>\nUsing remote ontologies, the OLS service, the latest version of the<br> ontologies is being used. In the other hand, it can take more time.<br>\nIf no remote ontologies is selected, local ontology files (OBO files)<br>\nwill be used instead.</html>");

        jCheckBoxShowUnanticipatedCVTerms.setSelected(true);
        jCheckBoxShowUnanticipatedCVTerms.setText("Show unanticipated CV terms");
        jCheckBoxShowUnanticipatedCVTerms.setToolTipText("<html>\nShows CV terms, which don't occur <br> in a mapping rule of the mapping file.</html>");

        jCBShowFlawErrors.setText("Show flaw errors");
        jCBShowFlawErrors.setToolTipText("<html>Show flaw errors (i.e. check case sensitive).</html>");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBShowFlawErrors)
                    .addComponent(jCheckBoxShowUnanticipatedCVTerms)
                    .addComponent(jCheckBoxUseRemoteOntologies)
                    .addComponent(jCheckBoxCheckCVRules)
                    .addComponent(jCheckBoxSkipSchemaValidation)
                    .addComponent(jComboValidationType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jComboValidationType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxSkipSchemaValidation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxCheckCVRules)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxUseRemoteOntologies, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxShowUnanticipatedCVTerms)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBShowFlawErrors))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Action"));

        jButtonValidate.setText("Validate!");
        jButtonValidate.setToolTipText("Start the validation process");
        jButtonValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonValidateActionPerformed(evt);
            }
        });

        jLabelSpinner.setText("<html>maximum number of reported identical messages:</html>");

        jSpinner.setToolTipText("Specify the maximal number of reported identical messages");
        jSpinner.setValue(1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonValidate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jButtonValidate)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("MzIdentMLValidator-GUI");
        getAccessibleContext().setAccessibleDescription("");
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Sets the data model for the spinner.
     */
    private void setSpinnerModel() {
        SpinnerModel spModel = new SpinnerNumberModel(this.DEFAULT_MAX_NUMBER_TO_REPORT_SAME_MESSAGE, 1, 1000, 1);  // initial, min, mas, step
        this.jSpinner.setModel(spModel);
    }
    
    /**
     * 
     * @param evt 
     */
    private void jComboValidationTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.isMIAPEValidationSelected()) {
            this.enableRadioButtons(true);
        }
        else if (this.isSemanticValidationSelected()) {
            this.enableRadioButtons(false);
        }
    }

    /**
     * Enable all radio buttons.
     * @param b 
     */
    private void enableRadioButtons(boolean b) {
        this.jRadioPFF.setEnabled(b);
        this.jRadioPMF.setEnabled(b);
        this.jRadioPMFPFF.setEnabled(b);
        this.jRadioDecoy.setEnabled(b);
        this.jRadioNoDecoy.setEnabled(b);
        this.jRadioCleavageRule.setEnabled(b);
        this.jRadioNoCleavageRule.setEnabled(b);
    }

    /**
     * Disable all radio buttons.
     */
    private void disableGUI() {
        this.jTextInputFile.setEnabled(false);
        this.jButtonBrowse.setEnabled(false);
        this.jButtonValidate.setEnabled(false);
        this.jRadioInfoLevel.setEnabled(false);
        this.jRadioDebugLevel.setEnabled(false);
        this.jRadioWarnLevel.setEnabled(false);
        this.jRadioErrorLevel.setEnabled(false);
        this.jComboValidationType.setEnabled(false);
        this.jCheckBoxSkipSchemaValidation.setEnabled(false);
        this.jCheckBoxCheckCVRules.setEnabled(false);
        this.jCheckBoxUseRemoteOntologies.setEnabled(false);
        this.jCheckBoxShowUnanticipatedCVTerms.setEnabled(false);
        this.jCBShowFlawErrors.setEnabled(false);
        this.jSpinner.setEnabled(false);
        this.jLabelSpinner.setEnabled(false);
    }

    /**
     * 
     * @param evt 
     */
    private void jButtonBrowseActionPerformed(ActionEvent evt) {
        this.selectFile();
    }

    /**
     * 
     * @param evt 
     */
    private void jButtonValidateActionPerformed(ActionEvent evt) {
        // Check input file.
        String input = this.jTextInputFile.getText();
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please specify an mzIdentML file to validate!",
                "No mzIdentML file specified!",
                JOptionPane.WARNING_MESSAGE);
            this.jTextInputFile.requestFocus();
            return;
        }
        // See if the file exists.
        final File inputFile = new File(input);
        if (!inputFile.exists()) {
            JOptionPane.showMessageDialog(this,
                "The mzIdentML file you specified ('" + input + "') does not exist!",
                "Specified mzIdentML file not found!",
                JOptionPane.WARNING_MESSAGE);
            this.jTextInputFile.requestFocus();
            return;
        }
        // make sure the input is not a directory!
        if (inputFile.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                "The mzIdentML file you specified ('" + input + "') is not a file but a folder!",
                "Folder specified instead of file!",
                JOptionPane.WARNING_MESSAGE);
            this.jTextInputFile.requestFocus();
            return;
        }

        // Reset the error.
        this.errorException = null;

        // OK, we have a valid file to validate. Proceed with the work.
        this.disableGUI();
        this.enableRadioButtons(false);

        try {
            // filter rules by the options of the user
            if (this.isMIAPEValidationSelected()) {
                this.ruleFilterManager = new RuleFilterManager(this.getMIAPEValidationRuleFilterInputStream());
            }
            else {
                this.ruleFilterManager = new RuleFilterManager(this.getSemanticValidationRuleFilterInputStream());
            }
        }
        catch (JAXBException | IllegalArgumentException | FileNotFoundException e) {
            e.printStackTrace(System.err);
            // no filter rules
        }

        // Call the SwingWorker that will start the validation.
        this.sw = new SwingWorker() {
            private MzIdentMLValidator validator = null;

            @Override
            public Object construct() {
                Collection<ValidatorMessage> messages = new ArrayList<>();
                jTextPaneMessages.setCaretPosition(0);
                jTextPaneMessages.setText("");
                jProgressBar.setIndeterminate(true);
                jProgressBar.setString("Initializing validator" + STR_ELLIPSIS);
                runStartTime = System.currentTimeMillis();

                try {
                    if (this.validator == null) {
                        this.loadOntologyFiles();
                    }
                    else {
                        // reset all validator fields except the ontologies
                        validator.reset(getRuleFileInputStream(MzIdentMLValidator.currentFileVersion, STR_MAPPING),
                                        getRuleFileInputStream(MzIdentMLValidator.currentFileVersion, STR_OBJECT));
                    }

                    // this will add to the validator the rules to be skipped
                    if (ruleFilterManager != null) {
                        ruleFilterManager.filterRulesByUserOptions(getSelectedOptions());
                    }
                }
                catch (ValidatorException | CvRuleReaderException | IOException e) {
                    e.printStackTrace(System.err);
                    notifyOfError(e);
                }

                if (this.validator != null) {
                    this.resetAfterPressedValidationButton();

                    final Collection<ValidatorMessage> validationResult = this.validator.startValidation(inputFile);
                    if (validationResult != null) {
                        Collection<ValidatorMessage> clearedMsgs = this.validator.clearMultipleMessages(validationResult);
                        messages.addAll(clearedMsgs);
                    }
                }

                return messages;
            }

            /**
             * Common settings to set each time the validation button is pressed.
             */
            private void resetAfterPressedValidationButton() {
                this.validator.setValidatorGUI(MzIdentMLValidatorGUI.this);
                this.validator.setMessageReportLevel(getSelectedLevel());
                // this.validator.setSchemaUris(getSchemaUri());
                this.validator.setSkipSchemaValidation(skipXMLSchemaValidation());

                // set the rule filter manager
                this.validator.setRuleFilterManager(ruleFilterManager);

                jProgressBar.setString("Indexing mzIdentML file" + STR_ELLIPSIS);
            }
            
            /**
             * Loads the ontology files.
             */
            private void loadOntologyFiles() {
                try {
                    jProgressBar.setString("Loading configuration files" + STR_ELLIPSIS);

                    InputStream isOntology;
                    boolean bRemoteOntologies = jCheckBoxUseRemoteOntologies.isSelected();
                    if (bRemoteOntologies) {
                        isOntology = getOntologiesFileInputStream("ols.ontologies.file");
                        MzIdentMLValidatorGUI.LOGGER.debug("Remote ontology: " + isOntology);
                        if (isOntology != null) {
                            this.validator = new MzIdentMLValidator(isOntology, MzIdentMLValidatorGUI.this);
                            isOntology.close();
                        }
                        else {
                            bRemoteOntologies = false;
                        }
                    }

                    if (!bRemoteOntologies) {
                        isOntology = getOntologiesFileInputStream("local.ontologies.file");
                        MzIdentMLValidatorGUI.LOGGER.debug("Local ontology: " + isOntology);
                        if (isOntology != null) {
                            // QUICK AND DIRTY HACK
                            try {
                                this.validator = new MzIdentMLValidator(isOntology, MzIdentMLValidatorGUI.this);
                            }
                            catch (OntologyLoaderException | FileNotFoundException | ValidatorException | CvRuleReaderException exc) {
                                exc.printStackTrace(System.err);
                            }
                            isOntology.close();
                        }
                        else {
                            MzIdentMLValidatorGUI.LOGGER.error("No ontologies file for validation.");
                        }
                    }
                }
                catch (IOException | OntologyLoaderException | ValidatorException | CvRuleReaderException e) {
                    e.printStackTrace(System.err);
                    notifyOfError(e);
                }
            }

            @Override
            public void finished() {
                MzIdentMLValidatorGUI.this.validationDone(this.validator);
            }
        };
        sw.start();
    }

    /**
     * Gets the file name/path of the ontologies file.<br>
     * Note: If found in the folder where application has launched it overrides the default files.
     * 
     * @param ontologyPropertyName can take the values: ontologies.file or local.ontologies.file
     * @return InputStream for the ontologies file
     * @throws FileNotFoundException
     */
    private InputStream getOntologiesFileInputStream(String ontologyPropertyName) throws FileNotFoundException {
        String ontologiesFile = MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER + MzIdentMLValidatorGUI.getProperty(ontologyPropertyName);
        File file = new File(ontologiesFile);

        // check if the file exists. If not, return the path
        if (!file.exists()) {
            MzIdentMLValidatorGUI.LOGGER.debug("ontologiesFile does not exist: " + ontologiesFile);
            return this.cl.getResourceAsStream(ontologiesFile);
        }

        return new FileInputStream(file);
    }

    /**
     * Gets the file name/path of the rule filter file.<br>
     * Note: If found in the folder where application has launched it overrides the default files.
     * 
     * @return InputStream for the MIAPE rule filter file
     * @throws FileNotFoundException
     */
    private InputStream getMIAPEValidationRuleFilterInputStream() throws FileNotFoundException {
        String ruleFilterFileName = MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER + MzIdentMLValidatorGUI.getProperty("miape.filter.rule.file");
        File file = new File(ruleFilterFileName);

        // check if the file exists. If not, return the path
        if (!file.exists()) {
            MzIdentMLValidatorGUI.LOGGER.debug("ruleFilterFileName does not exist: " + ruleFilterFileName);
            return this.cl.getResourceAsStream(ruleFilterFileName);
        }

        return new FileInputStream(file);
    }

    /**
     * Gets the file name/path of the rule filter file.<br>
     * Note: If found in the folder were application has launched it overrides
     * the default files.
     * 
     * @return InputStream for the rule filter filer
     * @throws FileNotFoundException
     */
    private InputStream getSemanticValidationRuleFilterInputStream() throws FileNotFoundException {
        String ruleFilterFileName = MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER +  MzIdentMLValidatorGUI.getProperty("semantic.filter.rule.file");
        File file = new File(ruleFilterFileName);

        // check if the file exists. If not, return the path
        if (!file.exists()) {
            MzIdentMLValidatorGUI.LOGGER.debug("ruleFilterFileName does not exist: " + ruleFilterFileName);
            return this.cl.getResourceAsStream(ruleFilterFileName);
        }

        return new FileInputStream(file);
    }

    /**
     * Checks if the .mzid version is 1.1
     * @param mzIdVersion the {@link MzIdVersion} of the file to validate> 1.1 or 1.2.
     * @return true if .mzid version is 1.1
     */
    private boolean isVersion11(MzIdVersion mzIdVersion) {
        return MzIdVersion._1_1.equals(mzIdVersion);
    }
    
    /**
     * Checks if the .mzid version is 1.2
     * @param mzIdVersion the {@link MzIdVersion} of the file to validate> 1.1 or 1.2.
     * @return true if .mzid version is 1.2
     */
    private boolean isVersion12(MzIdVersion mzIdVersion) {
        return MzIdVersion._1_2.equals(mzIdVersion);
    }

    /**
     * Gets the file name/path of a mapping or object rule file.<br>
     * Note: If found in the folder were application has launched it overrides the default files.
     * 
     * @param mzIdVersion the {@link MzIdVersion} of the file to validate: 1.1 or 1.2.
     * @param ruleKind "mapping" or "object"
     * @return InputStream for the rule file
     * @throws FileNotFoundException file not found exception
     */
    public InputStream getRuleFileInputStream(MzIdVersion mzIdVersion, String ruleKind) throws FileNotFoundException {
        String propertyName = ruleKind;
        String ruleFile = this.STR_EMPTY;
        
        if (this.isMIAPEValidationSelected()) {
            propertyName += ".rule.file.miape.validation.";
        }
        else if (this.isSemanticValidationSelected()) {
            propertyName += ".rule.file.semantic.validation.";
        }
        
        if (this.isVersion11(mzIdVersion)) {
            propertyName += "1.1.0";
            ruleFile = MzIdentMLValidatorGUI.getProperty(propertyName);
            MzIdentMLValidatorGUI.LOGGER.debug(".mzid version: 1.1.0");
        }
        else if (this.isVersion12(mzIdVersion)) {
            propertyName += "1.2.0";
            ruleFile = MzIdentMLValidatorGUI.getProperty(propertyName);
            MzIdentMLValidatorGUI.LOGGER.debug(".mzid version: 1.2.0");
        }
        else {
            // set default
            MzIdentMLValidatorGUI.LOGGER.error("Usupported .mzid version: " + mzIdVersion);
            switch (ruleKind) {
                case STR_MAPPING:
                    ruleFile = "mzIdentML-mapping_1.2.0.xml";
                    break;
                case STR_OBJECT:
                    ruleFile = "ObjectRules.1.2.0.xml";
                    break;
                default:
                    MzIdentMLValidatorGUI.LOGGER.error("Unsupported ruleKind: " + ruleKind);
            }
        }

        try {
            URL url = new URL(ruleFile);
            return url.openStream();
        }
        catch (IOException e) {
            File file = new File(MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER + ruleFile);
            if (!file.exists()) {
                MzIdentMLValidatorGUI.LOGGER.debug(ruleKind + "RuleFile does not exist: " + ruleFile);
                return this.cl.getResourceAsStream(ruleFile);
            }
            return new FileInputStream(file);
        }
    }
    
    /**
     * Checks if semantic validation was selected.
     * @return flag indicating if a semantic validation was selected by the user
     */
    public boolean isSemanticValidationSelected() {
        return this.jComboValidationType.getSelectedItem().equals(ValidationType.SEMANTIC_VALIDATION.getName());
    }

    /**
     * Checks if MIAPE validation was selected.
     * @return  flag indicating if a MIAPE validation was selected by the user
     */
    public boolean isMIAPEValidationSelected() {
        return this.jComboValidationType.getSelectedItem().equals(ValidationType.MIAPE_VALIDATION.getName());
    }

    /**
     * Reads a property from the .properties file.
     * @param propertyName the property name
     * @return The property value
     */
    public static String getProperty(String propertyName) {
        // TODO: What is wrong here?
        /*
        InputStream is = MzIdentMLValidatorGUI.class.getClassLoader().getResourceAsStream(MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER + MzIdentMLValidatorGUI.STR_VALIDATION_PROPERTIES);
        if (is == null) {
            MzIdentMLValidatorGUI.LOGGER.error(MzIdentMLValidatorGUI.STR_VALIDATION_PROPERTIES + " file not found");
            System.exit(EXIT_FAILURE);
        }

        Properties properties = new Properties();
        try {
            properties.load(is);
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IllegalArgumentException(e);
        }
        */

        
        PropertyFile propFile = new PropertyFile();
        MzIdentMLValidatorGUI.LOGGER.debug("Resources: " + MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER);
        Properties properties = propFile.loadProperties(MzIdentMLValidatorGUI.STR_RESOURCE_FOLDER + MzIdentMLValidatorGUI.STR_VALIDATION_PROPERTIES);
        
        
        return properties.getProperty(propertyName);
    }

    /**
     * Does the finalization work after validataion.
     * @param validator
     */
    private void validationDone(MzIdentMLValidator validator) {
        this.jProgressBar.setValue(this.jProgressBar.getMaximum());
        if (this.errorException != null) {
            String message = this.errorException.getMessage();
            message = StringEscapeUtils.escapeHtml4(message);
            String skipSchemaValidation = "Try to run again not skipping the schema validation";
            if (!this.skipXMLSchemaValidation()) {
                skipSchemaValidation = this.STR_EMPTY;
            }

            JOptionPane.showMessageDialog(this,
                "<html>A problem occurred when attempting to validate the mzIdentML file!<br>" + message + "<br>" + skipSchemaValidation,
                "Error occurred during validation!</html>", JOptionPane.ERROR_MESSAGE);
        }
        
        this.jProgressBar.setIndeterminate(false);
        this.jProgressBar.setValue(0);
        // Calculate last run time.
        long delta = System.currentTimeMillis() - this.runStartTime;
        // Reset run start time and moment.
        this.runStartTime = -1L;
        this.jProgressBar.setString(MzIdentMLValidatorGUI.DEFAULT_PROGRESS_MESSAGE + " (last run took " + (delta / 1000) + " seconds)");
        
        // Re-enable GUI.
        this.reEnableGUI();

        // not enable the checkbox for changing the ontologies source since it cannot be changed in the same program execution
        // jCheckBoxUseRemoteOntologies.setEnabled(true);

        // show results
        if (validator != null) {
            int cntMessages = this.showMessages(true, validator);
            String reportString = validator.getHtmlStatisticsReport(cntMessages);
            if (reportString != null) 
                JOptionPane.showMessageDialog(this, new String[] { this.STR_EMPTY, reportString }, "Rule Execution Report", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Re-Enable the GUI.
     */
    private void reEnableGUI() {
        this.jTextInputFile.setEnabled(true);
        this.jButtonBrowse.setEnabled(true);
        this.jButtonValidate.setEnabled(true);
        this.jRadioInfoLevel.setEnabled(true);
        this.jRadioDebugLevel.setEnabled(true);
        this.jRadioWarnLevel.setEnabled(true);
        this.jRadioErrorLevel.setEnabled(true);
        this.jComboValidationType.setEnabled(true);
        this.jCheckBoxSkipSchemaValidation.setEnabled(true);
        this.jComboValidationTypeActionPerformed(null);
        this.jCheckBoxCheckCVRules.setEnabled(true);
        this.jCheckBoxShowUnanticipatedCVTerms.setEnabled(true);
        this.jCBShowFlawErrors.setEnabled(true);
        this.jCheckBoxUseRemoteOntologies.setEnabled(true);
        this.jSpinner.setEnabled(true);
        this.jSpinner.setValue(this.DEFAULT_MAX_NUMBER_TO_REPORT_SAME_MESSAGE);
        this.jLabelSpinner.setEnabled(true);
    }

    /**
     * Filters out not relevant special processing related messages.
     * @param msgs      the messages
     * @param report    the report
     * @return Collection<>
     */
    private Collection<ValidatorMessage> filterOutSpecialProcessingMessages(Collection<ValidatorMessage> msgs, ExtendedValidatorReport report) {
        Collection<ValidatorMessage> filteredMsgs = new ArrayList<>();
        ArrayList<String> messageList = new ArrayList<>();
        
        String ruleID, message;
        for (ValidatorMessage msg: msgs) {
            message = msg.getMessage();
            
            boolean bAdd = true;
            // filter out flaw error messages
            if (!this.jCBShowFlawErrors.isSelected()) {
                if (msg.getContext() != null) {
                    if (msg.getContext().toString().contains(this.STR_FLAW_IN_RULE)) {
                        bAdd = false;
                        this.cntFlawErrors++;
                    }
                }
            } // flaw errors
            
            if (!messageList.contains(message)) {
                messageList.add(message);

                if (msg.getRule() != null) {
                    ruleID = msg.getRule().getId();
                }
                else {
                    ruleID = "-";
                }

                // positive testing
                if (SearchTypeObjectRule.bIsDeNovoSearch || SearchTypeObjectRule.bIsSpectralLibrarySearch) { 
                    switch (ruleID) {
                        case "DenovoSearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "SpectrumIdentificationItemPeptideEvidenceRefObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                
                // negative testing
                if (!SearchTypeObjectRule.bIsDeNovoSearch) { 
                    switch (ruleID) {
                        case "DenovoSearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "SpectrumIdentificationItemNullPeptideEvidenceRefObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                if (!SearchTypeObjectRule.bIsSpectralLibrarySearch) { 
                    switch (ruleID) {
                        case "SpectralLibrarySearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "SpectrumIdentificationItemNullPeptideEvidenceRefObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                if (!AdditionalSearchParamsObjectRule.bIsPeptideLevelScoring) {
                    switch (ruleID) {
                        case "PeptideLevelStatsSpectrumIdentificationItem_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "PeptideLevelStatsSearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "PeptideLevelStatsObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                if (!AdditionalSearchParamsObjectRule.bIsModificationLocalizationScoring) {
                    switch (ruleID) {
                        case "ModLocalizationSearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "ModLocalizationSpectrumIdentificationItem_must_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "SIIModLocalizationScoringRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                if (!AdditionalSearchParamsObjectRule.bIsCrossLinkingSearch) {
                    switch (ruleID) {
                        case "CrosslinkingSearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "CrosslinkingPeptideModification_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "XLinkPeptideModificationObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                        case "XLinkSIIObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                if (!AdditionalSearchParamsObjectRule.bIsProteoGenomicsSearch) {
                    switch (ruleID) {
                        case "ProteogenomicsSearchType_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "ProteogenomicsDBSequence_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "ProteogenomicsDBSequence_unmapped_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "ProteogenomicsPeptideEvidence_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "ProteogenomicsPeptideEvidence_unmapped_may_rule":
                            report.getInvalidCvRules().remove(ruleID);
                            bAdd = false;
                            break;
                        case "ProteoGenomicsPeptEvObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                        case "ProteoGenomicsDBSeqObjectRule":
                            report.getObjectRulesInvalid().remove(report.getObjectRuleById(ruleID));
                            bAdd = false;
                            break;
                    }
                }
                /*
                if (!AdditionalSearchParamsObjectRule.bIsSamplePreFractionation) {
                    // TODO: implement
                }
                if (!AdditionalSearchParamsObjectRule.bIsConsensusScoring) {
                    // TODO: implement
                }
                */

                // Quick and dirty hack for ProteinDetectionList_must_rule
                // MS:1002404 (count of identified proteins), see GitHub Issue #94
                /*
                if (MzIdentMLValidator.currentFileVersion == MzIdentMLValidator.MzIdVersion._1_2) {
                    MzIdentMLValidatorGUI.LOGGER.debug("ruleID: " + ruleID);
                    if (ProteinDetectionListObjectRule.bContainsCountsOfIdentifiedProteins) {
                        switch (ruleID) {
                            case "ProteinDetectionList_must_rule":
                                report.getInvalidCvRules().remove(ruleID);
                                bAdd = false;
                                break;
                        }
                    }
                }
                */

                if (bAdd) {
                    filteredMsgs.add(msg);
                }
            }
            else {
                this.cntDoubledUnanticipatedCVTermMessages++;
            }
        } // rof
        
        return filteredMsgs;
    }

    /**
     * Shows the messages.
     * @param showStatistics 
     * @param validator 
     * @return int
     */
    private int showMessages(boolean showStatistics, MzIdentMLValidator validator) {
        @SuppressWarnings("unchecked")
        Collection<ValidatorMessage> msgs = (Collection<ValidatorMessage>) this.sw.get();
        Collection<ValidatorMessage> messages = this.filterOutSpecialProcessingMessages(msgs, validator.getExtendedReport());

        Color col_BLACK = new Color(0, 0, 0);
        Color col_RED   = new Color(255, 0, 0);
        Color col_ORANGE= new Color(255, 128, 64);
        Color col_GREEN = new Color(0, 160, 0);
        this.bHasCvErrors = false;
        this.bHasCvWarnings = false;
        this.bHasObjErrors = false;
        this.bHasObjWarnings = false;
        
        if (messages != null && messages.size() > 0) {
            this.appendMsgToTextPane(this.jTextPaneMessages, "The following messages were obtained during the validation of your XML file:" + NEW_LINE, col_BLACK);
            
            MutableInt count = new MutableInt(0);
            this.appendMsgToTextPane(this.jTextPaneMessages, this.getRedSortedValidatorMessages(messages, count), col_RED);
            this.appendMsgToTextPane(this.jTextPaneMessages, this.getOrangeSortedValidatorMessages(messages, count), col_ORANGE);
            this.appendMsgToTextPane(this.jTextPaneMessages, this.getGreenSortedValidatorMessages(messages, count), col_GREEN);
            this.appendMsgToTextPane(this.jTextPaneMessages, this.getBlackSortedValidatorMessages(messages, count), col_BLACK);
            this.jTextPaneMessages.setCaretPosition(0);
            this.jTextPaneMessages.setLocation(0,0 );
        }
        else {
            this.appendMsgToTextPane(this.jTextPaneMessages, NEW_LINE + NEW_LINE + "No messages were returned by the validator.", col_BLACK);
            if (this.errorException == null) {
                JOptionPane.showMessageDialog(MzIdentMLValidatorGUI.this,
                    "Your mzIdentML file validated at the current message level.",
                    "No messages produced.", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (showStatistics) {
            if (messages != null) {
                final String statisticsReport = validator.getStatisticsReport(messages.size());
                if (statisticsReport != null) {
                    this.appendMsgToTextPane(this.jTextPaneMessages, NEW_LINE, col_BLACK);
                    this.appendMsgToTextPane(this.jTextPaneMessages, NEW_LINE, col_BLACK);
                    this.appendMsgToTextPane(this.jTextPaneMessages, "======== RULE STATISTICS ========", col_BLACK);
                    this.appendMsgToTextPane(this.jTextPaneMessages, NEW_LINE, col_BLACK);
                    this.appendMsgToTextPane(this.jTextPaneMessages, statisticsReport, col_BLACK);
                    this.appendMsgToTextPane(this.jTextPaneMessages, validator.getCvContextReport(), col_BLACK);
                }
            }
        }
        
        if (messages != null) {
            int noOfOutFilteredMsgs = msgs.size() - messages.size();
            return messages.size() - noOfOutFilteredMsgs;
        }
        else {
            return 0;
        }
    }
    
    /**
     * Appends a coloured message text to the TextPane.
     * @param txtPane
     * @param msg
     * @param color 
     */
    private void appendMsgToTextPane(JTextPane txtPane, String msg, Color color)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        
        AttributeSet attrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        attrSet = sc.addAttribute(attrSet, StyleConstants.FontFamily, "Lucida Console");
        attrSet = sc.addAttribute(attrSet, StyleConstants.FontSize, 11);
        attrSet = sc.addAttribute(attrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        txtPane.setCaretPosition(txtPane.getDocument().getLength());
        txtPane.setCharacterAttributes(attrSet, false);
        txtPane.replaceSelection(msg);
    }
    
    /**
     * Gets a string containing all validator messages sorted according to the MessageLevel.
     * @param aMessages
     * @param count
     * @return String of validator messages
     */
    private String getRedSortedValidatorMessages(Collection<ValidatorMessage> aMessages, MutableInt count) {
        StringBuilder strB = new StringBuilder();
        
        strB.append(this.getValidatorMessages(aMessages, MessageLevel.FATAL, count));
        strB.append(this.getValidatorMessages(aMessages, MessageLevel.ERROR, count));
        
        return strB.toString();
    }
    
    /**
     * Gets a string containing all validator messages sorted according to the MessageLevel.
     * @param aMessages
     * @param count
     * @return String of validator messages
     */
    private String getOrangeSortedValidatorMessages(Collection<ValidatorMessage> aMessages, MutableInt count) {
        StringBuilder strB = new StringBuilder();
        
        strB.append(this.getValidatorMessages(aMessages, MessageLevel.WARN, count));
        
        return strB.toString();
    }
    
    /**
     * Gets a string containing all validator messages sorted according to the MessageLevel.
     * @param aMessages
     * @param count
     * @return String of validator messages
     */
    private String getGreenSortedValidatorMessages(Collection<ValidatorMessage> aMessages, MutableInt count) {
        StringBuilder strB = new StringBuilder();
        
        strB.append(this.getValidatorMessages(aMessages, MessageLevel.INFO, count));
        strB.append(this.getValidatorMessages(aMessages, MessageLevel.DEBUG, count));
        
        return strB.toString();
    }

    /**
     * Gets a string containing all validator messages sorted according to the MessageLevel.
     * @param aMessages
     * @param count
     * @return String of validator messages
     */
    private String getBlackSortedValidatorMessages(Collection<ValidatorMessage> aMessages, MutableInt count) {
        StringBuilder strB = new StringBuilder();
        
        strB.append(this.getValidatorMessages(aMessages, MessageLevel.SUCCESS, count));
        
        return strB.toString();
    }

    /**
     * Gets a string containing all validator messages for a given MessageLevel.
     * @param aMessages
     * @param specLevel
     * @param count
     * @return String of validator messages
     */
    private String getValidatorMessages(Collection<ValidatorMessage> aMessages, MessageLevel specLevel, MutableInt count) {
        StringBuilder sb = new StringBuilder();
        
        MessageLevel selectedMsgLevel = this.getSelectedLevel();
        aMessages.stream().filter((message) -> (message.getLevel().isSame(specLevel) && (message.getLevel().isSame(selectedMsgLevel) || message.getLevel().isHigher(selectedMsgLevel)))).forEach((ValidatorMessage message) -> {
            count.increment();
            sb.append(NEW_LINE).append(NEW_LINE).append("Message ").append(count.intValue()).append(":").append(NEW_LINE);
            final Rule rule = message.getRule();
            MzIdentMLValidatorGUI.this.setFlagsFromRule(rule, message.getLevel());
            if (rule != null) {
                sb.append(MzIdentMLValidatorGUI.this.STR_4_INDENTATION).append("Rule ID: ").append(rule.getId()).append(NEW_LINE);
            }
            sb.append(MzIdentMLValidatorGUI.this.STR_4_INDENTATION).append("Level: ").append(message.getLevel()).append(NEW_LINE);
            if (message.getContext() != null) {
                sb.append(MzIdentMLValidatorGUI.this.STR_4_INDENTATION).append(message.getContext()).append(NEW_LINE);
            }
            sb.append(MzIdentMLValidatorGUI.this.STR_4_INDENTATION).append("--> ").append(message.getMessage()).append(NEW_LINE);
            if (rule != null && rule.getHowToFixTips() != null) {
                rule.getHowToFixTips().stream().forEach((howToFixTip) -> {
                    sb.append(MzIdentMLValidatorGUI.this.STR_4_INDENTATION).append("Tip: ").append(howToFixTip).append(NEW_LINE);
                });
            }
        }); // only get messages that have the same message level as selected by the user

        return sb.toString();
    }

    /**
     * Set the color flags from the rule.
     * @param rule 
     * @param msgLevel 
     */
    private void setFlagsFromRule(Rule rule, MessageLevel msgLevel) {
        if (rule instanceof CvRule) {
            if (msgLevel.isSame(MessageLevel.FATAL) || msgLevel.isSame(MessageLevel.ERROR)) {
                this.bHasCvErrors = true;
            }
            else if (msgLevel.isSame(MessageLevel.WARN)) {
                this.bHasCvWarnings = true;
            }
        }
        else if (rule instanceof ObjectRule) {
            if (msgLevel.isSame(MessageLevel.FATAL) || msgLevel.isSame(MessageLevel.ERROR)) {
                this.bHasObjErrors = true;
            }
            else if (msgLevel.isSame(MessageLevel.WARN)) {
                this.bHasObjWarnings = true;
            }
        }
    }
    
    /**
     * Gets the overall color for unanticipated Cv terms.
     * @param noOfUnanticipatedCVs number of unanticipated CV terms
     * @return String the color to use for the unanticipated CV message.
     */
    public String getUnanticipatedCVColor(int noOfUnanticipatedCVs) {
        if (noOfUnanticipatedCVs > 0) {
            return this.COLOR_ORANGE;
        }
        
        return this.COLOR_GREEN;
    }
    
    /**
     * Gets the overall color for cross-linking interaction score messages.
     * @param noOfXLInteractionScoreMsgs number ofcross-linking interaction score messages
     * @return String the color to use for the cross-linking interaction score messages.
     */
    public String getXLInteractionScoreColor(int noOfXLInteractionScoreMsgs) {
        if (noOfXLInteractionScoreMsgs > 0) {
            return this.COLOR_RED;
        }
        
        return this.COLOR_GREEN;
    }
    
    /**
     * Gets the overall color for the invalid messages.
     * @return String the color to use for the invalid messages.
     */
    public String getInvalidMsgColor() {
        if (this.bHasCvErrors || this.bHasObjErrors || this.bHasXLErrors) {
            return this.COLOR_RED;
        }
        else if (this.bHasCvWarnings || this.bHasObjWarnings) {
            return this.COLOR_ORANGE;
        }
        else {
            return this.COLOR_GREEN;
        }
    }
    
    /**
     * Gets the color for the invalid CV mapping Rule messages.
     * @return String the color to use for the invalid CV mapping Rule messages.
     */
    public String getInvalidCvMappingColor() {
        if (this.bHasCvErrors) {
            return this.COLOR_RED;
        }
        else if (this.bHasCvWarnings) {
            return this.COLOR_ORANGE;
        }
        else {
            return this.COLOR_GREEN;
        }
    }
    
    /**
     * Gets the color for the invalid Object Rule messages.
     * @return String the color to use for the invalid Object Rule messages.
     */
    public String getInvalidObjectRuleColor() {
        if (this.bHasObjErrors) {
            return this.COLOR_RED;
        }
        else if (this.bHasObjWarnings) {
            return this.COLOR_ORANGE;
        }
        else {
            return this.COLOR_GREEN;
        }
    }
    
    /**
     * Select the file to validate.
     */
    private void selectFile() {
        JFileChooser jfc = this.getFileChooser("Select mzIdentML (.mzid / .mzid.gz) file to validate");

        int returnVal = jfc.showOpenDialog(this.jTextInputFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.lastSelectedPath = jfc.getSelectedFile().getPath();
            this.jTextInputFile.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
        }
    }

    /**
     * Open the FileChooser on the root or the folder the user already specified (if it exists).
     * 
     * @return the File object
     */
    private File getStartFile() {
        File startHere;
        
        if (!this.lastSelectedPath.isEmpty()) {
            startHere = new File(this.lastSelectedPath);
        }
        else {
            startHere = new File("/");
        }
        
        if (!this.jTextInputFile.getText().trim().isEmpty()) {
            File f = new File(this.jTextInputFile.getText().trim());
            if (f.exists()) {
                startHere = f;
            }
        }

        return startHere;
    }
    
    /**
     * Get the FileChooser.
     * @param title
     * @return the JFileChooser object
     */
    private JFileChooser getFileChooser(String title) {
        JFileChooser jfc = new JFileChooser(this.getStartFile());
        
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setDialogTitle(title);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory()
                        || f.getName().toLowerCase().endsWith(STR_FILE_EXT_MZID)
                        || f.getName().toLowerCase().endsWith(STR_FILE_EXT_MZID_GZ)
                        || f.getName().toLowerCase().endsWith(STR_FILE_EXT_MZID_ZIP)
                        || f.getName().toLowerCase().endsWith(STR_FILE_EXT_7Z)
                        || f.getName().toLowerCase().endsWith(STR_FILE_EXT_XML)) {
                    result = true;
                }
                return result;
            }

            @Override
            public String getDescription() {
                return "mzIdentML files";
            }
        });
        
        return jfc;
    }
    
    /**
     * Gets the user-selected message level.
     * @return the selected MessageLevel
     */
    private MessageLevel getSelectedLevel() {
        MessageLevel result = null;

        if (this.jRadioInfoLevel.isSelected()) {
            result = MessageLevel.INFO;
        }
        else if (this.jRadioDebugLevel.isSelected()) {
            result = MessageLevel.DEBUG;
        }
        else if (this.jRadioWarnLevel.isSelected()) {
            result = MessageLevel.WARN;
        }
        else if (this.jRadioErrorLevel.isSelected()) {
            result = MessageLevel.ERROR;
        }

        return result;
    }

    /**
     * 
     * @return flag indicating, if XML schema validation should be skipped
     */
    private boolean skipXMLSchemaValidation() {
        return this.jCheckBoxSkipSchemaValidation.isSelected();
    }

    /**
     * 
     * @return  flag indicating if CvRule checking was selected by the user
     */
    public boolean skipCvRulesChecking() {
        return !this.jCheckBoxCheckCVRules.isSelected();
    }

    /**
     * 
     * @param e exception
     */
    public void notifyOfError(Exception e) {
        this.errorException = e;
    }

    /**
     * Initializes the progress bar.
     * @param min   minimum value
     * @param max   maximum value
     * @param current   the actual value
     */
    public void initProgress(int min, int max, int current) {
        this.jProgressBar.setIndeterminate(false);
        this.jProgressBar.setMinimum(min);
        this.jProgressBar.setMaximum(max);
        this.jProgressBar.setValue(current);
    }
    
    /**
     * Sets the progress bar.
     * @param value value to set
     * @param message message
     */
    public void setProgress(int value, String message) {
        if (value > this.jProgressBar.getMaximum()) {
            value = this.jProgressBar.getMaximum();
            MzIdentMLValidatorGUI.LOGGER.error("ProgressValue is too big: " + value);
        }
        this.jProgressBar.setValue(value);
        this.jProgressBar.setString(message);
    }

    /**
     * Main program for GUI execution.
     * .
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            if (!SystemUtils.IS_OS_WINDOWS) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            else {
                UIManager.createLookAndFeel(MzIdentMLValidatorGUI.STR_LAF_WINDOWS);                
            }
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException exc) {
            exc.printStackTrace(System.err);
            System.out.println("No Windows LookAndFeel found.");
        }
        MzIdentMLValidatorGUI jPanelValidator = new MzIdentMLValidatorGUI();

        if (args != null && args.length == 1) {
            jPanelValidator.jTextInputFile.setText(args[0]);
        }

        JFrame validatorFrame = new JFrame("mzIdentML validator GUI (mzIdentML versions 1.1.1 & 1.2.0)");
        validatorFrame.getContentPane().add(jPanelValidator, BorderLayout.CENTER);
        validatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validatorFrame.setResizable(false);
        validatorFrame.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed. The
             * close operation can be overridden at this point.
             */
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                e.getWindow().dispose();
                System.exit(EXIT_SUCCESS);
            }
        });
        validatorFrame.setLocation(100, 100);
        validatorFrame.pack();
        validatorFrame.setVisible(true);
    }

    /**
     * Gets the options selected by the user.
     * @return mapping the ID#s to the options
     */
    @Override
    public HashMap<String, String> getSelectedOptions() {
        HashMap<String, String> conditionSet = new HashMap<>();
        
        // MASS SPECTRA TYPE
        if (this.jRadioPFF.isSelected()) {
            conditionSet.put(MassSpectraTypeCondition.getID(), MassSpectraTypeCondition.PFF.getOption());
        }
        else if (this.jRadioPMF.isSelected()) {
            conditionSet.put(MassSpectraTypeCondition.getID(), MassSpectraTypeCondition.PMF.getOption());
        }
        else if (this.jRadioPMFPFF.isSelected()) {
            conditionSet.put(MassSpectraTypeCondition.getID(), MassSpectraTypeCondition.PMFPFF.getOption());
        }
        
        // USER SPECIFIC CLEAVAGE RULE
        if (this.jRadioCleavageRule.isSelected()) {
            conditionSet.put(CleavageRuleCondition.getID(), CleavageRuleCondition.USER_SPECIFIC_CLEAVAGE_RULE.getOption());
        }
        else if (this.jRadioNoCleavageRule.isSelected()) {
            conditionSet.put(CleavageRuleCondition.getID(), CleavageRuleCondition.NO_USER_SPECIFIC_CLEAVAGE_RULE.getOption());
        }
        
        // DECOY DATABASE
        if (this.jRadioDecoy.isSelected()) {
            conditionSet.put(DatabaseTypeCondition.getID(), DatabaseTypeCondition.DECOY_DATABASE.getOption());
        }
        else if (this.jRadioNoDecoy.isSelected()) {
            conditionSet.put(DatabaseTypeCondition.getID(), DatabaseTypeCondition.NO_DECOY_DATABASE.getOption());
        }
        
        return conditionSet;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupCleavageRule;
    private javax.swing.ButtonGroup buttonGroupDecoy;
    private javax.swing.ButtonGroup buttonGroupMessageLevel;
    private javax.swing.ButtonGroup buttonGroupPMForPFF;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonValidate;
    private javax.swing.JCheckBox jCBShowFlawErrors;
    private javax.swing.JCheckBox jCheckBoxCheckCVRules;
    public javax.swing.JCheckBox jCheckBoxShowUnanticipatedCVTerms;
    private javax.swing.JCheckBox jCheckBoxSkipSchemaValidation;
    private javax.swing.JCheckBox jCheckBoxUseRemoteOntologies;
    private javax.swing.JComboBox jComboValidationType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelSpinner;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JRadioButton jRadioCleavageRule;
    private javax.swing.JRadioButton jRadioDebugLevel;
    private javax.swing.JRadioButton jRadioDecoy;
    private javax.swing.JRadioButton jRadioErrorLevel;
    private javax.swing.JRadioButton jRadioInfoLevel;
    private javax.swing.JRadioButton jRadioNoCleavageRule;
    private javax.swing.JRadioButton jRadioNoDecoy;
    private javax.swing.JRadioButton jRadioPFF;
    private javax.swing.JRadioButton jRadioPMF;
    private javax.swing.JRadioButton jRadioPMFPFF;
    private javax.swing.JRadioButton jRadioWarnLevel;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JSpinner jSpinner;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextInputFile;
    private javax.swing.JTextPane jTextPaneMessages;
    // End of variables declaration//GEN-END:variables
}