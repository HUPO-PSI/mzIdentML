Configuration resource files:
- miape-msi-rules_x.y.z.xml:	CV rules for MIAPE compliance validation of mzIdentML
- mzIdentML-mapping_x.y.z.xml:	CV rules for semantic validation of mzIdentML
- ObjectRules_x.y.z.xml:		object rules for semantic validation of mzIdentML
- ObjectRulesMIAPE_x.y.z.xml:	object rules for MIAPE compliance validation of mzIdentML
- ontologies.xml:				file that determines how to read the ontology resources by EBI-OLS (Open Lookup Service)
- ontologies_local.xml:			file that determines how to read the local ontology resources
- ruleFilter_MIAPEMSI.xml:		XML file that determine conditionalities in the MIAPE rules
- ruleFilter_semantic.xml:		XML file that determine conditionalities in the rules

Note that these files will be overriden by others if found in the application path