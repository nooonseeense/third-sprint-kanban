package http_service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {
    private final URL url;
    private final HttpClient client;
    private String apiToken;

    public KVTaskClient(URL url) {
        this.url = url;
        client = HttpClient.newHttpClient();
        URI uri = URI.create(url + ":" + KVServer.PORT + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            apiToken = new String(response.body().getBytes(), UTF_8);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + ":" + KVServer.PORT + "/save" + key + "/?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .GET()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + ":" + KVServer.PORT + "/load" + key + "/?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
