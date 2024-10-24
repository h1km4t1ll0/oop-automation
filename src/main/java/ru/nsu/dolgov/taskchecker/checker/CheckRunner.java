package ru.nsu.dolgov.taskchecker.checker;

import de.jplag.exceptions.ExitException;
import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.exceptions.NotEnoughPlagiarismCandidatesException;
import ru.nsu.dolgov.taskchecker.models.core.Configuration;
import ru.nsu.dolgov.taskchecker.models.core.JSONReportObject;
import ru.nsu.dolgov.taskchecker.plagiarism.PlagiarismChecker;
import ru.nsu.dolgov.taskchecker.reportbuilder.JSONReportBuilder;

import java.io.IOException;

import static ru.nsu.dolgov.taskchecker.Logger.LogLevel.*;

/**
 * An utility class used to start processing tasks.
 */
public class CheckRunner extends Thread {
    /**
     * An overriding of the Thread method that starts the thread.
     */
    @Override
    public void run() {
        GroovyParser parser = new GroovyParser(
                "./src/main/groovy/ru/nsu/dolgov/taskchecker/process.groovy"
        );
        Logger.log(INFO, "Initialized configuration");
        Configuration config;
        try {
            config = parser.parse();
        } catch (IOException e) {
            Logger.log(ERROR, "Error when parsing configuration!");
            return;
        }
        Logger.log(INFO, "Parsed configuration");
        config.additionalSettings.setToCheckList();
        JSONReportObject jsonReportObject = new JSONReportObject();

        if (!config.additionalSettings.plagiarismCandidateList.isEmpty()) {
            PlagiarismChecker plagiarismChecker = new PlagiarismChecker(config.additionalSettings);
            try {
                jsonReportObject.plagiarismReportPath = plagiarismChecker.checkForPlagiarism();
            } catch (NotEnoughPlagiarismCandidatesException e) {
                Logger.log(ERROR, e.getMessage(), "PLAGIARISM SERVICE");
            } catch (IOException e) {
                Logger.log(ERROR, "Error when creating plagiarism report!", "PLAGIARISM SERVICE");
            } catch (ExitException e) {
                Logger.log(ERROR, "Error when running JPlag!", "PLAGIARISM SERVICE");
            }
        }
        Logger.log(SUCCESS, "Created plagiarism report at ./" +
                config.additionalSettings.plagiarismReportFolder);

        TaskRunnerWrapper taskRunner = new TaskRunnerWrapper(config.additionalSettings);
        Logger.log(INFO, "Initialized the task runner");
        JSONReportBuilder jsonReportBuilder = new JSONReportBuilder("./src/main/typescript/public/");
        Logger.log(INFO, "Initialized JSONReport builder");
        jsonReportObject.additionalSettings = config.additionalSettings;
        jsonReportObject.groups = config.groups;
        jsonReportObject.students = config.students;
        jsonReportObject.tasks = config.tasks;
        Logger.log(INFO, "Checking tasks...");
        jsonReportObject.taskRunnerResults = taskRunner.checkTasks();
        Logger.log(
                INFO,
                "Started building JSON report"
        );
        jsonReportBuilder.serialize(jsonReportObject);
        Logger.log(
                SUCCESS,
                "All is done!"
        );
    }
}
