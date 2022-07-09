package tasks;

import constants.Status;
import java.util.ArrayList;

public class Epic extends Task {// Хранятся просто номера

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }
}
