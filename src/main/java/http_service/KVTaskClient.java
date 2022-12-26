package http_service;

import java.net.URL;

public class KVTaskClient { // Этот класс будет использоваться HttpTaskManager
    private final URL url;

    public KVTaskClient(URL url) {
        this.url = url;
        // TODO Конструктор принимает URL к серверу хранилища и регистрируется. При регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером.
    }

    public void put(String key, String json) { // 1. Метод будет вызываться с HttpTaskManager
        // TODO Должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
    }

    public String load(String key) { // Метод будет вызываться с HttpTaskManager
        // TODO Должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
        return key;
    }
}
