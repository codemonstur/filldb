package filldb.core;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;

public enum HTTP {;

    public static <K, V> String toFormData(final Map<K, V> data) {
        final var list = new ArrayList<String>();
        for (final var entry : data.entrySet()) {
            list.add(toFormEntry(entry));
        }
        return join("&", list);
    }

    private static <K, V> String toFormEntry(final Map.Entry<K, V> entry) {
        return encodeUrl(entry.getKey().toString(), UTF_8) + "=" + encodeUrl(entry.getValue().toString(), UTF_8);
    }

    public static String encodeUrl(final String input, final Charset charset) {
        return URLEncoder.encode(input, charset);
    }

}
