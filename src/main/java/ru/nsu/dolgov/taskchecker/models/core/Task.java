package ru.nsu.dolgov.taskchecker.models.core;

import java.time.LocalDate;

public class Task {
    public String id;
    public String title;
    public Integer points;
    public LocalDate softDeadline;
    public LocalDate hardDeadline;

    void id(String id) {
        this.id = id;
    }

    void title(String title) {
        this.title = title;
    }

    void points(int points) {
        this.points = points;
    }

    void softDeadline(String softDeadline) {
        this.softDeadline = LocalDate.parse(softDeadline);
    }

    void hardDeadline(String hardDeadline) {
        this.hardDeadline = LocalDate.parse(hardDeadline);
    }
}
