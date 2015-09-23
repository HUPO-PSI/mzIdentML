PSI mzIdentML semantic validator
================================

Disclaimer
----------
This validator is not part of the formal document process and is still under development.
At this stage, the validation is neither complete nor has been thoroughly tested for correctness.
The current version should be regarded as a prototype and proof of concept implementation not as final version!


Configuration resource files:

- mzIdentML-mapping.1.1.0.xml: cv rules for semantic validation of mzIdentML v1.1
- miape-msi-rules.1.1.0.xml: cv rules for MIAPE compliance validation of mzIdentML v1.1
- ObjectRules.1.1.0.xml: object rules for semantic validation of mzIdentML v1.1
- ObjectRulesMIAPE.1.1.0.xml: object rules for MIAPE compliance validation of mzIdentML v1.1

- mzIdentML-mapping.1.2.0.xml: cv rules for semantic validation of mzIdentML v1.2
- miape-msi-rules.1.2.0.xml: cv rules for MIAPE compliance validation of mzIdentML v1.2
- ObjectRules.1.2.0.xml: object rules for semantic validation of mzIdentML v1.2
- ObjectRulesMIAPE.1.2.0.xml: object rules for MIAPE compliance validation of mzIdentML v1.2

- ontologies.xml: ontology resources, which by default are using OLS queries, except for the UNIMOD ontology that is integrated.
- ruleFilter_MIAPEMSI.xml: xml file that determine conditionalities in the rules for the MIAPE validation
- ruleFilter_semantic.xml: xml file that determine conditionalities in the rules for the semantic validation
