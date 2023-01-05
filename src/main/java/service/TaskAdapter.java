package service;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import tasks.Task;

import java.lang.reflect.Type;

public class TaskAdapter implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        return jsonDeserializationContext.deserialize(jsonElement, Task.class);
    }
}
