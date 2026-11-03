package filldb.generators;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static filldb.core.Constants.LORUM_IPSUM;
import static filldb.core.Util.*;
import static java.util.Arrays.asList;

public enum LocalLorums {;

    public static Supplier<String> newRandomLocalIpsumGenerator() {
        final List<LorumIpsumGenerator> generators =
            asList( newDeLoreanIpsum(), newDoggoIpsum(), newMauiIpsum()
                  , newRikerIpsum(), newSamuelJacksonIpsum(), newFuzzAttacksIpsum());

        return () -> {
            int tries = 3;
            while (tries > 0) {
                try {
                    return randomItemFrom(generators).getIpsum();
                } catch (IOException e) { /* Local ones can't fail but yeah */ }
                tries--;
            }
            return LORUM_IPSUM;
        };
    }

    public static LorumIpsumGenerator newDeLoreanIpsum() {
        final List<String> movie = resourceToLines("/lorum/delorean.txt");
        return () -> {
            final int index = randomInt(movie.size());
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                builder.append(movie.get(i+index % movie.size()));
                builder.append("\n");
            }
            return builder.toString();
        };
    }

    public static LorumIpsumGenerator newDoggoIpsum() {
        final List<String> dogwords = resourceToLines("/lorum/doggo.txt");
        return () -> {
            final int numSentences = randomInt(20)+5;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < numSentences; i++) {
                builder.append(newSentence(dogwords));
            }
            return builder.toString();
        };
    }

    public static LorumIpsumGenerator newMauiIpsum() {
        final String fishEnglish = resourceAsString("/lorum/maui-fish-english.txt");
        final String fishMaori = resourceAsString("/lorum/maui-fish-maori.txt");
        final String sunEnglish = resourceAsString("/lorum/maui-sun-english.txt");
        final String sunMaori = resourceAsString("/lorum/maui-sun-maori.txt");
        return () -> {
            if (isTrue(chance(0.25))) return fishEnglish;
            if (isTrue(chance(0.25))) return fishMaori;
            if (isTrue(chance(0.25))) return sunEnglish;
            return sunMaori;
        };
    }

    public static LorumIpsumGenerator newRikerIpsum() {
        final List<String> riker = resourceToLines("/lorum/riker.txt");
        return () -> {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 25; i++) {
                builder.append(riker.get(randomInt(riker.size())));
                builder.append("\n");
            }
            return builder.toString();
        };
    }

    public static LorumIpsumGenerator newSamuelJacksonIpsum() {
        final List<String> jackson = resourceToLines("/lorum/samuel-l-jackson.txt");
        return () -> randomItemFrom(jackson);
    }

    public static LorumIpsumGenerator newFuzzAttacksIpsum() {
        final List<String> attacks = resourceToLines("/lists/all-attacks.txt");
        return () -> randomItemFrom(attacks);
    }
}
