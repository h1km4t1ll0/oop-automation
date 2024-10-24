package ru.nsu.dolgov.taskchecker.models.results;

import ru.nsu.dolgov.taskchecker.models.core.Student;

import java.util.List;

/**
 * Class used to describe the result got from the task runner.
 */
public class TaskRunnerResult {
    public Student student;
    public List<TaskTestResult> tasksResults;
}
