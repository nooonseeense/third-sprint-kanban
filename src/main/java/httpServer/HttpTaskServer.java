package httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;
    private TaskManager manager;

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefaultTask());
        server.start();
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefaultTask());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/v1/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();

            switch (requestMethod) {
                case "GET":

                case "DELETE":

                default:
                    exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.\n"
                    + "Проверьте, пожалуйста, адрес и повторите попытку.");
        } finally {
            exchange.close();
        }
    }

    public void start() {
        System.out.println("Started TaskServer: " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped in PORT: " + PORT);
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

//    private int returnMessageCode(HttpResponse<String> response) {
//        int responseCode = response.statusCode();
//
//        switch (responseCode) {
//            case 200:
//                System.out.println("Сервер успешно обработал запрос."
//                        + " Код состояния: " + responseCode);
//                return 200;
//            case 400:
//                System.out.println("В запросе содержится ошибка. Проверьте параметры и повторите запрос."
//                        + " Код состояния: " + responseCode);
//                return 400;
//            case 404:
//                System.out.println("По указанному адресу нет ресурса. Проверьте URL-адрес ресурса и повторите запрос."
//                        + " Код состояния: " + responseCode);
//                return 404;
//            case 500:
//                System.out.println("На стороне сервера произошла непредвиденная ошибка."
//                        + " Код состояния: " + responseCode);
//                return 500;
//            default:
//                System.out.println("Сервер временно недоступен. Попробуйте повторить запрос позже."
//                        + " Код состояния: " + responseCode);
//                return 503;
//        }
//    }

//    private void requestHandler(HttpExchange exchange) {
//        String path = exchange.getRequestURI().getPath();
//        String requestMethod = exchange.getRequestMethod();
//
//        switch (requestMethod) {
//            case "GET":
//
//
//            case "POST":
//
//            case "DELETE":
//
//            default:
//
//        }
//    }
}
