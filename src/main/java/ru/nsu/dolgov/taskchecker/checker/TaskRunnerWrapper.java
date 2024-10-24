package ru.nsu.dolgov.taskchecker.checker;

import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.exceptions.WrongDatesForActivityReportException;
import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings;
import ru.nsu.dolgov.taskchecker.models.core.StudentWithTasks;
import ru.nsu.dolgov.taskchecker.models.results.TaskRunnerResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static ru.nsu.dolgov.taskchecker.Logger.LogLevel.*;

/**
 * Wrapper above the TaskRunner used to achieve parallel computations.
 */
public class TaskRunnerWrapper {
    private final AdditionalSettings configuration;
    private final Integer threadQuantity;
    private final CopyOnWriteArrayList<TaskRunnerResult> taskRunnerResults = new CopyOnWriteArrayList<>();
    private ExecutorService threadPool;
    private List<Future<Integer>> futures;

    /**
     * Constructor. Accepts only app configuration.
     *
     * @param configuration configuration for the app.
     */
    public TaskRunnerWrapper(
            AdditionalSettings configuration
    ) {
        this.configuration = configuration;
        this.threadQuantity = this.configuration.runInParallel ?
                Runtime.getRuntime().availableProcessors() : 1;
    }

    /**
     * Method used to process tasks.
     *
     * @param studentWithTasksList list filled with students and their tasks.
     * @param executorName         number of the thread used
     *                             to achieve appropriate logging.
     * @return int to know that execution is finished.
     * @throws IOException                          is thrown by GitHubAPI.
     * @throws WrongDatesForActivityReportException is thrown by GitHubAPI.
     */
    private int taskFactory(
            List<StudentWithTasks> studentWithTasksList,
            String executorName
    ) throws IOException, WrongDatesForActivityReportException {
        for (StudentWithTasks studentWithTasks : studentWithTasksList) {
            GithubAPI githubAPI = new GithubAPI(
                    this.configuration.repositoriesPath,
                    studentWithTasks.student,
                    this.configuration
            );
            githubAPI.downloadRepository();
            Logger.log(
                    SUCCESS,
                    "Downloaded repository for " + studentWithTasks.student.nickname,
                    "TASK CHECKER " + executorName
            );
            TaskRunner taskRunner = new TaskRunner(
                    studentWithTasks.student,
                    this.configuration.repositoriesPath,
                    studentWithTasks.tasks
            );
            studentWithTasks.student.commitsCheckResult = githubAPI.getCommitsActivity();
            Logger.log(
                    SUCCESS,
                    "Got commits activity for " + studentWithTasks.student.nickname,
                    "TASK CHECKER " + executorName
            );
            TaskRunnerResult taskRunnerResult = taskRunner.checkTasks();
            Logger.log(
                    SUCCESS,
                    "Checked tasks for " + studentWithTasks.student.nickname,
                    "TASK CHECKER " + executorName
            );
            this.taskRunnerResults.add(taskRunnerResult);

            if (this.configuration.cleanUp) {
                githubAPI.cleanUp();
                Logger.log(
                        INFO,
                        "Cleaning up...",
                        "TASK CHECKER " + executorName
                );
            }
        }

        return 0;
    }

    /**
     * Method used to split an array of students with
     * tasks to an list of lists of students with tasks
     * to split them into threads.
     *
     * @param studentsWithTasksArray student with tasks to process.
     * @return list of lists of students with tasks
     * to split them into threads.
     */
    private List<List<StudentWithTasks>> splitToCheckList(
            StudentWithTasks[] studentsWithTasksArray
    ) {
        List<List<StudentWithTasks>> listsOfStudentsWithTasks = new ArrayList<>();

        if (studentsWithTasksArray.length < this.threadQuantity) {
            for (StudentWithTasks studentWithTasks : studentsWithTasksArray) {
                List<StudentWithTasks> studentWithTasksList = new ArrayList<>();
                studentWithTasksList.add(studentWithTasks);
                listsOfStudentsWithTasks.add(studentWithTasksList);
            }

            return listsOfStudentsWithTasks;
        }

        int totalSubarrays = (int) Math.ceil((double) studentsWithTasksArray.length / this.threadQuantity);

        for (int i = 0; i < this.threadQuantity; i++) {
            int start = i * totalSubarrays;
            int end = Math.min(start + totalSubarrays, studentsWithTasksArray.length);
            StudentWithTasks[] subarray = Arrays.copyOfRange(studentsWithTasksArray, start, end);
            listsOfStudentsWithTasks.add(Arrays.stream(subarray).toList());
        }

        return listsOfStudentsWithTasks;
    }

    /**
     * Parses the 'toCheck' map into an array.
     *
     * @return array if students with tasks.
     */
    private StudentWithTasks[] parseToCheckMapIntoArray() {
        StudentWithTasks[] studentsWithTasksArray = new StudentWithTasks[this.configuration.toCheckList.size()];
        return this.configuration.toCheckList.toArray(studentsWithTasksArray);
    }

    /**
     * Method to initialize thread pull and assign them tasks.
     */
    private void prepareExecutors() {
        this.threadPool = Executors.newFixedThreadPool(this.threadQuantity);
        this.futures = new ArrayList<>();

        if (this.threadQuantity > 1) {
            List<List<StudentWithTasks>> toCheck = this.splitToCheckList(this.parseToCheckMapIntoArray());
            int executorNumber = 1;
            for (List<StudentWithTasks> studentWithTasksList : toCheck) {
                int finalExecutorNumber = executorNumber;
                this.futures.add(this.threadPool.submit(
                                () -> this.taskFactory(
                                        studentWithTasksList,
                                        Integer.toString(finalExecutorNumber)
                                )
                        )
                );
                executorNumber++;
                Logger.log(INFO, "Submitted task for execution", "TASK CHECKER MAIN");
            }
        } else {
            this.futures.add(
                    this.threadPool.submit(() -> this.taskFactory(
                            Arrays.stream(
                                    this.parseToCheckMapIntoArray()
                            ).toList(),
                            "1"
                    ))
            );
            Logger.log(INFO, "Submitted task for execution", "TASK CHECKER MAIN");
        }
    }

    /**
     * An entrypoint to check the tasks.
     *
     * @return a list of results. One result per student.
     */
    public List<TaskRunnerResult> checkTasks() {
        try {
            this.prepareExecutors();
            Logger.log(
                    INFO,
                    "Preparing " + this.threadQuantity + " executors...",
                    "TASK CHECKER MAIN"
            );
            Logger.log(
                    INFO,
                    "Starting executors...",
                    "TASK CHECKER MAIN"
            );
            for (Future<Integer> result : this.futures) {
                result.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.log(ERROR, "Error when checking tasks!", "TASK CHECKER MAIN");
        } finally {
            this.threadPool.shutdownNow();
        }

        Logger.log(
                SUCCESS,
                "Checking completed!",
                "TASK CHECKER MAIN"
        );

        return this.taskRunnerResults;
    }
}
