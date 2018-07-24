package psidev.psi.pi.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import psidev.psi.pi.rulefilter.RuleFilterManager;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import psidev.psi.tools.validator.rules.cvmapping.MappingRuleStatus;
import psidev.psi.tools.validator.util.ValidatorReport;

/**
 * 
 * @author Salva
 */
public class ExtendedValidatorReport extends ValidatorReport {
    
    /**
     * Constants.
     */
    private static final Logger LOG = LogManager.getLogger(ExtendedValidatorReport.class);
    private static final String NEW_LINE = System.getProperty("line.separator");
    private final HashMap<String, ObjectRule> objectRulesNotChecked = new HashMap<>();
    private final HashMap<String, ObjectRule> objectRulesValid = new HashMap<>();
    private final HashMap<String, ObjectRule> objectRulesInvalid = new HashMap<>();
    private final HashSet<String> invalidSchemaValidation = new HashSet<>();
    private final HashSet<String> notCheckedCvRules = new HashSet<>();
    private final HashSet<String> validCvRules = new HashSet<>();
    private final HashSet<String> invalidCvRules = new HashSet<>();
    private final String BLANK_HYPHEN_BLANK = " - ";
    private final String STR_DASHED_LINE    = "-----------------------------------------------------";
    
    /**
     * Constructor.
     * 
     * @param objectRules collection of object rules
     */
    public ExtendedValidatorReport(Collection<ObjectRule> objectRules) {
        super(new ArrayList<>());
        this.addObjectRules(objectRules);
    }

    /**
     * Gets the collection of not checked object rules.
     * 
     * @return collection of not checked object rules
     */
    public Collection<ObjectRule> getObjectRulesNotChecked() {
        return this.objectRulesNotChecked.values();
    }

    /**
     * Gets the collection of valid object rules.
     * 
     * @return collection of valid object rules
     */
    public Collection<ObjectRule> getObjectRulesValid() {
        return this.objectRulesValid.values();
    }

    /**
     * Gets the collection of invalid object rules.
     * 
     * @return collection of invalid object rules
     */
    public Collection<ObjectRule> getObjectRulesInvalid() {
        return this.objectRulesInvalid.values();
    }

    /**
     * Sets the Cv mapping rules.
     * 
     * @param cvRules   the CV rules
     * @param filterManager the filter manager
     * @param messages  the messages
     */
    public void setCvRules(Collection<CvRule> cvRules, RuleFilterManager filterManager, HashMap<String, List<ValidatorMessage>> messages) {
        this.clearCvMappingRules();

        for (CvRule rule : cvRules) {
            if (null != rule.getStatus()) switch (rule.getStatus()) {
                case INVALID_XPATH:
                    this.getCvRulesInvalidXpath().add(rule);
                    break;
                case NOT_CHECKED:
                    this.getCvRulesNotChecked().add(rule);
                    break;
                case VALID_RULE:
                    this.getCvRulesValid().add(rule);
                    break;
                case VALID_XPATH:
                    this.getCvRulesValidXpath().add(rule);
                    break;
                default:
                    break;
            }

            // classify on rules that results errors, valid executed rules and not executed rules
            String ruleID = rule.getId();
            if (messages.containsKey(ruleID) || rule.getStatus() == MappingRuleStatus.INVALID_XPATH) {
                this.invalidCvRules.add(ruleID);
                ExtendedValidatorReport.LOG.debug("INVALID RULE id=" + ruleID + this.BLANK_HYPHEN_BLANK + rule.getStatus());
            }
            else if (rule.getStatus() == MappingRuleStatus.NOT_CHECKED) {
                this.notCheckedCvRules.add(ruleID);
            }
            else if (ruleID.isEmpty() || ruleID.equals("unknown") || ruleID.equals("Schema Validation error")) {
                this.invalidSchemaValidation.add(ruleID);
            }
            else {
                this.validCvRules.add(ruleID);
            }
        }

        this.filterRules(filterManager);
    }

    /**
     * Filters the rules.
     * @param filterManager 
     */
    private void filterRules(RuleFilterManager filterManager) {
        if (filterManager != null) {
            if (!filterManager.getRulesToSkipList().isEmpty()) {
                
                // look in valid rules
                List<CvRule> toRemove = new ArrayList<>();
                filterManager.getRulesToSkipList().stream().map((ruleId) -> {
                    this.getCvRulesValid().stream().filter((cvRuleValid) -> (cvRuleValid.getId().equals(ruleId))).forEach((cvRuleValid) -> {
                        toRemove.add(cvRuleValid);
                    });
                    return ruleId;                    
                }).map((ruleId) -> {
                    this.validCvRules.remove(ruleId);
                    return ruleId;
                }).map((ruleId) -> {
                    this.invalidCvRules.remove(ruleId);
                    return ruleId;
                }).forEach((ruleId) -> {
                    this.notCheckedCvRules.add(ruleId);
                });
                
                toRemove.stream().map((cvRule) -> {
                    this.getCvRulesValid().remove(cvRule);
                    return cvRule;
                }).forEach((cvRule) -> {
                    this.getCvRulesNotChecked().add(cvRule);
                });
                
                // look in valid XPath rules
                toRemove.clear();
                filterManager.getRulesToSkipList().stream().forEach((ruleId) -> {
                    this.getCvRulesValidXpath().stream().filter((cvRuleValidXpath) -> (cvRuleValidXpath.getId().equals(ruleId))).forEach((cvRuleValidXpath) -> {
                        toRemove.add(cvRuleValidXpath);
                    });
                });
                
                toRemove.stream().map((cvRule) -> {
                    this.getCvRulesValidXpath().remove(cvRule);
                    return cvRule;
                }).map((cvRule) -> {
                    this.getCvRulesNotChecked().add(cvRule);
                    return cvRule;                    
                }).map((cvRule) -> {
                    this.validCvRules.remove(cvRule.getId());
                    return cvRule;
                }).map((cvRule) -> {
                    this.invalidCvRules.remove(cvRule.getId());
                    return cvRule;
                }).forEach((cvRule) -> {
                    this.notCheckedCvRules.add(cvRule.getId());
                });
            }
        }
    }
    
    /**
     * Adds the objects rules.
     * 
     * @param objectRules 
     */
    private void addObjectRules(Collection<ObjectRule> objectRules) {
        this.addAllToMap(objectRules, this.objectRulesNotChecked);
    }

    /**
     * Clears the HashMap's for the Cv mapping rules.
     */
    private void clearCvMappingRules() {
        this.getCvRulesInvalidXpath().clear();
        this.getCvRulesValid().clear();
        this.getCvRulesNotChecked().clear();
        this.getCvRulesValidXpath().clear();
    }

    /**
     * Update the lists of object rules depending on the result of the rule.
     * 
     * @param rule  the rule
     * @param resultCheck the validator message to check
     */
    public void objectRuleExecuted(ObjectRule rule, Collection<ValidatorMessage> resultCheck) {
        boolean valid;
        valid = resultCheck == null || resultCheck.isEmpty();

        // remove from the list of rules not applied
        String ruleID = rule.getId();
        this.objectRulesNotChecked.remove(ruleID);

        // if valid, add to the list of valid rules
        if (valid) {
            if (!this.objectRulesValid.containsKey(ruleID))
                this.objectRulesValid.put(ruleID, rule);
        }
        else {
            if (!this.objectRulesInvalid.containsKey(ruleID))
                this.objectRulesInvalid.put(ruleID, rule);

            this.objectRulesValid.remove(ruleID);
        }
    }

    /**
     * 
     * @param rule  the rule
     * @param resultCheck the validator message to check
     */
    public void objectRuleExecuted(ObjectRule rule, ValidatorMessage resultCheck) {
        Collection<ValidatorMessage> messages = new ArrayList<>();
        messages.add(resultCheck);
        this.objectRuleExecuted(rule, messages);
    }

    /**
     * 
     * @param ruleId the rule ID
     */
    public void setObjectRuleAsSkipped(String ruleId) {
        ObjectRule objectRule = this.getObjectRuleById(ruleId);
        if (objectRule != null) {
            String ruleID = objectRule.getId();
            
            // add to NotChecked rules
            if (this.getObjectRuleById(ruleId, this.objectRulesNotChecked) == null) {
                this.objectRulesNotChecked.put(ruleID, objectRule);
            }
            // remove from the other collections
            this.objectRulesInvalid.remove(ruleID);
            this.objectRulesValid.remove(ruleID);
        }
    }

    /**
     * Search for a rule in a collection of rules
     * 
     * @param ruleId
     * @param map
     * @return ObjectRule
     */
    private ObjectRule getObjectRuleById(String ruleId, HashMap<String, ObjectRule> map) {
        return map.get(ruleId);
    }

    /**
     * Search for an object rule in all collections in the class
     * 
     * @param ruleId    the rule ID
     * @return ObjectRule
     */
    public ObjectRule getObjectRuleById(String ruleId) {
        if (ruleId != null) {
            if (this.objectRulesInvalid.containsKey(ruleId))
                return this.objectRulesInvalid.get(ruleId);

            if (this.objectRulesNotChecked.containsKey(ruleId))
                return this.objectRulesNotChecked.get(ruleId);

            if (this.objectRulesValid.containsKey(ruleId))
                return this.objectRulesValid.get(ruleId);
        }
        
        return null;
    }

    /**
     * Prints out the Cv mapping rules.
     * 
     * @param sb
     * @param header
     * @param cvMappingRules 
     */
    private void printCvMappingRules(StringBuilder sb, String header, Collection<CvRule> cvMappingRules) {
        sb.append(header).append(" (").append(cvMappingRules.size()).append(")").append(NEW_LINE);
        sb.append(this.STR_DASHED_LINE).append(NEW_LINE);
        cvMappingRules.stream().forEach((rule) -> {
            sb.append(rule.getId()).append(this.BLANK_HYPHEN_BLANK).append(rule.getName()).append(NEW_LINE);
        });
        sb.append(NEW_LINE);
    }

    /**
     * Prints out the object rules.
     * 
     * @param sb
     * @param header
     * @param objRules 
     */
    private void printObjectRules(StringBuilder sb, String header, Collection<ObjectRule> objRules) {
        sb.append(header).append(" (").append(objRules.size()).append(")").append(NEW_LINE);
        sb.append(this.STR_DASHED_LINE).append(NEW_LINE);
        objRules.stream().forEach((rule) -> {
            sb.append(rule.getId()).append(this.BLANK_HYPHEN_BLANK).append(rule.getName()).append(NEW_LINE);
        });
        sb.append(NEW_LINE);
    }

    /**
     * Gets the report as string.
     * 
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        this.printCvMappingRules(sb, "Valid cvMapping rules", this.getCvRulesValid());
        this.printCvMappingRules(sb, "cvMapping Rules with valid Xpath that have not collected data", this.getCvRulesValidXpath());
        this.printCvMappingRules(sb, "cvMapping Rules with invalid Xpath", this.getCvRulesInvalidXpath());
        this.printCvMappingRules(sb, "cvMapping Rules that haven't been run", this.getCvRulesNotChecked());
        this.printObjectRules(sb, "Valid object rules", this.getObjectRulesValid());
        this.printObjectRules(sb, "Invalid object rules", this.getObjectRulesInvalid());
        this.printObjectRules(sb, "Object rules that haven't been run", this.getObjectRulesNotChecked());
        
        return sb.toString();
    }

    /**
     * 
     * @param ruleId the rule ID
     */
    public void setObjectRuleAsInvalid(String ruleId) {
        ObjectRule objectRule = this.getObjectRuleById(ruleId);
        if (objectRule != null) {
            String ruleID = objectRule.getId();
            
            // add to invalid rules
            if (this.getObjectRuleById(ruleId, this.objectRulesInvalid) == null) {
                this.objectRulesInvalid.put(ruleID, objectRule);
            }
            // remove from the other collections
            this.objectRulesNotChecked.remove(ruleID);
            this.objectRulesValid.remove(ruleID);
        }
    }
    
    // UTILS
    /**
     * Adds an object rule to a map.
     * 
     * @param objectRule
     * @param map 
     */
    private void addToMap(ObjectRule objectRule, HashMap<String, ObjectRule> map) {
        if (!map.containsKey(objectRule.getId()))
            map.put(objectRule.getId(), objectRule);
    }

    /**
     * Adds a Collection of object rules to a map.
     * @param objectRules
     * @param map 
     */
    private void addAllToMap(Collection<ObjectRule> objectRules, HashMap<String, ObjectRule> map) {
        objectRules.stream().forEach((objectRule) -> {
            this.addToMap(objectRule, map);
        });
    }

    /**
     * Gets the total number of Cv mapping rules.
     * 
     * @return int
     */
    public int getTotalCvRules() {
        return this.notCheckedCvRules.size() + this.validCvRules.size() + this.invalidCvRules.size();
    }

    /**
     * Gets the total number of object rules.
     * 
     * @return int
     */
    public int getTotalObjectRules() {
        return this.objectRulesInvalid.size() + this.objectRulesNotChecked.size() + this.objectRulesValid.size();
    }

    /**
     * Gets the invalid Cv rules maping rules.
     * 
     * @return hash with the invalid CV mapping rules
     */
    public HashSet<String> getInvalidCvRules() {
        return this.invalidCvRules;
    }

    /**
     * Gets the valid Cv rules maping rules.
     * 
     * @return hash with the valid CV mapping rules
     */
    public HashSet<String> getValidCvRules() {
        return this.validCvRules;
    }

    /**
     * Gets the non-checked Cv rules maping rules.
     * 
     * @return hash with the non-checked CV mapping rules
     */
    public HashSet<String> getNonCheckedCvRules() {
        return this.notCheckedCvRules;
    }
    
    /**
     * Gets the invalid XML schema validations.
     * 
     * @return HashSet of message strings
     */
    public HashSet<String> getInvalidSchemaValidation() {
        return this.invalidSchemaValidation;
    }
    
    /**
     * Adds an invalid XML schema validation message.
     * 
     * @param msg   message
     */
    public void addInvalidSchemaValidationMessage(String msg) {
        this.invalidSchemaValidation.add(msg);
    }
}
