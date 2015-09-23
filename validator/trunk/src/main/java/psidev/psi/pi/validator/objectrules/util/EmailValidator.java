package psidev.psi.pi.validator.objectrules.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for validating email addresses.
 * @author Salva
 */
public class EmailValidator {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN1 = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String EMAIL_PATTERN2 = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*_AT_[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate hex with regular expression.
     * 
     * @param hex   hex for validation
     * @return true valid hex, false invalid hex
     */
    public static boolean validate(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN1);
        matcher = pattern.matcher(hex);
        boolean bRet1 = matcher.matches();
        
        pattern = Pattern.compile(EMAIL_PATTERN2);
        matcher = pattern.matcher(hex);
        boolean bRet2 = matcher.matches();
        
        return bRet1 || bRet2;
    }
}
