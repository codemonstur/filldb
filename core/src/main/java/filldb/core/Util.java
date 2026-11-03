package filldb.core;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.*;

public enum Util {;

    public static int randomInt(final int maxValueExclusive) {
        return (int) (random() * maxValueExclusive);
    }

    public static <T> T randomItemFrom(final List<T> list) {
        return list.get(randomInt(list.size()));
    }

    public static long chance(final double p) {
        return random() < p ? 1 : 0;
    }
    public static boolean isTrue(final long chance) {
        return chance == 1;
    }

    public static long substractSomeTime(final long timestamp) {
        final long seconds = randomInt(60) * chance(0.9) * SECONDS.toMillis(1);
        final long minutes = randomInt(60) * chance(0.7) * MINUTES.toMinutes(1);
        final long hours = randomInt(24) * chance(0.5) * HOURS.toMillis(1);
        final long days = randomInt(7) * chance(0.3) * DAYS.toMillis(1);
        final long weeks = randomInt(4) * chance(0.2) * DAYS.toMillis(7);
        final long months = randomInt(12) * chance(0.1) * DAYS.toMillis(30);
        final long years = randomInt(3) * chance(0.01) * DAYS.toMillis(365);

        return timestamp - seconds - minutes - hours - days - weeks - months - years;
    }

    public static long subtractYears(final int years, final long timestamp) {
        return timestamp - (years * DAYS.toMillis(365));
    }

    private static final char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(final byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] sha256(final String value) {
        try {
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(value.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // Can't happen
            return null;
        }
    }

    public static byte[] sha1(final String value) {
        try {
            final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            return sha1.digest(value.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // Can't happen
            return null;
        }
    }

    public static byte[] md5(final String value) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(value.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // Can't happen
            return null;
        }
    }

    public static String abbreviate(final String input, final int maxLength) {
        if (maxLength <= 3 || input == null || input.length() <= maxLength) return input;
        return input.substring(0, maxLength-4)+"... ";
    }

    public static String insertText(final String format, final String filler) {
        final int length = countSubstring("%s", format);
        final Object[] input = new Object[length];
        for (int i = 0; i < input.length; i++) {
            input[i] = filler;
        }
        return String.format(format, input);
    }

    public static int countSubstring(final String substring, final String input) {
        final String temp = input.replace(substring, "");
        return (input.length() - temp.length()) / substring.length();
    }

    public static String newSentence(final List<String> words) {
        final int numWords = randomInt(6) + 4;
        final List<String> chosen = new ArrayList<>(numWords);
        for (int i = 0, j = 0; i < numWords; i++, j++) {
            chosen.add(words.get(randomInt(words.size())));
            if (j > 3 && i < numWords - 2 && isTrue(chance(0.5)))
                chosen.add(words.get(randomInt(words.size()))+",");
        }
        chosen.set(0, capitalize(chosen.get(0)));
        return String.join(" ", words) + ".";
    }

    public static String capitalize(final String input) {
        if (input == null) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static Long toNumber(final String input) {
        return Long.valueOf(input.replaceAll("[^0-9]+", ""));
    }

    public static String hexHash(final String value, final int maxLength) {
        if (maxLength >= 64) return bytesToHex(sha256(value));
        if (maxLength >= 40) return bytesToHex(sha1(value));
        return bytesToHex(md5(value));
    }

    public static List<String> resourceToLines(final String resource) {
        try (final InputStream in = Util.class.getResourceAsStream(resource)) {
            if (in == null) throw new IOException("Resource " + resource + " does not exist.");
            return streamToLines(in, UTF_8);
        } catch (IOException e) {
            return emptyList();
        }
    }

    public static List<String> streamToLines(final InputStream in, final Charset charset) throws IOException {
        final List<String> lines = new ArrayList<>();
        try (final BufferedReader reader  = new BufferedReader(new InputStreamReader(in, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static String resourceAsString(final String resource) {
        try (final InputStream in = Util.class.getResourceAsStream(resource)) {
            if (in == null) throw new IOException("Resource " + resource + " does not exist.");
            return streamAsString(in, UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public static String streamAsString(final InputStream in, final Charset charset) throws IOException {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024]; int read;
            while ((read = in.read(buffer)) != -1) {
                result.write(buffer, 0, read);
            }
            return result.toString(charset.name());
        }
    }

    public static byte[] resourceAsBytes(final String resource) throws IOException {
        try (final InputStream in = Util.class.getResourceAsStream(resource)) {
            return streamAsBytes(in);
        }
    }

    public static byte[] streamAsBytes(final InputStream in) throws IOException {
        try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024]; int read;
            while ((read = in.read(buffer)) != -1) {
                result.write(buffer, 0, read);
            }
            return result.toByteArray();
        }
    }
}
