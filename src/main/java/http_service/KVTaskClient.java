package http_service;

import exceptions.HttpServerConnectException;

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
        register();
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + ":" + KVServer.PORT + "/save/" + key + "/?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new HttpServerConnectException();
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpServerConnectException();
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + ":" + KVServer.PORT + "/load/" + key + "/?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400) {
                return response.body();
            }
            if (response.statusCode() != 200) {
                throw new HttpServerConnectException();
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new HttpServerConnectException();
        }
    }

    private void register() {
        URI uri = URI.create(url + ":" + KVServer.PORT + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new HttpServerConnectException();
            }
            apiToken = new String(response.body().getBytes(), UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new HttpServerConnectException();
        }
    }
}
