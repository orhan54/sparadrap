package fr.pompey.cda24060.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtility {
    // Regex format date
    private static String DATE_VALIDE = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])" +
            "\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]" +
            "\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1" +
            "\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";

    // Regex alpha
    private static final String ALPHA = "^[\\p{L}'-]+(?:\\s[\\p{L}'-]+)*$";

    // Regex pour un nombre entier
    private static String POSITIVE_INT_REGEX = "\\d+";

    // regex numéro agréement
    private static String NUMERO_AGREEMENT = "^[0-9][0-9]{10}$";

    // Regex email
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // Regex numero telephone
    private static final Pattern VALID_PHONE_NUMBER = Pattern.compile("^\\+?[1-9][0-9]{7,14}$");

    // Regex type double
    private static final String POSITIF_DOUBLE_REGEX = "[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?";

    // Regex pour adresse postal
    private static final String ADRESSE_VALIDE = "^[0-9]+\\s+[a-zA-ZéèêàâôûùïüçÉÈÊÀÂÔÛÙÏÜÇ]+(\\s+[a-zA-ZéèêàâôûùïüçÉÈÊÀÂÔÛÙÏÜÇ]+)*$";


    // *** Vérification de toutes les Regex avec "input.matches" ***

    public static boolean numAgreementValide(String input){ return input != null && input.matches(NUMERO_AGREEMENT); }

    public static boolean dateValide(String input) {
        return input != null && input.matches(DATE_VALIDE);
    }

    public static boolean regexAlpha(String input) {
        return input != null && input.matches(ALPHA);
    }

    public static boolean positifInt(String input) {
        return input != null && input.matches(POSITIVE_INT_REGEX);
    }

    public static boolean validate(String pEmail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(pEmail);
        return matcher.matches();
    }

    public static boolean validatePhone(String pTel) {
        Matcher matcher = VALID_PHONE_NUMBER.matcher(pTel);
        return matcher.matches();
    }

    public static boolean validateDouble(String input) {
        return input != null && input.matches(POSITIF_DOUBLE_REGEX);
    }

    public static boolean validateAdresse(String input) {
        return input != null && input.matches(ADRESSE_VALIDE);
    }
}
