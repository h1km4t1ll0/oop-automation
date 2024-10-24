package ru.nsu.dolgov.taskchecker.models.results;

import ru.nsu.dolgov.taskchecker.models.core.Task;

/**
 * Class used to describe the result of checking tasks'
 * build, javadoc, etc.
 */
public class TaskTestResult {
    public Boolean build = false;
    public Boolean javadoc = false;
    public Integer passedTests = 0;
    public Integer totalTests = 0;
    public Integer failedTests = 0;
    public Integer ignoredTests = 0;
    public DeadlinesCheckResult deadlinesCheckResult = null;
    public Task task = null;
    public float points = -0.5F;
}
