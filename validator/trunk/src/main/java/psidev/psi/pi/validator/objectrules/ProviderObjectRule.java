package psidev.psi.pi.validator.objectrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import psidev.psi.pi.validator.objectrules.util.EmailValidator;
import psidev.psi.pi.validator.objectrules.util.ObjectRuleUtil;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.OntologyUtils;
import psidev.psi.tools.ontology_manager.impl.OntologyTermImpl;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccess;
import psidev.psi.tools.validator.Context;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import uk.ac.ebi.jmzidml.MzIdentMLElement;
import uk.ac.ebi.jmzidml.model.mzidml.Affiliation;
import uk.ac.ebi.jmzidml.model.mzidml.ContactRole;
import uk.ac.ebi.jmzidml.model.mzidml.CvParam;
import uk.ac.ebi.jmzidml.model.mzidml.Organization;
import uk.ac.ebi.jmzidml.model.mzidml.Person;
import uk.ac.ebi.jmzidml.model.mzidml.Provider;
import uk.ac.ebi.jmzidml.model.mzidml.Role;

/**
 * Check if the provider has the appropriate information: - a contactRole or if
 * not: a not. If yes, checks if the a Person or a Organization is provided with
 * the appropriate minimal information
 * 
 * @author Salva
 * 
 */
public class ProviderObjectRule extends AObjectRule<Provider> {

    /**
     * Constants.
     */
    private static final String LOC_NEW_LINE = System.getProperty("line.separator");
    private static final Context PROVIDER_CONTEXT = new Context(MzIdentMLElement.Provider.getXpath());
    private static final String EMAIL_ACC = "MS:1000589";
    private static final String CONTACT_ORGANIZATION = "MS:1000590";
    private static final String ROLE_TYPE_ACC = "MS:1001266";
    private static final String CONTACT_NAME_ACC = "MS:1000586";
    
    /**
     * Members.
     */
    private Collection<String> roleTypeAccessions;
    private boolean contactRoleError = false;
    private boolean emailError = false;
    private boolean organizationNameError = false;

    /**
     * Constructor.
     */
    public ProviderObjectRule() {
        this(null);
    }

    /**
     * Constructor.
     * @param ontologyManager the ontology manager
     */
    public ProviderObjectRule(OntologyManager ontologyManager) {
        super(ontologyManager);
        this.getRequiredAccessionsFromOntology();
    }

    /**
     * Checks, if the object is a Provider.
     * 
     * @param obj   the object to check
     * @return true, if obj is a Provider
     */
    @Override
    public boolean canCheck(Object obj) {
            return (obj instanceof Provider);
    }

    /**
     * 
     * @param provider the Provider element
     * @return collection of messages
     * @throws ValidatorException validator exception
     */
    @Override
    public Collection<ValidatorMessage> check(Provider provider) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<>();
        StringBuilder messageString = new StringBuilder();
        
        if (provider.getContactRole() == null) {
            this.contactRoleError = true;
            messageString.append("There is not a ContactRole in the Provider (id='").append(provider.getId()).append("') element at ").append(ProviderObjectRule.PROVIDER_CONTEXT.getContext());
        }
        else {
            // check if the provider has an email
            if (!hasEmail(provider.getContactRole())) {
                this.emailError = true;
                if (!messageString.toString().isEmpty())
                    messageString.append(LOC_NEW_LINE);
                messageString
                    .append("There is not a valid e-mail contact ('")
                    .append(EMAIL_ACC)
                    .append("') in provider (id='")
                    .append(provider.getId())
                    .append("') or referenced person or organizations element at ")
                    .append(ProviderObjectRule.PROVIDER_CONTEXT.getContext());
            }
            
            // check if the provider has an organization name
            if (!hasOrganizationName(provider.getContactRole())) {
                this.organizationNameError = true;
                if (!messageString.toString().isEmpty())
                    messageString.append(LOC_NEW_LINE);
                messageString
                    .append("There is not an affiliation name for the organizations referenced by the provider (id='")
                    .append(provider.getId())
                    .append("') at ")
                    .append(ProviderObjectRule.PROVIDER_CONTEXT.getContext());
            }

            // check if the provider has a valid person or a role type
            boolean validPerson = checkPerson(provider.getContactRole());
            boolean validRoleType = checkRoleType(provider.getContactRole());
            if (!validPerson && !validRoleType) {
                //this.validPersonError = true;
                if (!messageString.toString().isEmpty())
                    messageString.append(LOC_NEW_LINE);
                messageString
                    .append("There is not a valid person or a valid role type referenced by the provider (id='")
                    .append(provider.getId())
                    .append("') at ")
                    .append(ProviderObjectRule.PROVIDER_CONTEXT.getContext());
            }
        }
        
        if (!messageString.toString().isEmpty()) {
            messages.add(new ValidatorMessage(messageString.toString(), MessageLevel.ERROR, ProviderObjectRule.PROVIDER_CONTEXT, this));
        }
        
        return messages;
    }

    /**
     * 
     * @param contactRole
     * @return boolean
     */
    private boolean checkPerson(ContactRole contactRole) {
        if (contactRole != null) {
            final Person person = contactRole.getPerson();
            if (person != null) {
                if (person.getFirstName() != null && !person.getFirstName().isEmpty())
                    return true;
                if (person.getName() != null && !person.getName().isEmpty())
                    return true;
                if (person.getLastName() != null && !person.getLastName().isEmpty())
                    return true;
                final CvParam contactNameTerm = ObjectRuleUtil.checkAccessionsInCVParams(person.getCvParam(), CONTACT_NAME_ACC);
                if (contactNameTerm != null) {
                    if (contactNameTerm.getValue() != null && !contactNameTerm.getValue().isEmpty())
                        return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 
     * @param contactRole
     * @return boolean
     */
    private boolean checkRoleType(ContactRole contactRole) {
        if (contactRole != null) {
            final Role role = contactRole.getRole();
            if (role != null) {
                List<CvParam> cvParams = new ArrayList<>();
                cvParams.add(role.getCvParam());
                final List<CvParam> roleTypeTerms = ObjectRuleUtil.checkAccessionsInCVParams(cvParams, roleTypeAccessions);
                if (roleTypeTerms != null && !roleTypeTerms.isEmpty()) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 
     * @param contactRole
     * @return boolean
     */
    private boolean hasOrganizationName(ContactRole contactRole) {
        if (contactRole != null) {
            Person person = contactRole.getPerson();
            if (person != null) {
                if (hasOrganizationName(person))
                    return true;
            }
            
            Organization organization = contactRole.getOrganization();
            if (organization != null)
                if (hasOrganizationName(organization))
                    return true;
        }
        
        return false;
    }

    /**
     * 
     * @param person
     * @return boolean
     */
    private boolean hasOrganizationName(Person person) {
        if (hasOrganizationName(person.getCvParam()))
            return true;
        if (person.getAffiliation() != null) {
            final List<Affiliation> affiliations = person.getAffiliation();
            if (affiliations.stream().filter((affiliation) -> (affiliation.getOrganization() != null)).anyMatch((affiliation) -> (hasOrganizationName(affiliation.getOrganization())))) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 
     * @param organization
     * @return boolean
     */
    private boolean hasOrganizationName(Organization organization) {
        if (organization != null) {
            if (hasOrganizationName(organization.getCvParam()))
                return true;
            if (organization.getName() != null && !organization.getName().isEmpty())
                return true;
            if (organization.getParent() != null) {
                return hasOrganizationName(organization.getParent().getOrganization());
            }
        }

        return false;
    }

    /**
     * 
     * @param cvParams
     * @return boolean
     */
    private boolean hasOrganizationName(List<CvParam> cvParams) {
        if (cvParams != null) {
            final CvParam contactOrganizationTerm = ObjectRuleUtil.checkAccessionsInCVParams(cvParams, CONTACT_ORGANIZATION);
            if (contactOrganizationTerm != null) {
                if (contactOrganizationTerm.getValue() != null && !contactOrganizationTerm.getValue().isEmpty())
                    return true;
            }
        }
        
        return false;
    }

    /**
     * 
     * @param contactRole
     * @return boolean
     */
    private boolean hasEmail(ContactRole contactRole) {
        if (contactRole != null) {
            if (contactRole.getContact() != null) {
                if (hasEmail(contactRole.getContact().getCvParam()))
                    return true;
            }
            
            final Person person = contactRole.getPerson();
            if (person != null) {
                if (hasEmail(person))
                    return true;
            }

            final Organization organization = contactRole.getOrganization();
            if (organization != null) {
                if (hasEmail(organization))
                    return true;
            }
        }
        
        return false;
    }

    /**
     * 
     * @param person
     * @return boolean
     */
    private boolean hasEmail(Person person) {
        if (person != null) {
            if (hasEmail(person.getCvParam()))
                return true;
            if (person.getAffiliation() != null) {
                if (person.getAffiliation().stream().filter((affiliation) -> (affiliation.getOrganization() != null)).anyMatch((affiliation) -> (hasEmail(affiliation.getOrganization())))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 
     * @param organization
     * @return boolean
     */
    private boolean hasEmail(Organization organization) {
        if (organization != null) {
            if (hasEmail(organization.getCvParam()))
                return true;
            if (organization.getParent() != null) {
                if (organization.getParent().getOrganization() != null)
                    if (hasEmail(organization.getParent().getOrganization()))
                        return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param cvParam
     * @return boolean
     */
    private boolean hasEmail(List<CvParam> cvParam) {
        final CvParam emailTerm = ObjectRuleUtil.checkAccessionsInCVParams(cvParam, EMAIL_ACC);
        
        if (emailTerm != null) {
            if (emailTerm.getValue() != null && !emailTerm.getValue().isEmpty()) {
                return EmailValidator.validate(emailTerm.getValue());
            }
        }

        return false;
    }

    /**
     * Gets the tips how to fix the error.
     * 
     * @return  collection of tips
     */
    @Override
    public Collection<String> getHowToFixTips() {
        List<String> ret = new ArrayList<>();

        if (this.contactRoleError) {
            ret.add("Add a ContactRole element in the Provider element.");
        }
        
        if (this.emailError) {
            ret.add("Add a valid contact email ('" + EMAIL_ACC + "') in any referenced element in the provider.");
        }
        
        if (this.organizationNameError) {
            ret.add("Add an affiliation name ('" + CONTACT_ORGANIZATION + "') in any referenced element in the provider.");
        }
        
        return ret;
    }

    /**
     * 
     */
    private void getRequiredAccessionsFromOntology() {
        final OntologyAccess msOntology = ontologyManager.getOntologyAccess("MS");
        this.roleTypeAccessions = OntologyUtils.getAccessions(msOntology.getDirectChildren(new OntologyTermImpl(ROLE_TYPE_ACC)));
    }
}
