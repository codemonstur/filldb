import okhttp3.OkHttpClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static filldb.generators.Images.getPhdComics;

public class TestClient {

    public static void main(final String... args) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final byte[] image = getPhdComics(client);
        Files.write(Paths.get("temp-img.jpg"), image);
    }

}
