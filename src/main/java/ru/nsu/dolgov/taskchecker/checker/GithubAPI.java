package ru.nsu.dolgov.taskchecker.checker;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.exceptions.WrongDatesForActivityReportException;
import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings;
import ru.nsu.dolgov.taskchecker.models.core.Student;
import ru.nsu.dolgov.taskchecker.models.core.Task;
import ru.nsu.dolgov.taskchecker.models.results.CommitsCheckResult;
import ru.nsu.dolgov.taskchecker.models.results.DeadlinesCheckResult;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * Class used to do operations with Github: clone, pull, etc.
 */
public class GithubAPI {
    private final Student student;
    private final File currentStudentRepository;
    private final AdditionalSettings additionalSettings;

    /**
     * Constructor.
     *
     * @param allRepositoriesPath path where all repositories are located.
     * @param student             student which repo the app uses.
     * @param additionalSettings  additionalSettings object that is needed to access the configuration.
     */
    public GithubAPI(
            String allRepositoriesPath,
            Student student,
            AdditionalSettings additionalSettings
    ) {
        this.student = student;
        this.additionalSettings = additionalSettings;
        this.currentStudentRepository = new File("./" + allRepositoriesPath + "/" + this.student.nickname);
    }

    /**
     * Cleanup helper used to recursively delete all the
     * folders that belongs to a specific user.
     *
     * @param directoryToBeDeleted repository that will be deleted.
     */
    private static void cleanUpHelper(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                cleanUpHelper(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    /**
     * Method used to compute the deadlines using the last commit in the repository.
     *
     * @param task                     task for which deadlines will be computed.
     * @param currentStudentRepository current repository.
     * @return DeadlineCheckResult describing the result of the check.
     */
    public static DeadlinesCheckResult checkDeadlines(Task task, String currentStudentRepository) {
        DeadlinesCheckResult deadlinesCheckResult = new DeadlinesCheckResult();

        try {
            Iterable<RevCommit> commits = Git
                    .open(new File(currentStudentRepository))
                    .log()
                    .addPath(task.id)
                    .call();
            LocalDate firstCommitDate = null;
            int lastCommitDate = 0;
            for (RevCommit commit : commits) {
                if (firstCommitDate == null) {
                    firstCommitDate = LocalDate.ofInstant(
                            Instant.ofEpochSecond(commit.getCommitTime()), ZoneId.systemDefault()
                    );
                }
                lastCommitDate = commit.getCommitTime();
            }
            if (firstCommitDate == null) {
                Logger.log(Logger.LogLevel.INFO, "No commits with this project found", "GITHUB API");
                return deadlinesCheckResult;
            }
            LocalDate lastDate = LocalDate.ofInstant(Instant.ofEpochSecond(lastCommitDate), ZoneId.systemDefault());

            deadlinesCheckResult.softDeadlinePass = firstCommitDate.isBefore(task.softDeadline);
            deadlinesCheckResult.hardDeadlinePass = lastDate.isBefore(task.hardDeadline);
        } catch (Exception ignored) {
        }

        return deadlinesCheckResult;
    }

    /**
     * Method used to clone/pull the repository.
     */
    public void downloadRepository() {
        try {
            File directory = this.currentStudentRepository;
            Git repository;
            if (!directory.exists()) {
                repository = Git.cloneRepository()
                        .setURI(this.student.repository)
                        .setDirectory(directory)
                        .call();
            } else {
                try {
                    repository = Git.open(directory);
                } catch (IOException e) {
                    Logger.log(
                            Logger.LogLevel.ERROR,
                            "Error when downloading repository!",
                            "GITHUB API"
                    );
                    return;
                }
            }

            if (repository != null) {
                repository.pull().call();
            }

        } catch (GitAPIException e) {
            Logger.log(Logger.LogLevel.ERROR, "Error when downloading repository!", "GITHUB API");
        }
    }

    /**
     * Method that makes a request to the Github API to get commits between two dates.
     *
     * @param startDate start date.
     * @param endDate   end date.
     * @return Response object from the Github API.
     * @throws IOException when request cant be done.
     */
    private Response request(LocalDate startDate, LocalDate endDate) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(
                "https://api.github.com/repos/%s/OOP/commits?since=%sT00:00:00Z&until=%sT23:59:59Z",
                this.student.nickname,
                startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "token " + this.additionalSettings.githubToken)
                .build();

        return client.newCall(request).execute();
    }

    /**
     * Method to parse the response and get JSONArray of commits.
     *
     * @param response response object from GitHub API.
     * @return parsed JSONArray.
     * @throws IOException when tha app cant get the
     *                     string representation of the response body.
     */
    private JsonArray parseResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            assert response.body() != null;
            String responseBody = response.body().string();
            return JsonParser.parseString(responseBody).getAsJsonArray();
        }

        return new JsonArray();
    }

    /**
     * Method that used to get the activity of the student
     * according to the configuration's control points.
     *
     * @return CommitsCheckResult object describing students' activity.
     * @throws IOException                          thrown by the request() method.
     * @throws WrongDatesForActivityReportException when controlPoints' length is not equal to 2.
     */
    public CommitsCheckResult getCommitsActivity() throws
            IOException,
            WrongDatesForActivityReportException {
        return this.getCommitsActivity(
                additionalSettings.controlPoints.getFirst(),
                additionalSettings.controlPoints.get(1)
        );
    }

    /**
     * Method-helper that  used to get the activity of
     * the student based on start and end dates.
     *
     * @param startDate start date.
     * @param endDate   end date.
     * @return CommitsCheckResult object describing students' activity.
     * @throws IOException                          thrown by the request() method.
     * @throws WrongDatesForActivityReportException is thrown if controlPoints' length is not equal to 2.
     */
    public CommitsCheckResult getCommitsActivity(LocalDate startDate, LocalDate endDate) throws
            IOException,
            WrongDatesForActivityReportException {
        if (this.additionalSettings.controlPoints.size() != 2) {
            throw new WrongDatesForActivityReportException("Wrong dates for an activity report!");
        }
        LocalDate currentEndDate = startDate.plusDays(7);
        List<CommitsCheckResult.CommitsPerWeek> commitsPerWeekList = new ArrayList<>();
        int commitCount = 0;
        int maxCommitsCount = -1;
        int weeksCount = 0;
        int totalActiveWeeks = 0;

        while (currentEndDate.isBefore(endDate)) {
            startDate = currentEndDate.plusDays(1);
            currentEndDate = currentEndDate.plusDays(7);
            Response response = this.request(startDate, currentEndDate);
            JsonArray commitsArray = this.parseResponse(response);
            int currentCommitsCount = commitsArray.size();

            CommitsCheckResult.CommitsPerWeek commitsPerWeek = new CommitsCheckResult.CommitsPerWeek();
            commitsPerWeek.week = currentEndDate;
            commitsPerWeek.commits = currentCommitsCount;
            commitsPerWeekList.add(commitsPerWeek);

            if (currentCommitsCount > 0) {
                totalActiveWeeks++;
            }
            commitCount += currentCommitsCount;
            if (currentCommitsCount > maxCommitsCount) {
                maxCommitsCount = currentCommitsCount;
            }
            weeksCount++;
        }

        if (ChronoUnit.DAYS.between(endDate, currentEndDate) > 1) {
            Response response = this.request(currentEndDate, endDate);
            JsonArray commitsArray = this.parseResponse(response);
            int currentCommitsCount = commitsArray.size();
            commitCount += currentCommitsCount;

            CommitsCheckResult.CommitsPerWeek commitsPerWeek = new CommitsCheckResult.CommitsPerWeek();
            commitsPerWeek.week = endDate;
            commitsPerWeek.commits = currentCommitsCount;
            commitsPerWeekList.add(commitsPerWeek);

            if (currentCommitsCount > maxCommitsCount) {
                maxCommitsCount = currentCommitsCount;
            }
        }
        CommitsCheckResult result = new CommitsCheckResult();
        result.maximumCommitsPerWeek = maxCommitsCount;
        result.totalCommits = commitCount;
        result.totalWeeks = weeksCount;
        result.totalActiveWeeks = totalActiveWeeks;
        result.commitsPerWeekList = commitsPerWeekList;

        return result;
    }

    /**
     * Cleanup method that uses helper to clean up repositories.
     */
    public void cleanUp() {
        cleanUpHelper(this.currentStudentRepository);
    }
}
