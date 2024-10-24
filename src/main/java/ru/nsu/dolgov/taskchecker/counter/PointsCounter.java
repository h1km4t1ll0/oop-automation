package ru.nsu.dolgov.taskchecker.counter;


import ru.nsu.dolgov.taskchecker.models.results.TaskTestResult;

/**
 * Utility class that implements initial count of the tasks.
 */
public class PointsCounter {
    /**
     * Method that counts points for the task according to the
     * data provided.
     *
     * @param taskTestResult result of the task check.
     * @return float, points for the task.
     */
    public static float countTaskPoints(TaskTestResult taskTestResult) {
        float totalPoints = -0.5F;

        if (taskTestResult.failedTests > 0) {
            return 0;
        }

        if (taskTestResult.deadlinesCheckResult.hardDeadlinePass) {
            totalPoints += 0.5F;
        }

        if (taskTestResult.deadlinesCheckResult.softDeadlinePass) {
            totalPoints += 0.5F;
        }

        return totalPoints;
    }
}
