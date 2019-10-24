package filldb.generators;

import com.mifmif.common.regex.Generex;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import filldb.model.Column;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static filldb.core.Constants.LORUM_IPSUM;
import static filldb.core.Util.*;
import static filldb.generators.Images.newRandomImageGenerator;
import static filldb.generators.LocalLorums.newRandomLocalIpsumGenerator;
import static filldb.generators.RemoteLorums.newRandomRemoteIpsumGenerator;
import static filldb.generators.ValueGenerator.newValueGenerator;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

public enum DataGenerators {;

    public static List<ValueGenerator> newDataGenerators(final boolean allowHumor, final boolean allowRemote
            , final boolean allowNSFW) {
        return Arrays.asList(newHexGenerator(), newAddressGenerator(), newStreetGenerator(), newCityGenerator()
            , newStateGenerator(), newCountryGenerator(), newAgeGenerator(), newDateOfBirthGenerator()
            , newDateGenerator(), newEmailGenerator(), newFirstNameGenerator(), newLastNameGenerator()
            , newMiddleNameGenerator(), newFullNameGenerator(), newPhoneGenerator(), newPhoneNumberGenerator()
            , newDomainGenerator(), newTimestampGenerator(), newJobGenerator(), newUriGenerator(allowHumor)
            , newImageGenerator(allowHumor, allowRemote, allowNSFW), newLargeTextGenerator(allowHumor, allowRemote)
            , newShortTextGenerator(allowHumor));
    }

    public static ValueGenerator newAddressGenerator() {
        final List<String> streetNames = resourceToLines("/lists/streets.txt");
        return newValueGenerator("Addresses", newLengthAndAllWordCheck(20, "address"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(randomItemFrom(streetNames)+" "+randomInt(9)+1, column.characterMaxLength)));
    }

    public static ValueGenerator newStreetGenerator() {
        final List<String> streetNames = resourceToLines("/lists/streets.txt");
        return newValueGenerator("Street names", newLengthAndAllWordCheck(20, "street"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(randomItemFrom(streetNames), column.characterMaxLength)));
    }

    public static ValueGenerator newCityGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("City names", newLengthAndAllWordCheck(20, "city"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getCity(), column.characterMaxLength)));
    }

    public static ValueGenerator newStateGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("State names", newLengthAndAllWordCheck(20, "state"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getStateFull(), column.characterMaxLength)));
    }

    public static ValueGenerator newCountryGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("Country names", newLengthAndAllWordCheck(20, "country"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getCountry(), column.characterMaxLength)));
    }

    public static ValueGenerator newEmailGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("email addresses", newLengthAndAllWordCheck(20, "email"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getEmail(), column.characterMaxLength)));
    }

    public static ValueGenerator newPhoneGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"varchar".equals(column.dataType)) return false;
            if (column.characterMaxLength == null || column.characterMaxLength < 20) return false;
            return column.name.contains("phone");
        };

        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("phone number", canGenerateFor,
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getPhone(), column.characterMaxLength)));
    }

    public static ValueGenerator newPhoneNumberGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"int".equals(column.dataType) && !"bigint".equals(column.dataType)) return false;
            return column.name.contains("phone");
        };

        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("phone number as int", canGenerateFor, column ->
            (index, statement) -> statement.setLong(index, toNumber(lorem.getPhone())));
    }

    public static ValueGenerator newFullNameGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("Full names", newLengthAndAllWordCheck(20, "name"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getName(), column.characterMaxLength)));
    }

    public static ValueGenerator newFirstNameGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("First names", newLengthAndAllWordCheck(10, "first", "name"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getFirstName(), column.characterMaxLength)));
    }

    public static ValueGenerator newMiddleNameGenerator() {
        final List<String> middleNames = resourceToLines("/lists/middle-names.txt");
        return newValueGenerator("Middle names", newLengthAndAllWordCheck(10, "middle", "name"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(randomItemFrom(middleNames), column.characterMaxLength)));
    }

    public static ValueGenerator newLastNameGenerator() {
        final Lorem lorem = LoremIpsum.getInstance();
        return newValueGenerator("Last names", newLengthAndAllWordCheck(10, "last", "name"),
            column -> (index, statement) -> statement.setString(index,
                abbreviate(lorem.getLastName(), column.characterMaxLength)));
    }

    public static ValueGenerator newDomainGenerator() {
        final List<String> topDomains = resourceToLines("/lists/top-domains.txt");
        final List<String> allDomains = resourceToLines("/lists/domains.txt");

        final Function<Column, Boolean> canGenerateFor = column -> {
            if (column.characterMaxLength == null || column.characterMaxLength < 32) return false;
            if (!"varchar".equals(column.dataType)) return false;
            return column.name.contains("hostname") || column.name.contains("domain");
        };

        return newValueGenerator("Domain names", canGenerateFor, column -> (index, statement) -> {
            final double chance = random();
            String result = randomItemFrom(topDomains);
            if (chance < 0.25) result = randomItemFrom(allDomains);
            statement.setString(index, abbreviate(result, column.characterMaxLength));
        });
    }

    public static ValueGenerator newUriGenerator(final boolean allowHumor) {
        final List<String> timewaste = resourceToLines("/lists/timewastelinks.txt");
        final List<String> youtube = resourceToLines("/lists/youtube.txt");
        final Lorem lorem = LoremIpsum.getInstance();

        return newValueGenerator("URIs", newLengthAndAnyWordCheck(64, "url", "uri", "link"),
            column -> (index, statement) -> {
                String result = lorem.getUrl();
                if (allowHumor && isTrue(chance(0.5))) result = randomItemFrom(timewaste);
                if (allowHumor && isTrue(chance(0.3))) result = randomItemFrom(youtube);
                statement.setString(index, abbreviate(result, column.characterMaxLength));
            });
    }

    public static ValueGenerator newHexGenerator() {
        final var randomStart = Double.toString(round(random()*1000));
        final var generator = new Generex(randomStart+"[a-zA-Z]{1,30}").iterator();
        final var canGenerateFor = newLengthAndAnyWordCheck(32, "hex", "salt", "password", "id");

        return newValueGenerator("Hash as hex", canGenerateFor,
            column -> (index, statement) -> statement.setString(index,
                hexHash(generator.next(), column.characterMaxLength)));
    }

    public static ValueGenerator newAgeGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"int".equals(column.dataType) && !"bigint".equals(column.dataType)) return false;
            return column.name.contains("age");
        };

        return newValueGenerator("Age", canGenerateFor, column ->
            (index, statement) -> statement.setLong(index, round(random()*95)));
    }

    public static ValueGenerator newDateGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"varchar".equals(column.dataType)) return false;
            if (column.characterMaxLength == null || column.characterMaxLength != 10) return false;
            return column.name.contains("date");
        };

        final DateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
        return newValueGenerator("Date in yyyy-mm-dd", canGenerateFor, column ->
            (index, statement) -> statement.setString(index,
                yyyy_mm_dd.format(new Date(substractSomeTime(currentTimeMillis())))));
    }

    public static ValueGenerator newDateOfBirthGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"varchar".equals(column.dataType)) return false;
            if (column.characterMaxLength == null || column.characterMaxLength != 10) return false;
            return column.name.contains("birth") && column.name.contains("date");
        };

        final DateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
        return newValueGenerator("Date of birth", canGenerateFor, column ->
            (index, statement) -> statement.setString(index,
                yyyy_mm_dd.format(new Date(subtractYears(randomInt(95),
                        substractSomeTime(currentTimeMillis()))))));
    }

    public static ValueGenerator newTimestampGenerator() {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"bigint".equals(column.dataType)) return false;
            final String columnName = column.name.toLowerCase();
            return columnName.contains("date") || columnName.contains("timestamp")
                || columnName.contains("last_login") || columnName.contains("update");
        };
        return newValueGenerator("Timestamps", canGenerateFor, column ->
            (index, statement) -> statement.setLong(index,
                substractSomeTime(currentTimeMillis())));
    }

    public static ValueGenerator newJobGenerator() {
        final var jobs = resourceToLines("/lists/jobs.txt");
        final var canGenerateFor = newLengthAndAnyWordCheck(16,
                "job", "vacancy", "position", "role", "profession");

        return newValueGenerator("Job titles", canGenerateFor, column ->
            (index, statement) -> statement.setString(index,
                abbreviate(capitalize(randomItemFrom(jobs)), column.characterMaxLength)));
    }

    public static ValueGenerator newShortTextGenerator(final boolean allowHumor) {
        final List<String> jokes = resourceToLines("/lists/jokes.txt");
        final List<String> insults = resourceToLines("/lists/insults.txt");
        final List<String> attacks = resourceToLines("/lists/attacks.txt");

        final Function<Column, Boolean> canGenerateFor = column -> {
            if (column.characterMaxLength == null || column.characterMaxLength < 64) return false;
            return "varchar".equals(column.dataType);
        };

        return newValueGenerator("Short texts", canGenerateFor, column -> (index, statement) -> {
            String result = LORUM_IPSUM;
            if (allowHumor && isTrue(chance(0.4))) result = randomItemFrom(jokes);
            else if (allowHumor && isTrue(chance(0.4))) result = insertText(randomItemFrom(insults), "the tester");
            else if (isTrue(chance(0.8))) result = randomItemFrom(attacks);
            statement.setString(index, abbreviate(result, column.characterMaxLength));
        });
    }

    public static ValueGenerator newLargeTextGenerator(final boolean allowHumor, final boolean allowRemote) {
        final var local = newRandomLocalIpsumGenerator();
        final var remote = newRandomRemoteIpsumGenerator();

        final Function<Column, Boolean> canGenerateFor = column -> "text".equals(column.dataType);
        return newValueGenerator("Large texts", canGenerateFor, column -> (index, statement) -> {
            String result = LORUM_IPSUM;
            if (allowHumor && allowRemote && isTrue(chance(0.5))) result = remote.get();
            if (allowHumor && isTrue(chance(0.5))) result = local.get();
            statement.setString(index, result);
        });
    }

    public static ValueGenerator newImageGenerator(final boolean allowHumor, final boolean allowRemote, final boolean allowNSFW) {
        final Function<Column, Boolean> canGenerateFor = column -> {
            if (!"longblob".equals(column.dataType)) return false;
            return column.name.contains("image") || column.name.contains("picture");
        };

        final var images = newRandomImageGenerator(allowRemote, allowHumor, allowNSFW);
        return newValueGenerator("Images", canGenerateFor, column -> (index, statement) ->
            statement.setBinaryStream(index, new ByteArrayInputStream(images.get())));
    }


    private static Function<Column, Boolean> newLengthAndAllWordCheck(final int length, final String... words) {
        return column -> {
            if (column.characterMaxLength == null || column.characterMaxLength < length) return false;
            for (final String word : words) {
                if (!column.name.contains(word)) return false;
            }
            return true;
        };
    }

    private static Function<Column, Boolean> newLengthAndAnyWordCheck(final int length, final String... words) {
        return column -> {
            final String columnName = column.name.toLowerCase();
            if (column.characterMaxLength == null || column.characterMaxLength < length) return false;
            for (final String word : words) {
                if (columnName.contains(word)) return true;
            }
            return false;
        };
    }

}
