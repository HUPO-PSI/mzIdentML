package psidev.psi.pi.rulefilter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import psidev.psi.pi.rulefilter.jaxb.RuleFilter;
import psidev.psi.pi.rulefilter.jaxb.RuleToSkip;
import psidev.psi.pi.rulefilter.jaxb.UserCondition;
import psidev.psi.pi.validator.ExtendedValidatorReport;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;

/**
 * This class provides the methods to maintain a list of object rules and a list
 * of cv mapping rules to skip. Its behaviour is configured by a configuration xml file (following the schema:
 * @see "http://proteo.cnb.csic.es/miape-api/schemas/ruleFilter_v1.4.xsd"
 * 
 * @author Salva
 * 
 */
public class RuleFilterManager {
    /**
     * Constants.
     */
    private static final String NEW_LINE = System.getProperty("line.separator");
    private final String TAB = "\t";
    private final String DOUBLE_TAB = TAB + TAB;
    private final JAXBContext jc;
    
    /**
     * Members.
     */
    private RuleFilter filter = null;
    private List<String> rulesToSkip = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param xmlFile the XML file
     * @throws JAXBException  JAXB exception
     */
    public RuleFilterManager(InputStream xmlFile) throws JAXBException {
        if (xmlFile == null) {
            throw new IllegalArgumentException("Provide a no null file!");
        }
        
        this.jc = JAXBContext.newInstance("psidev.psi.pi.rulefilter.jaxb");
        this.filter = (RuleFilter) this.jc.createUnmarshaller().unmarshal(xmlFile);
    }

    /**
     * Sets the RuleFilter.
     * 
     * @param filter the rule filter
     */
    public void setFilter(RuleFilter filter) {
        this.filter = filter;
    }

    /**
     * Gets the RuleFilter.
     * 
     * @return RuleFilter
     */
    public RuleFilter getFilter() {
        return this.filter;
    }

    /**
     * Gets a list of mandatory elements
     * 
     * @return a list of mandatory elements
     */
    public List<String> getMandatoryElements() {
        List<String> ret = new ArrayList<>();
        
        if (this.filter.getMandatoryElements() != null) {
            this.filter.getMandatoryElements().getMandatoryElement().stream().map((mandatoryElement) -> mandatoryElement.getElement()).filter((mzIdentMLElement) -> (!mzIdentMLElement.isEmpty())).forEach((mzIdentMLElement) -> {
                ret.add(mzIdentMLElement);
            });
        }
        
        return ret;
    }

    /**
     * Gets the rules to skip.
     * 
     * @param userCondition
     * @param optionId
     * @return List<>
     */
    private List<RuleToSkip> getRulesToSkip(UserCondition userCondition, String optionId) {
        List<RuleToSkip> locRulesToSkip = new ArrayList<>();
        
        if (optionId != null) {
            userCondition.getUserOption().stream().filter((option) -> (option.getId().equals(optionId))).map((option) -> {
                if (option.getRuleToSkip() != null) {
                    locRulesToSkip.addAll(option.getRuleToSkip());
                }
                return option;
            }).filter((option) -> (option.getRulesToSkipRef() != null)).forEach((option) -> {
                option.getRulesToSkipRef().stream().filter((rulesToSkipRef) -> (this.filter.getReferences() != null && this.filter.getReferences().getReferencedRules() != null)).forEach((rulesToSkipRef) -> {
                    this.filter.getReferences().getReferencedRules().stream().filter((referencedRuleSet) -> (referencedRuleSet.getId().equals(rulesToSkipRef.getRef()))).forEach((referencedRuleSet) -> {
                        locRulesToSkip.addAll(referencedRuleSet.getRuleToSkip());
                    });
                });
            });
        }

        return locRulesToSkip;
    }

    /**
     * Gets the rule condition.
     * 
     * @param conditionId
     * @return UserCondition
     */
    private UserCondition getCondition(String conditionId) {
        if (conditionId == null || conditionId.isEmpty() || this.filter == null) {
            return null;
        }
        
        if (this.filter.getUserConditions() != null) {
            for (UserCondition userCondition : this.filter.getUserConditions().getUserCondition()) {
                if (userCondition.getId().equals(conditionId)) {
                    return userCondition;
                }
            }
        }
        
        return null;
    }

    /**
     * Look for the chosen options in the rule filter file and returns a list with the identifiers of the rules to exclude.
     * 
     * @param selectedOptions
     *            : the key is the identifier of the condition and the value is the chosen option
     * @return the list of identifiers of rules to exclude
     */
    private Set<String> getRulesToSkipByUserOptions(HashMap<String, String> selectedOptions) {
        Set<String> ret = new HashSet<>();

        selectedOptions.keySet().stream().forEach((conditionId) -> {
            UserCondition condition = this.getCondition(conditionId);
            if (condition != null) {
                List<RuleToSkip> rules = this.getRulesToSkip(condition, selectedOptions.get(conditionId));
                rules.stream().forEach((objectRule) -> {
                    ret.add(objectRule.getId());
                });
            }
        });

        return ret;
    }

    /**
     * 
     * @param ruleId
     * @param valid
     * @return List<>
     */
    private List<String> getRulesToSkipByRuleId(String ruleId, boolean valid) {
        List<String> ret = new ArrayList<>();

        if (this.filter.getRuleConditions() != null) {
            this.filter.getRuleConditions().getRuleCondition().stream().filter((objectRuleCondition) -> (objectRuleCondition.getId().equals(ruleId))).filter((objectRuleCondition) -> ((valid && objectRuleCondition.isValid()) || (!valid && !objectRuleCondition.isValid()))).forEach((objectRuleCondition) -> {
                objectRuleCondition.getRuleToSkip().stream().forEach((objectRule) -> {
                    ret.add(objectRule.getId());
                });
            });
        }
        
        return ret;
    }

    /**
     * Add a collection of rules identifiers to the list of rules to skip
     * 
     * @param objectRulesIdentifiers
     */
    private void addRulesToSkip(Collection<String> objectRulesIdentifiers) {
        objectRulesIdentifiers.stream().filter((objectRuleIdentifier) -> (!this.rulesToSkip.contains(objectRuleIdentifier))).forEach((objectRuleIdentifier) -> {
            this.rulesToSkip.add(objectRuleIdentifier);
        });
    }

    /**
     * Gets the list of identifiers of rules to skip
     * 
     * @return a list of rules to skip
     */
    public List<String> getRulesToSkipList() {
        return this.rulesToSkip;
    }

    /**
     * Check if it is necessary to add some rules to skip since the result of the execution of a cvRule.
     * 
     * @param rule the rule
     * @param valid flag indicating if the rule has been passed or not
     */
    public void updateRulesToSkipByARuleResult(Rule rule, boolean valid) {
        // get cvMappingRules that should be skipped
        List<String> rulesToSkipByRuleId = this.getRulesToSkipByRuleId(rule.getId(), valid);
        if (!rulesToSkipByRuleId.isEmpty()) {
            this.addRulesToSkip(rulesToSkipByRuleId);
        }
    }

    /**
     * Restarts the list of rules to skip.
     */
    public void restartRulesToSkip() {
        this.rulesToSkip = new ArrayList<>();
    }

    /**
     * Depending of the user selections some object and cvMapping rules will be tagged to be skipped in the validator
     * 
     * @param selectedOptions
     *            a set of pairs key, value, where the key is the identifier of
     *            the condition and the value is the name of the chosen option
     */
    public void filterRulesByUserOptions(HashMap<String, String> selectedOptions) {
        this.addRulesToSkip(this.getRulesToSkipByUserOptions(selectedOptions));
    }

    /**
     * Filters the list of ValidatorMessages checking each message with the lists of rules to skip and return the final list of messages
     * 
     * @param msgs list of messages
     * @param extendedReport    the extended validation report
     * @return the collection of validation messages after the filter
     */
    public Collection<ValidatorMessage> filterValidatorMessages(HashMap<String, List<ValidatorMessage>> msgs, ExtendedValidatorReport extendedReport) {
        ArrayList<ValidatorMessage> finalMessages = new ArrayList<>();

        // for each message, check if the rule that generated it is in the list of rules to skip
        if (msgs != null && !msgs.isEmpty()) {
            List<String> skipList = this.getRulesToSkipList();                
            msgs.keySet().stream().forEach((ruleIdentifier) -> {
                // if the rule that generated the messages is in the list of rules to skip, not add to the final message list
                if (skipList.contains(ruleIdentifier)) {
                    // move the rule to the list of non checked rules
                    extendedReport.setObjectRuleAsSkipped(ruleIdentifier);
                }
                else {
                    finalMessages.addAll(msgs.get(ruleIdentifier));
                }
            });
        }
        
        return finalMessages;
    }

    /**
     * Prints out the RuleFilter.
     */
    public void printRuleFilter() {
        for (UserCondition condition : this.filter.getUserConditions().getUserCondition()) {
            System.out.println(NEW_LINE + "Condition: " + condition.getId());
            condition.getUserOption().stream().map((option) -> {
                System.out.println(this.TAB + "Option " + option.getId());
                return option;
            }).map((option) -> {
                option.getRuleToSkip().stream().forEach((rule) -> {
                    System.out.println(this.DOUBLE_TAB + "rule id: " + rule.getId());
                });
                return option;
            }).filter((option) -> (option.getRulesToSkipRef() != null)).forEach((option) -> {
                option.getRulesToSkipRef().stream().filter((ruleSetReference) -> (this.filter.getReferences() != null)).forEach((ruleSetReference) -> {
                    this.filter.getReferences().getReferencedRules().stream().filter((referencedRuleSet) -> (referencedRuleSet.getId().equals(ruleSetReference.getRef()))).forEach((referencedRuleSet) -> {
                        referencedRuleSet.getRuleToSkip().stream().forEach((rule) -> {
                            System.out.println(this.DOUBLE_TAB + "mapping rule id: " + rule.getId());
                        });
                    });
                });
            });
        }
        
        if (this.filter.getRuleConditions() != null) {
            System.out.println(NEW_LINE + "ObjectRuleConditions:");
            this.filter.getRuleConditions().getRuleCondition().stream().map((ruleCondition) -> {
                System.out.println(this.TAB + "Rule condition: " + ruleCondition.getId() + " isValid: " + ruleCondition.isValid());
                return ruleCondition;
            }).forEach((ruleCondition) -> {
                ruleCondition.getRuleToSkip().stream().forEach((objectRule) -> {
                    System.out.println(this.DOUBLE_TAB + "rule to skip: " + objectRule.getId());
                });
            });
        }
        
        if (this.filter.getMandatoryElements() != null) {
            System.out.println(NEW_LINE + "Mandatory elements:");
            this.filter.getMandatoryElements().getMandatoryElement().stream().forEach((mandatorymzMLElement) -> {
                System.out.println(this.TAB + "Mandatory element: " + mandatorymzMLElement.getElement());
            });
        }
    }
}
