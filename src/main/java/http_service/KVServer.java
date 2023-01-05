package http_service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("<SYSTEM>: Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());

                if (key.isEmpty()) {
                    System.out.println("<SYSTEM>: Key для получения пустой. Key указывается по пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if (data.containsKey(key)) {
                    System.out.println("<SYSTEM>: Значение для ключа " + key + " успешно отправлено!");
                    sendText(h, data.get(key));
                } else {
                    System.out.println("<SYSTEM>: Значение для ключа " + key + " отсутствует!");
                    h.sendResponseHeaders(400, 0);
                }
            } else {
                System.out.println("<SYSTEM>: /load ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("<SYSTEM>: Запрос не авторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());

                if (key.isEmpty()) {
                    System.out.println("<SYSTEM>: Key для сохранения пустой. Key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);

                if (value.isEmpty()) {
                    System.out.println("<SYSTEM>: Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("<SYSTEM>: Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("<SYSTEM>: /save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("<SYSTEM>:/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("<SYSTEM>: Started KVServer in PORT: " + PORT);
        System.out.println("<SYSTEM>: http://localhost:" + PORT + "/");
        System.out.println("<SYSTEM>: API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("<SYSTEM>: Server stopped in PORT: " + PORT);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}