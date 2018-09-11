package psidev.psi.pi.validator.objectrules.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.jmzidml.model.mzidml.CvParam;

public class ObjectRuleUtil {
    /**
     * Checks, if one of the accessions occurs in the list of cvParams.
     * 
     * @param cvParams      list of CV params
     * @param accessions    collection of accessions
     * @return The list of {@link CvParam} found, or an empty list if not found
     **/
    public static List<CvParam> checkAccessionsInCVParams(List<CvParam> cvParams, Collection<String> accessions) {
        List<CvParam> ret = new ArrayList<>();
        
        if (cvParams != null && accessions != null) {
            accessions.stream().map((accession) -> checkAccessionsInCVParams(cvParams, accession)).filter((cvParam) -> (cvParam != null)).forEach((cvParam) -> {
                ret.add(cvParam);
            });
        }
        
        return ret;
    }

    /**
     * Checks, if the accession occurs in the list of cvParams.
     * 
     * @param cvParams     list of CV params
     * @param accession    collection of accessions
     * @return The CvParam found or null if not found
     */
    public static CvParam checkAccessionsInCVParams(List<CvParam> cvParams, String accession) {
        if (cvParams != null && accession != null) {
            for (CvParam cvParam : cvParams) {
                if (accession.equals(cvParam.getAccession()))
                    return cvParam;
            }
        }
        
        return null;
    }
}
