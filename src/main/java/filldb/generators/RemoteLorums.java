package filldb.generators;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static filldb.core.Constants.LORUM_IPSUM;
import static filldb.core.Util.randomItemFrom;
import static java.util.Arrays.asList;
import static okhttp3.MultipartBody.Builder;
import static okhttp3.MultipartBody.FORM;

public enum RemoteLorums {;

    public static Supplier<String> newRandomRemoteIpsumGenerator() {
        final OkHttpClient client = new OkHttpClient();
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

    public static LorumIpsumGenerator newBibleIpsum(final OkHttpClient client) {
        return () -> toDocument("BibleIpsum", client,
            newBibleIpsumRequest()).select("#paragraphe").text();
    }

    private static Request newBibleIpsumRequest() {
        return new Request.Builder()
            .url("http://bibleipsum.free.fr/index.php")
            .header("Accept-Language", "en-GB,en;q=0.5")
            .post(new Builder()
                .setType(FORM)
                .addFormDataPart("choix", "phrase")
                .addFormDataPart("nombre", "25")
                .addFormDataPart("aleatoire", "1")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newBluthIpsum(final OkHttpClient client) {
        return () -> toDocument("BluthIpsum", client,
            newBluthIpsumRequest()).select("#main").text();
    }

    private static Request newBluthIpsumRequest() {
        return new Request.Builder()
            .url("https://www.bluthipsum.com")
            .get()
            .build();
    }

    public static LorumIpsumGenerator newCatIpsum(final OkHttpClient client) {
        return () -> toDocument("CatIpsum", client,
            newCatIpsumRequest()).selectFirst(".body_text").text();
    }

    private static Request newCatIpsumRequest() {
        return new Request.Builder()
            .url("http://www.catipsum.com/index.php")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("result_type", "phrase")
                .addFormDataPart("par_count", "10")
                .addFormDataPart("Generate", "Make Muffins")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newCipIpsum(final OkHttpClient client) {
        return () -> toDocument("CipIpsum", client,
            newCipIpsumRequest()).text();
    }

    private static Request newCipIpsumRequest() {
        return new Request.Builder()
            .url("https://www.cipsum.com/includes/getIpsum.php")
            .header("Accept", "*/*")
            .header("Accept-Language", "en-GB,en;q=0.5")
            .header("Referer","https://www.cipsum.com/")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Cookie","SPSI=eefc9f2c73aff9789bef833f69658370; spcsrf=d346bcc366828599ed3e328a14599ccf; UTGv2=h4b4a959e5ddeebc217a1b9b8fc1c511bf92; sp_lit=aTKsb5Hr2jUw61b/r2gObA==; PRLST=wp; adOtr=9cTTecf72a3;")
            .header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:69.0) Gecko/20100101 Firefox/69.0")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("p", "5")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newDalaIpsum(final OkHttpClient client) {
        return () -> toDocument("DalaIpsum", client,
            newDalaIpsumRequest()).text();
    }

    private static Request newDalaIpsumRequest() {
        return new Request.Builder()
            .url("http://dalaipsum.com/ipsum.php?number=3&divider=p")
            .get()
            .build();
    }

    public static LorumIpsumGenerator newFuckIpsum(final OkHttpClient client) {
        return () -> toDocument("FuckIpsum", client,
            newFuckIpsumRequest()).selectFirst("#ta").text();
    }

    private static Request newFuckIpsumRequest() {
        return new Request.Builder()
            .url("http://fucklorem.com/")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("filter[]", "[5,4,3,2,1]")
                .addFormDataPart("tags", "")
                .addFormDataPart("limit", "3")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newHillBillyIpsum(final OkHttpClient client) {
        return () -> toDocument("HillBillyIpsum", client,
            newHillBillyIpsumRequest()).selectFirst("#clip").text();
    }

    private static Request newHillBillyIpsumRequest() {
        return new Request.Builder()
            .url("http://hillbillyipsum.com/?p=4")
            .get()
            .build();
    }

    public static LorumIpsumGenerator newITCrowdIpsum(final OkHttpClient client) {
        return () -> toDocument("ITCrowdIpsum", client,
            newITCrowdIpsumRequest()).select("section > p").text();
    }

    private static Request newITCrowdIpsumRequest() {
        return new Request.Builder()
            .url("http://www.itcrowdipsum.com/")
            .get()
            .build();
    }

    public static LorumIpsumGenerator newLegalIpsum(final OkHttpClient client) {
        return () -> toDocument("LegalIpsum", client,
            newLegalIpsumRequest()).select(".main-content > p").text();
    }

    private static Request newLegalIpsumRequest() {
        return new Request.Builder()
            .url("http://legalipsum.com/?count=1")
            .get()
            .build();
    }

    public static LorumIpsumGenerator newOfficePhraseIpsum(final OkHttpClient client) {
        return () -> toDocument("OfficeIpsum", client,
            newOfficeIpsumRequest()).selectFirst(".body_text").text();
    }

    private static Request newOfficeIpsumRequest() {
        return new Request.Builder()
            .url("http://officeipsum.com/index.php")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("result_type", "phrase")
                .addFormDataPart("par_count", "1")
                .addFormDataPart("Generate", "Productize Deliverables")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newOfficeClientPhraseIpsum(final OkHttpClient client) {
        return () -> toDocument("OfficeClientIpsum", client,
            newOfficeClientPhraseIpsumRequest()).selectFirst(".body_text").text();
    }

    private static Request newOfficeClientPhraseIpsumRequest() {
        return new Request.Builder()
            .url("http://officeipsum.com/index.php")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("result_type", "clientphrases")
                .addFormDataPart("par_count", "1")
                .addFormDataPart("Generate", "Productize Deliverables")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newCartoonDilbertIpsum(final OkHttpClient client) {
        return newCartoonIpsum(client, "CartoonDilbertIpsum", "dilbert");
    }
    public static LorumIpsumGenerator newCartoonAmericanDadIpsum(final OkHttpClient client) {
        return newCartoonIpsum(client, "CartoonAmericanDadIpsum", "american dad");
    }
    public static LorumIpsumGenerator newCartoonFamilyGuyIpsum(final OkHttpClient client) {
        return newCartoonIpsum(client, "CartoonFamilyGuyIpsum", "family guy");
    }
    public static LorumIpsumGenerator newCartoonFuturamaIpsum(final OkHttpClient client) {
        return newCartoonIpsum(client, "CartoonFuturamaIpsum", "futurama");
    }
    public static LorumIpsumGenerator newCartoonSimpsonsIpsum(final OkHttpClient client) {
        return newCartoonIpsum(client, "CartoonSimpsonsIpsum", "simpsons");
    }
    private static LorumIpsumGenerator newCartoonIpsum(final OkHttpClient client, final String name, final String option) {
        return () -> toDocument(name, client, newCartoonIpsumRequest(option)).selectFirst("textarea[class=textbox]").text();
    }

    private static Request newCartoonIpsumRequest(final String name) {
        return new Request.Builder()
            .url("http://www.tvipsum.org/cartoon.php")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("show", name)
                .addFormDataPart("format", "text")
                .addFormDataPart("quotes", "10")
                .addFormDataPart("submit", "Lorem Ipsum!")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newSciFiBabylon5Ipsum(final OkHttpClient client) {
        return newSciFiIpsum(client, "SciFiBabylon5Ipsum", "babylon5");
    }
    public static LorumIpsumGenerator newSciFiBattlestarGallacticaIpsum(final OkHttpClient client) {
        return newSciFiIpsum(client, "SciFiBattlestarGallacticaIpsum", "battlestargalactica");
    }
    public static LorumIpsumGenerator newSciFiFireflyIpsum(final OkHttpClient client) {
        return newSciFiIpsum(client, "SciFiFireflyIpsum", "firefly");
    }
    public static LorumIpsumGenerator newSciFiStargateIpsum(final OkHttpClient client) {
        return newSciFiIpsum(client, "SciFiStargateIpsum", "stargate");
    }
    public static LorumIpsumGenerator newSciFiStartrekIpsum(final OkHttpClient client) {
        return newSciFiIpsum(client, "SciFiStartrekIpsum", "startrek");
    }
    private static LorumIpsumGenerator newSciFiIpsum(final OkHttpClient client, final String name, final String option) {
        return () -> toDocument(name, client, newSciFiIpsumRequest(option)).selectFirst("textarea[class=textbox]").text();
    }

    private static Request newSciFiIpsumRequest(final String name) {
        return new Request.Builder()
            .url("http://www.tvipsum.org/scifi.php")
            .post(new MultipartBody.Builder()
                .setType(FORM)
                .addFormDataPart("show", name)
                .addFormDataPart("format", "text")
                .addFormDataPart("quotes", "10")
                .addFormDataPart("submit", "Lorem Ipsum!")
                .build())
            .build();
    }

    public static LorumIpsumGenerator newWikiIpsum(final OkHttpClient client) {
        return () -> toDocument("WikiIpsum", client,
            newWikiIpsumRequest()).text();
    }

    private static Request newWikiIpsumRequest() {
        return new Request.Builder()
            .url("http://www.wikipsum.com/data.php?count=5&length=2&wiki=false")
            .get()
            .build();
    }

    private static Document toDocument(final String site, final OkHttpClient client, final Request request) throws IOException {
        try (final Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException(site+" did not respond");
            final ResponseBody body = response.body();
            if (body == null) throw new IOException(site+ " returned an empty response");
            return Jsoup.parse(body.string());
        }
    }
}
