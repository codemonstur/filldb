package filldb.generators;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static filldb.core.Constants.LORUM_IPSUM;
import static filldb.core.HTTP.toFormData;
import static filldb.core.Util.randomItemFrom;
import static java.net.http.HttpClient.newHttpClient;
import static java.util.Arrays.asList;

public enum RemoteLorums {;

    public static Supplier<String> newRandomRemoteIpsumGenerator() {
        final HttpClient client = newHttpClient();
        final List<LorumIpsumGenerator> generators =
            asList(newBibleIpsum(client), newBluthIpsum(client), newCatIpsum(client)
                , newCipIpsum(client), newDalaIpsum(client), newFuckIpsum(client)
                , newHillBillyIpsum(client), newITCrowdIpsum(client), newLegalIpsum(client)
                , newOfficePhraseIpsum(client), newOfficeClientPhraseIpsum(client)
                , newCartoonAmericanDadIpsum(client), newCartoonDilbertIpsum(client)
                , newCartoonFamilyGuyIpsum(client), newCartoonFuturamaIpsum(client)
                , newCartoonSimpsonsIpsum(client), newSciFiBabylon5Ipsum(client)
                , newSciFiBattlestarGallacticaIpsum(client), newSciFiFireflyIpsum(client)
                , newSciFiStargateIpsum(client), newSciFiStartrekIpsum(client));

        return () -> {
            int tries = 3;
            while (tries > 0) {
                try {
                    return randomItemFrom(generators).getIpsum();
                } catch (IOException e) {
                    /* Remote generators can fail so we just try a bit and fall
                     * back to regular Ipsum if not possible
                     */
                }
                tries--;
            }
            return LORUM_IPSUM;
        };
    }

    public static LorumIpsumGenerator newBibleIpsum(final HttpClient client) {
        return () -> toDocument("BibleIpsum", client,
            newBibleIpsumRequest()).select("#paragraphe").text();
    }

    private static HttpRequest newBibleIpsumRequest() {
        final var formFields = Map.of
            ("choix", "phrase"
            ,"nombre", "25"
            ,"aleatoire", "1"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://bibleipsum.free.fr/index.php"))
            .header("Accept-Language", "en-GB,en;q=0.5")
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newBluthIpsum(final HttpClient client) {
        return () -> toDocument("BluthIpsum", client,
            newBluthIpsumRequest()).select("#main").text();
    }

    private static HttpRequest newBluthIpsumRequest() {
        return HttpRequest.newBuilder()
            .uri(URI.create("https://www.bluthipsum.com"))
            .GET().build();
    }

    public static LorumIpsumGenerator newCatIpsum(final HttpClient client) {
        return () -> toDocument("CatIpsum", client,
            newCatIpsumRequest()).selectFirst(".body_text").text();
    }

    private static HttpRequest newCatIpsumRequest() {
        final var formFields = Map.of
            ("result_type", "phrase"
            ,"par_count", "10"
            ,"Generate", "Make Muffins"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://www.catipsum.com/index.php"))
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newCipIpsum(final HttpClient client) {
        return () -> toDocument("CipIpsum", client,
            newCipIpsumRequest()).text();
    }

    private static HttpRequest newCipIpsumRequest() {
        final var formFields = Map.of("p", "5");

        return HttpRequest.newBuilder()
            .uri(URI.create("https://www.cipsum.com/includes/getIpsum.php"))
            .header("Accept", "*/*")
            .header("Accept-Language", "en-GB,en;q=0.5")
            .header("Referer","https://www.cipsum.com/")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Cookie","SPSI=eefc9f2c73aff9789bef833f69658370; spcsrf=d346bcc366828599ed3e328a14599ccf; UTGv2=h4b4a959e5ddeebc217a1b9b8fc1c511bf92; sp_lit=aTKsb5Hr2jUw61b/r2gObA==; PRLST=wp; adOtr=9cTTecf72a3;")
            .header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:69.0) Gecko/20100101 Firefox/69.0")
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newDalaIpsum(final HttpClient client) {
        return () -> toDocument("DalaIpsum", client,
            newDalaIpsumRequest()).text();
    }

    private static HttpRequest newDalaIpsumRequest() {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://dalaipsum.com/ipsum.php?number=3&divider=p"))
            .GET().build();
    }

    public static LorumIpsumGenerator newFuckIpsum(final HttpClient client) {
        return () -> toDocument("FuckIpsum", client,
            newFuckIpsumRequest()).selectFirst("#ta").text();
    }

    private static HttpRequest newFuckIpsumRequest() {
        final var formFields = Map.of
            ("filter[]", "[5,4,3,2,1]"
            ,"tags", ""
            ,"limit", "3"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://fucklorem.com/"))
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newHillBillyIpsum(final HttpClient client) {
        return () -> toDocument("HillBillyIpsum", client,
            newHillBillyIpsumRequest()).selectFirst("#clip").text();
    }

    private static HttpRequest newHillBillyIpsumRequest() {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://hillbillyipsum.com/?p=4"))
            .GET().build();
    }

    public static LorumIpsumGenerator newITCrowdIpsum(final HttpClient client) {
        return () -> toDocument("ITCrowdIpsum", client,
            newITCrowdIpsumRequest()).select("section > p").text();
    }

    private static HttpRequest newITCrowdIpsumRequest() {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://www.itcrowdipsum.com/"))
            .GET().build();
    }

    public static LorumIpsumGenerator newLegalIpsum(final HttpClient client) {
        return () -> toDocument("LegalIpsum", client,
            newLegalIpsumRequest()).select(".main-content > p").text();
    }

    private static HttpRequest newLegalIpsumRequest() {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://legalipsum.com/?count=1"))
            .GET().build();
    }

    public static LorumIpsumGenerator newOfficePhraseIpsum(final HttpClient client) {
        return () -> toDocument("OfficeIpsum", client,
            newOfficeIpsumRequest()).selectFirst(".body_text").text();
    }

    private static HttpRequest newOfficeIpsumRequest() {
        final var formFields = Map.of
            ("result_type", "phrase"
            ,"par_count", "1"
            ,"Generate", "Productize Deliverables"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://officeipsum.com/index.php"))
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newOfficeClientPhraseIpsum(final HttpClient client) {
        return () -> toDocument("OfficeClientIpsum", client,
            newOfficeClientPhraseIpsumRequest()).selectFirst(".body_text").text();
    }

    private static HttpRequest newOfficeClientPhraseIpsumRequest() {
        final var formFields = Map.of
            ("result_type", "clientphrases"
            ,"par_count", "1"
            ,"Generate", "Productize Deliverables"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://officeipsum.com/index.php"))
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newCartoonDilbertIpsum(final HttpClient client) {
        return newCartoonIpsum(client, "CartoonDilbertIpsum", "dilbert");
    }
    public static LorumIpsumGenerator newCartoonAmericanDadIpsum(final HttpClient client) {
        return newCartoonIpsum(client, "CartoonAmericanDadIpsum", "american dad");
    }
    public static LorumIpsumGenerator newCartoonFamilyGuyIpsum(final HttpClient client) {
        return newCartoonIpsum(client, "CartoonFamilyGuyIpsum", "family guy");
    }
    public static LorumIpsumGenerator newCartoonFuturamaIpsum(final HttpClient client) {
        return newCartoonIpsum(client, "CartoonFuturamaIpsum", "futurama");
    }
    public static LorumIpsumGenerator newCartoonSimpsonsIpsum(final HttpClient client) {
        return newCartoonIpsum(client, "CartoonSimpsonsIpsum", "simpsons");
    }
    private static LorumIpsumGenerator newCartoonIpsum(final HttpClient client, final String name, final String option) {
        return () -> toDocument(name, client, newCartoonIpsumRequest(option)).selectFirst("textarea[class=textbox]").text();
    }

    private static HttpRequest newCartoonIpsumRequest(final String name) {
        final var formFields = Map.of
            ("show", name
            ,"format", "text"
            ,"quotes", "10"
            ,"submit", "Lorem Ipsum!"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://www.tvipsum.org/cartoon.php"))
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newSciFiBabylon5Ipsum(final HttpClient client) {
        return newSciFiIpsum(client, "SciFiBabylon5Ipsum", "babylon5");
    }
    public static LorumIpsumGenerator newSciFiBattlestarGallacticaIpsum(final HttpClient client) {
        return newSciFiIpsum(client, "SciFiBattlestarGallacticaIpsum", "battlestargalactica");
    }
    public static LorumIpsumGenerator newSciFiFireflyIpsum(final HttpClient client) {
        return newSciFiIpsum(client, "SciFiFireflyIpsum", "firefly");
    }
    public static LorumIpsumGenerator newSciFiStargateIpsum(final HttpClient client) {
        return newSciFiIpsum(client, "SciFiStargateIpsum", "stargate");
    }
    public static LorumIpsumGenerator newSciFiStartrekIpsum(final HttpClient client) {
        return newSciFiIpsum(client, "SciFiStartrekIpsum", "startrek");
    }
    private static LorumIpsumGenerator newSciFiIpsum(final HttpClient client, final String name, final String option) {
        return () -> toDocument(name, client, newSciFiIpsumRequest(option)).selectFirst("textarea[class=textbox]").text();
    }

    private static HttpRequest newSciFiIpsumRequest(final String name) {
        final var formFields = Map.of
            ("show", name
            ,"format", "text"
            ,"quotes", "10"
            ,"submit", "Lorem Ipsum!"
            );

        return HttpRequest.newBuilder()
            .uri(URI.create("http://www.tvipsum.org/scifi.php"))
            .POST(BodyPublishers.ofString(toFormData(formFields)))
            .build();
    }

    public static LorumIpsumGenerator newWikiIpsum(final HttpClient client) {
        return () -> toDocument("WikiIpsum", client,
            newWikiIpsumRequest()).text();
    }

    private static HttpRequest newWikiIpsumRequest() {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://www.wikipsum.com/data.php?count=5&length=2&wiki=false"))
            .GET().build();
    }

    private static Document toDocument(final String site, final HttpClient client, final HttpRequest request) throws IOException {
        try {
            final HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() > 299) throw new IOException(site+" did not respond");
            final String body = response.body();
            if (body == null) throw new IOException(site + " returned an empty response");
            return Jsoup.parse(body);
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while downloading " + request.uri());
        }
    }
}
