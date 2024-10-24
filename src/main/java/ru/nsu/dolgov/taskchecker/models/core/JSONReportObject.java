package ru.nsu.dolgov.taskchecker.models.core;

import ru.nsu.dolgov.taskchecker.models.results.TaskRunnerResult;

import java.util.List;

public class JSONReportObject {
    public List<TaskRunnerResult> taskRunnerResults;
    public List<Task> tasks;
    public List<Group> groups;
    public AdditionalSettings additionalSettings;
    public List<Student> students;
    public String plagiarismReportPath = null;
}
