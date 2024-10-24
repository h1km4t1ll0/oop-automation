package ru.nsu.dolgov.taskchecker.models.core;

import ru.nsu.dolgov.taskchecker.models.results.CommitsCheckResult;

public class Student {
    public String studentName;
    public String repository;
    public String nickname;
    public String groupName = null;
    public CommitsCheckResult commitsCheckResult = null;

    public void studentName(String name) {
        this.studentName = name;
    }

    public void repository(String repository) {
        this.repository = repository;
    }

    public void nickname(String nickname) {
        this.nickname = nickname;
    }
}
