package ru.nsu.dolgov.taskchecker.models.core;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public String name;
    public List<Student> students = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }
}
