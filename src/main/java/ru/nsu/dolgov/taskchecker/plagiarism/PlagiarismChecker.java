package ru.nsu.dolgov.taskchecker.plagiarism;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;
import de.jplag.reporting.reportobject.ReportObjectFactory;
import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.checker.GithubAPI;
import ru.nsu.dolgov.taskchecker.exceptions.NotEnoughPlagiarismCandidatesException;
import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings;
import ru.nsu.dolgov.taskchecker.models.core.AdditionalSettings.PlagiarismCandidate;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static ru.nsu.dolgov.taskchecker.Logger.LogLevel.INFO;

/**
 * Class that implements the plagiarism check. Uses JPlag.
 */
public class PlagiarismChecker {
    private final List<PlagiarismCandidate> plagiarismCandidateList;
    private final AdditionalSettings settings;

    /**
     * Constructor. Accepts configuration.
     *
     * @param settings app configuration.
     */
    public PlagiarismChecker(AdditionalSettings settings) {
        this.plagiarismCandidateList = settings.plagiarismCandidateList;
        this.settings = settings;
    }

    /**
     * Downloads repository if it is not exists.
     */
    private void downloadRepositoryIfNotExists() {
        for (PlagiarismCandidate plagiarismCandidateObject : this.plagiarismCandidateList) {
            GithubAPI githubAPI = new GithubAPI(
                    settings.repositoriesPath,
                    plagiarismCandidateObject.suspectedStudent,
                    this.settings
            );
            githubAPI.downloadRepository();
        }
    }

    /**
     * Checks provided students for plagiarism and makes a report.
     *
     * @return String, report file path.
     * @throws NotEnoughPlagiarismCandidatesException is thrown when less than 2 students provided.
     * @throws IOException                            is thrown when unable to run JPlag.
     * @throws ExitException                          is thrown by JPlag.
     */
    public String checkForPlagiarism() throws
            NotEnoughPlagiarismCandidatesException,
            IOException,
            ExitException {
        if (this.plagiarismCandidateList.size() >= 2) {
            this.downloadRepositoryIfNotExists();
            List<File> submissionDirectoriesList = this.plagiarismCandidateList.stream().map(
                    plagiarismCandidateObject -> new File(
                            "./" + this.settings.repositoriesPath + "/" +
                                    plagiarismCandidateObject.suspectedStudent.nickname
                    )
            ).collect(toList());
            File baseCode = submissionDirectoriesList.getFirst();
            submissionDirectoriesList.removeFirst();
            Set<File> submissionDirectories = Set.copyOf(submissionDirectoriesList);

            JavaLanguage language = new JavaLanguage();
            JPlagOptions options = new JPlagOptions(
                    language,
                    submissionDirectories,
                    Set.of(baseCode)
            );

            JPlagResult result = JPlag.run(options);
            String reportFilename = "./" + this.settings.plagiarismReportFolder +
                    "/" + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss")
            ) + "_plagiarism_report.zip";
            Path path = Paths.get(reportFilename);

            Files.createDirectories(path.getParent());

            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException e) {
                Logger.log(INFO, "Directory for the plagiarism report already exists. Skipping creation...", "PLAGIARISM SERVICE");
            }

            File reportFile = new File(reportFilename);
            ReportObjectFactory reportObjectFactory = new ReportObjectFactory(
                    reportFile
            );
            reportObjectFactory.createAndSaveReport(result);

            return reportFilename;
        } else {
            throw new NotEnoughPlagiarismCandidatesException(
                    String.format(
                            "Not enough plagiarism candidates provided: %s. Minimal amount is 2.",
                            this.plagiarismCandidateList.size()
                    )
            );
        }
    }
}
