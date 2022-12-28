package service;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import constants.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;

public class TaskAdapter implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        String taskType = jsonElement.getAsJsonObject().get("type").getAsString();

        switch (taskType) {
            case "TASK":
                return jsonDeserializationContext.deserialize(jsonElement, Task.class);
            case "EPIC":
                return jsonDeserializationContext.deserialize(jsonElement, Epic.class);
            case "SUBTASK":
                return jsonDeserializationContext.deserialize(jsonElement, Subtask.class);
            default:
                throw new IllegalArgumentException();
        }
    }
}
