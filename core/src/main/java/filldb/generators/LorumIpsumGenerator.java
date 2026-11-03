package filldb.generators;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Map;

import static filldb.generators.RemoteLorums.*;
import static java.net.http.HttpClient.newHttpClient;
import static java.util.Map.entry;

public interface LorumIpsumGenerator {

    String getIpsum() throws IOException;

    static Map<String, LorumIpsumGenerator> newLorumIpsumGenerators() {
        final HttpClient client = newHttpClient();
        return Map.ofEntries
            ( entry("bible", newBibleIpsum(client))
            , entry("bluth", newBluthIpsum(client))
            , entry("cat", newCatIpsum(client))
            , entry("corporate", newCipIpsum(client))
            , entry("dalai lama", newDalaIpsum(client))
            , entry("fuck", newFuckIpsum(client))
            , entry("hillbilly", newHillBillyIpsum(client))
            , entry("it-crowd", newITCrowdIpsum(client))
            , entry("legal", newLegalIpsum(client))
            , entry("office", newOfficePhraseIpsum(client))
            , entry("client", newOfficeClientPhraseIpsum(client))
            , entry("american dad", newCartoonAmericanDadIpsum(client))
            , entry("dilbert", newCartoonDilbertIpsum(client))
            , entry("family guy", newCartoonFamilyGuyIpsum(client))
            , entry("futurama", newCartoonFuturamaIpsum(client))
            , entry("simpsons", newCartoonSimpsonsIpsum(client))
            , entry("babylon5", newSciFiBabylon5Ipsum(client))
            , entry("battlestar galactica", newSciFiBattlestarGallacticaIpsum(client))
            , entry("firefly", newSciFiFireflyIpsum(client))
            , entry("stargate", newSciFiStargateIpsum(client))
            , entry("startrek", newSciFiStartrekIpsum(client))
            );
    }
}
