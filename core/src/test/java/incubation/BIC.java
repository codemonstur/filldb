package incubation;

import java.util.List;

import static filldb.core.Util.*;
import static java.util.stream.Collectors.toList;

public class BIC {
/*
Only the following countries have BIC codes:

Austria, Belgium, Bulgaria, Cyprus, Czech Republic, Denmark, Estonia, Finland, France,
French, Germany, Gibraltar, Greece, Guadeloupe, Hungary, Iceland, Ireland, Italy, Latvia,
Liechtenstein, Lithuania, Luxembourg, Malta, Martinique, Mayotte, Monaco, Netherlands,
Norway, Poland, Portugal, Reunion Island, Romania, Slovak Republic, Slovenia, Spain,
Sweden, Switzerland, United Kingdom

The Bank Identifier Code (BIC) formerly known as SWIFT code is a unique code to identify
individual banks worldwide. These codes are registered at SWIFT (Society for Worldwide
Interbank Financial Telecommunication). The BIC / SWIFT codes are used when transferring
money between banks, particularly for international wire transfer.

The BIC / SWIFT codes consists of 8 or 11 characters, for example:

BIC / SWIFT code example

Country : Switzerland
Bank name : LLOYDS TSB BANK PLC ZURICH
BIC/SWIFT code : LOYDCHGGZCH
4 letters: Bank identification (for example: LOYD).
2 letters: ISO 3166 A2 country code. The country where the bank is located (for example: CH).
2 letters or digits: The region or city where the bank is located followed by a suffix (for example: GG).
3 letters or digits: This is optional. If the bank is a branch, department or office (for example: ZCH).
*/
    private static String newBICcode() {
        final List<String> bicCountryCodes = resourceToLines("/lists/cc_iso_3166_a2.txt");
        final String bankId = newRandomString(upperCaseLetters, 4);
        final String countryCode = randomItemFrom(bicCountryCodes);
        final String regionId = newRandomString(upperCaseLetters, 2);
        final String branchId = isTrue(chance(0.5)) ? newRandomString(upperCaseLetters, 3) : "";

        return bankId + countryCode + regionId + branchId;
    }

    private static String newRandomString(final List<Character> charset, final int number) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            builder.append(randomItemFrom(charset));
        }
        return builder.toString();
    }

    private static final List<Character> upperCaseLetters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(i -> (char) i).collect(toList());
}
