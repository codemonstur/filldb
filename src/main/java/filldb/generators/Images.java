package filldb.generators;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.function.Supplier;

import static filldb.core.Util.*;
import static java.net.http.HttpClient.newHttpClient;
import static java.util.Arrays.asList;

public enum Images {;

    public interface ImageGenerator {
        byte[] getImage(final HttpClient client) throws IOException;
    }

    public static Supplier<byte[]> newRandomImageGenerator(final boolean allowRemote, final boolean allowHumor, final boolean allowNSFW) {
        final HttpClient client = newHttpClient();

        final List<ImageGenerator> remote =
            asList(Images::getLifeLooselyBased, Images::getPhdComics, Images::getUserFriendly
                , Images::getDilbert, Images::getXkcd, Images::getKopozky, Images::getOkCancel
                , Images::getCommitStrip, Images::getAbstruseGoose);
        final List<ImageGenerator> nsfw = asList(Images::getOglaf);
        final List<ImageGenerator> local = asList(Images::getKowalsky, Images::getUnderConstruction);

        final int numGenerators = remote.size()+nsfw.size()+local.size();
        final double pNSFW = (1.0 / numGenerators) * nsfw.size();
        final double pHumor = (1.0 / numGenerators) * remote.size();

        return () -> {
            int tries = 3;
            while (tries > 0) {
                try {
                    if (allowRemote && allowHumor && allowNSFW && isTrue(chance(pNSFW)))
                        return randomItemFrom(nsfw).getImage(client);
                    if (allowRemote && allowHumor && isTrue(chance(pHumor)))
                        return randomItemFrom(remote).getImage(client);
                    return randomItemFrom(local).getImage(client);
                } catch (IOException e) {
                    /* Remote generators can fail so we just try a bit and fall
                     * back to regular Ipsum if not possible
                     */
                }
                tries--;
            }
            return getUnderConstruction(null);
        };
    }

    public static byte[] getKowalsky(final HttpClient client) {
        try {
            return resourceAsBytes("/images/kowalski.png");
        } catch (IOException e) { return null; }
    }
    public static byte[] getUnderConstruction(final HttpClient client) {
        try {
            return resourceAsBytes("/images/under-construction.jpg");
        } catch (IOException e) { return null; }
    }


    public interface PageParser {
        String extractImageLink(Document document) throws IOException;
    }

    public static byte[] getLifeLooselyBased(final HttpClient client) throws IOException {
        final String site = "LifeLooselyBased";
        // FIXME only some of the IDs between 1 and 200 lead to an image, we could list them all
        // now we throw an error, let the retry deal with it
        final int comicId = randomInt(201);
        final String url = "http://www.lifelooselybased.com/index.php?comic_id="+comicId;
        final String imgPrefix = "http://www.lifelooselybased.com/imgs/comics/";
        final String cssSelector = "body > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(1) > a:nth-child(1) > img:nth-child(1)";

        return linkToBytes(client, site, parsePageBody(client, site, url,
            document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getUserFriendly(final HttpClient client) throws IOException {
        final String site = "UserFriendly";
        final String url = "http://www.userfriendly.org/";
        final String imgPrefix = "http://www.userfriendly.org/cartoons/archives/";
        final String cssSelector = "img[alt=Latest Strip]";

        return linkToBytes(client, site, parsePageBody(client, site, url,
            document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getDilbert(final HttpClient client) throws IOException {
        final String site = "Dilbert";
        final String url = "https://dilbert.com/";
        final String imgPrefix = "//assets.amuniversal.com/";
        final String cssSelector = "img.img-responsive.img-comic";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> "https:" + toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getOglaf(final HttpClient client) throws IOException {
        final String site = "Oglaf";
        final String url = "https://www.oglaf.com/";
        final String imgPrefix = "https://media.oglaf.com/comic/";
        final String cssSelector = "#strip";

        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Cookie", "AGE_CONFIRMED=yes")
            .GET().build();

        return linkToBytes(client, site, parsePageBody(client, site, request,
            document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getXkcd(final HttpClient client) throws IOException {
        final String site = "XKCD";
        final String url = "https://xkcd.com/";
        final String imgPrefix = "//imgs.xkcd.com/comics/";
        final String cssSelector = "#comic > img";

        return linkToBytes(client, site, parsePageBody(client, site, url,
            document -> "https:" + toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getKopozky(final HttpClient client) throws IOException {
        final String site = "Kopozky";
        final String url = "https://kopozky.net/";
        final String imgPrefix = "img/";
        final String cssSelector = "figure";

        return linkToBytes(client, site, parsePageBody(client, site, url,
            document -> thumbToReal(url + toImageLink(document, cssSelector, "data-thumb", imgPrefix))));
    }

    private static String thumbToReal(final String thumbLink) {
        if (thumbLink.endsWith(".t.png")) return thumbLink.substring(0, thumbLink.length()-6) + ".png";
        return thumbLink;
    }

    public static byte[] getOkCancel(final HttpClient client) throws IOException {
        final String site = "OkCancel";
        final int comicId = randomInt(178);
        final String url = "http://okcancel.com/comic/"+comicId+".html";
        final String imgPrefix = "http://okcancel.com/strips/";
        final String cssSelector = "#comic > a > img";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    // FIXME penny arcade download
    // this code works in the sense that it gets the image, however the process fails
    // to end and hangs forever. I'm guessing some thread is still busy doing stuff, but
    // I don't know where or why
    public static byte[] getPennyArcade(final HttpClient client) throws IOException {
        final String site = "PennyArcade";
        final String url = "https://www.penny-arcade.com/comic";
        final String imgPrefix = "https://photos.smugmug.com/photos/";
        final String cssSelector = "#comicFrame > img";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getCommitStrip(final HttpClient client) throws IOException {
        final String site = "CommitStrip";
        final int comicId = randomInt(59)+2;
        final String pagesUrl = "http://www.commitstrip.com/en/page/"+comicId+"/";

        final String url = parsePageBody(client, site, pagesUrl, document -> {
            Elements excerpts = document.select("div.excerpt");
            Element element = excerpts.get(randomInt(excerpts.size())).selectFirst("a");
            if (element == null) throw new IOException("Couldn't find a page link for " + site);
            final String href = element.attr("href");
            if (href == null || href.isEmpty() || !href.startsWith("http://www.commitstrip.com/en/"))
                throw new IOException("Couldn't find a page link for " + site);
            return href;
        });

        final String imgPrefix = "https://www.commitstrip.com/wp-content/uploads/";
        final String cssSelector = "div.entry-content > p > img";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    // FIXME monkey user download
    // this code works in the sense that it gets the image, however the process fails
    // to end and hangs forever. I'm guessing some thread is still busy doing stuff, but
    // I don't know where or why
    public static byte[] getMonkeyUser(final HttpClient client) throws IOException {
        final String site = "MonkeyUser";
        final String url = "https://www.monkeyuser.com/";
        final String imgPrefix = "https://www.monkeyuser.com/assets/images/";
        final String cssSelector = "div.content > p > img";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getAbstruseGoose(final HttpClient client) throws IOException {
        final String site = "AbstruseGoose";
        final String url = "https://abstrusegoose.com/";
        final String imgPrefix = "https://abstrusegoose.com/strips/";
        final String cssSelector = "section img";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    public static byte[] getPhdComics(final HttpClient client) throws IOException {
        final String site = "PhdComics";
        final String url = "http://phdcomics.com/";
        final String imgPrefix = "http://www.phdcomics.com/comics/archive/";
        final String cssSelector = "#comic";

        return linkToBytes(client, site, parsePageBody(client, site, url,
                document -> toImageLink(document, cssSelector, "src", imgPrefix)));
    }

    private static String toImageLink(final Document page, final String cssSelector, final String attribute, final String imgPrefix) throws IOException {
        final Element img = page.selectFirst(cssSelector);
        if (img == null) throw new IOException("Couldn't find the webcomic image");
        final String link = img.attr(attribute);
        if (link != null && !link.isEmpty() && link.startsWith(imgPrefix)) return link;
        throw new IOException("Couldn't find the webcomic image");
    }

    private static HttpRequest newUrlRequest(final String url) {
        return HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    }

    private static String parsePageBody(final HttpClient client, final String site, final String url
            , final PageParser parser) throws IOException {
        return parsePageBody(client, site, newUrlRequest(url), parser);
    }
    private static String parsePageBody(final HttpClient client, final String site, final HttpRequest request, final PageParser parser) throws IOException {
        try {
            final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() < 200 && response.statusCode() > 299) throw new IOException(site + " returned wrong status code " + response.statusCode());
            final String body = response.body();
            if (body == null) throw new IOException(site + " returned empty body");
            return parser.extractImageLink(Jsoup.parse(body));
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while downloading " + request.uri());
        }
    }

    private static byte[] linkToBytes(final HttpClient client, final String site, final String url) throws IOException {
        try {
            final HttpResponse<byte[]> response = client.send(newUrlRequest(url), BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() > 299) throw new IOException(site + " returned wrong status code " + response.statusCode());
            final byte[] body = response.body();
            if (body == null) throw new IOException(site + " returned empty body");
            return body;
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while downloading " + url);
        }
    }
}
