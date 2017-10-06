# mzIdentML validator - Semantic validation of mzIdentML 1.1 and 1.2 files


### General
The mzIdentML validator is based on the [PSI validator generic framework] (http://www.psidev.info/validator)
which is described in detail in the following [tutorial] (http://www.psidev.info/validator-tutorial-0).

The mzIdentML validator makes use of mapping files describing at which positions in the data files which CV terms from
the [psi-ms.obo ontology] (https://raw.githubusercontent.com/HUPO-PSI/psi-ms-CV/master/psi-ms.obo) are allowed.
In addition there are object rules (encoded in Java) for cases where a configuration via the mapping file mechanism is not possible.

All mapping / object rules which are violoated are reported in coloured output encoding the type of violated rule:

        A violated MUST   rule is reported in red
        A violated SHOULD rule is reported in orange
        A violated MAY    rule is reported in green


### Download link
The latest version of the mzIdentML validator can be downloaded [here] (https://github.com/HUPO-PSI/mzIdentML/blob/master/validator/mzIdentMLValidator_GUI_v1.4.28-SNAPSHOT.zip)


### Literature
More details about the mapping file mechanism and the validator are described in the following publications:

* Mayer G, Jones AR, Binz PA, Deutsch EW, Orchard S, Montecchi-Palazzi L, Vizcaíno JA, Hermjakob H, Oveillero D, Julian R, Stephan C, Meyer HE, Eisenacher M.
Controlled vocabularies and ontologies in proteomics: overview, principles and practice.
Biochim Biophys Acta. 2014 Jan;1844(1 Pt A):98-107. doi: 10.1016/j.bbapap.2013.02.017. Epub 2013 Feb 19.
[pdf] (http://ac.els-cdn.com/S1570963913000800/1-s2.0-S1570963913000800-main.pdf?_tid=fa5771ec-0acb-11e6-90c5-00000aab0f6b&acdnat=1461578367_5d20743396f240fc9fbb9075ed94a4c8)


* Ghali F, Krishna R, Lukasse P, Martínez-Bartolomé S, Reisinger F, Hermjakob H, Vizcaíno JA, Jones AR.
Tools (Viewer, Library and Validator) that facilitate use of the peptide and protein identification standard format, termed mzIdentML.
Mol Cell Proteomics. 2013 Nov;12(11):3026-35. doi: 10.1074/mcp.O113.029777. Epub 2013 Jun 28.
[pdf] (http://www.mcponline.org/cgi/pmidlookup?view=long&pmid=23813117)


