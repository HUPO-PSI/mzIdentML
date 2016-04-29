# Configuration resource files:
- miape-msi-rules_x.y.z.xml:	CV rules for MIAPE compliance validation of mzIdentML
- mzIdentML-mapping_x.y.z.xml:	CV rules for semantic validation of mzIdentML
- ObjectRules_x.y.z.xml:		object rules for semantic validation of mzIdentML
- ObjectRulesMIAPE_x.y.z.xml:	object rules for MIAPE compliance validation of mzIdentML
- ontologies.xml:				file that determines how to read the ontology resources by EBI-OLS (Open Lookup Service)
- ontologies_local.xml:			file that determines how to read the local ontology resources
- ruleFilter_MIAPEMSI.xml:		XML file that determine conditionalities in the MIAPE rules
- ruleFilter_semantic.xml:		XML file that determine conditionalities in the rules

Note that these files will be overriden by others if found in the application path

#### ruleFilter_ files:
Let’s say, we want to check that some CVs must be present (let’s call them CV terms B) just when other certain CV term (let’s call it CV term A) is present in the protocol.
There are two files that we should modify: the mapping file and the ruleFilter file.
In the mapping file we should write a rule for checking for the presence of CV term A, let’s say, rule A, and another rule that checks the presence of CV terms B, let’s say, rule B.
At this stage, each of the rules (A and B rules) will generate an error message when their corresponding CV terms are not present.
Now we want to say to the validator that the error messages from rule B should only be showed when rule A is valid.
Additionally, we don’t want to see any error from rule A, since it is only used for the condition.
So then, we want to say to the validator that errors coming from rule A always been discarded.

How to do that? Edit the ruleFilter_semantic.xml file as follows:
<ruleFilter xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://proteo.cnb.csic.es/miape-api/schemas/ruleFilter_v1.4.xsd">
	<ruleConditions>
		<ruleCondition valid="true" id="rule A">
			<ruleToSkip id="rule A">
		</ruleCondition>
		<ruleCondition valid="false" id="rule A">
			<ruleToSkip id="rule B"/>
			<ruleToSkip id="rule A">
		</ruleCondition>    
	</ruleConditions>
</ruleFilter>

#### Just some comments about this approach:
Maybe you can notice that in the resulting mapping file both rule A and rule B will be present.
This can be confusing for the community, since you can think that both rules must be valid in order to get a semantically valid mzId file, which is not true,
since some rules are only applied when some other CV terms are present (rules like rule B in this example), and other rules are just running as conditions and
they never will show any error message (rules like rule A in the example). So that is something we should aware to the people somehow,
apart from writing the appropriate comments in the mapping file.The rules involved in the conditionally checking can be either cvMapping or object rules.
As soon as they have a unique id, it doesn’t matter which rule type they are.

#### MIAPE validation
MIAPE validation is using other different mapping and ruleFiler files (see https://github.com/HUPO-PSI/mzIdentML/tree/master/validator/resources):

o   mzIdentML-mapping_1.2.0.xml, ObjectRules.1.2.0.xml and ruleFilter_semantic.xml for semantic validation, and

o   miape-msi-rules.1.2.0.xml, ObjectRulesMIAPE.1.2.0.xml and  ruleFilter_MIAPEMSI.xml for the MIAPE MSI validation.

so any addition to the semantic validation files should also be incorporated to the MIAPE validation files.