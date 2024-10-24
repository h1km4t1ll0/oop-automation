package ru.nsu.dolgov.taskchecker.checker;

import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.counter.PointsCounter;
import ru.nsu.dolgov.taskchecker.models.core.Student;
import ru.nsu.dolgov.taskchecker.models.core.Task;
import ru.nsu.dolgov.taskchecker.models.results.TaskRunnerResult;
import ru.nsu.dolgov.taskchecker.models.results.TaskTestResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.nsu.dolgov.taskchecker.Logger.LogLevel.ERROR;
import static ru.nsu.dolgov.taskchecker.Logger.LogLevel.INFO;


/**
 * Class that is used to check tasks.
 */
public class TaskRunner {
    private final String currentStudentPath;
    private final Student student;
    private final List<Task> tasksToCheck;
    private final GradleConnector connector = GradleConnector.newConnector();

    /**
     * Constructor. Initializes student, his repository
     * path and tasks that need to be processed.
     *
     * @param student             student that will be processed.
     * @param allRepositoriesPath students' repository with the submissions.
     * @param tasksToCheck        tasks that will be processed.
     */
    public TaskRunner(
            Student student,
            String allRepositoriesPath,
            List<Task> tasksToCheck
    ) {
        this.student = student;
        this.currentStudentPath = allRepositoriesPath + '/' + this.student.nickname + '/';
        this.tasksToCheck = tasksToCheck;
    }

    /**
     * Method that processes all th tasks provided.
     *
     * @return TaskRunnerResult with all the metrics collected from the repository.
     */
    public TaskRunnerResult checkTasks() {
        TaskRunnerResult taskRunnerResult = new TaskRunnerResult();
        List<TaskTestResult> taskTestResultMap = new ArrayList<>();
        for (Task task : this.tasksToCheck) {
            TaskTestResult checkResult = new TaskTestResult();
            checkResult.task = task;

            this.connectToGradleProject(task.id);
            Logger.log(INFO, "Connected to the project " + this.currentStudentPath + task.id);

            Boolean buildResult = this.checkBuild();
            if (!buildResult) {
                taskTestResultMap.add(checkResult);
                continue;
            }
            checkResult.build = true;
            this.checkTests(checkResult, task.id);
            checkResult.javadoc = this.testJavadoc(task.id);
            checkResult.deadlinesCheckResult = GithubAPI.checkDeadlines(
                    task,
                    this.currentStudentPath + task.id
            );

            checkResult.points = PointsCounter.countTaskPoints(checkResult);

            taskTestResultMap.add(checkResult);
        }

        taskRunnerResult.tasksResults = taskTestResultMap;
        taskRunnerResult.student = this.student;

        return taskRunnerResult;
    }

    /**
     * Method that calls Gradle to check whether task can build or not.
     *
     * @return true, if task is successfully build, false otherwise.
     */
    private Boolean checkBuild() {
        return this.executeGradleCommand("build");
    }

    /**
     * Method that starts all tests and parses XML with the tests result.
     *
     * @param taskTestResult link to the TaskTestResult object that is
     *                       filled with the data during the check.
     * @param taskName       name of the task used to get the appropriate folder.
     */
    private void checkTests(
            TaskTestResult taskTestResult,
            String taskName
    ) {
        try {
            this.executeGradleCommand("test");
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory
                    .newDocumentBuilder();
            String xmlFile = Objects.requireNonNull(
                    new File(
                            this.currentStudentPath + taskName +
                                    "/build/test-results/test/"
                    ).listFiles((dir, name) ->
                            name.toLowerCase().endsWith(".xml")))[0].getName();
            Document junitDoc = builder
                    .parse(new File(
                            this.currentStudentPath + taskName +
                                    "/build/test-results/test/" + xmlFile)
                    );
            Element junitTestSuite = (Element) junitDoc
                    .getElementsByTagName("testsuite")
                    .item(0);
            taskTestResult.totalTests = Integer.parseInt(
                    junitTestSuite.getAttribute("tests")
            );
            taskTestResult.failedTests = Integer.parseInt(
                    junitTestSuite.getAttribute("failures")
            );
            taskTestResult.ignoredTests = Integer.parseInt(
                    junitTestSuite.getAttribute("skipped")
            );
            taskTestResult.passedTests = taskTestResult.totalTests -
                    taskTestResult.ignoredTests -
                    taskTestResult.failedTests;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Logger.log(ERROR, "Error when parsing tests result!");
        }
    }

    /**
     * Checks whether javadoc exists or not.
     *
     * @param taskName name of the task to get the appropriate folder.
     * @return true if javadoc exists false otherwise.
     */
    private Boolean testJavadoc(String taskName) {
        this.executeGradleCommand("javadoc");
        Path documentationPath = Paths.get(
                this.currentStudentPath + taskName + "/build/docs/javadoc/"
        );
        return Files.exists(documentationPath);
    }

    /**
     * Method that connects TaskRunner instance
     * to the provided Gradle project.
     *
     * @param projectName project that will be connected.
     */
    private void connectToGradleProject(String projectName) {
        this.connector.forProjectDirectory(
                new File(this.currentStudentPath + projectName)
        );
    }

    /**
     * Method that is used to execute a Gradle command in the current project.
     *
     * @param command Gradle command.
     * @return true if the execution is successful, false otherwise.
     */
    private Boolean executeGradleCommand(String command) {
        try {
            ProjectConnection connection = this.connector.connect();
            connection.newBuild()
                    .forTasks(command)
                    .run();
            return true;
        } catch (GradleConnectionException | IllegalStateException e) {
            Logger.log(ERROR, "Error when executing gradle command " + command, "TASK RUNNER");
            return false;
        }
    }
}
