package filldb.generators.special;

import java.util.List;

import static filldb.core.Util.randomItemFrom;
import static filldb.core.Util.resourceToLines;

public class CountryCode {

    private static String newCountryCode() {
        // TODO there is more than 1 list of country codes. Do the rest?
        final List<String> bicCountryCodes = resourceToLines("/lists/cc_iso_3166_a2.txt");
        return randomItemFrom(bicCountryCodes);
    }
}
