package ru.nsu.dolgov.taskchecker.reportbuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.nsu.dolgov.taskchecker.Logger;
import ru.nsu.dolgov.taskchecker.models.core.JSONReportObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;

import static ru.nsu.dolgov.taskchecker.Logger.LogLevel.ERROR;

/**
 * Class that is used to make a JSON output of the app.
 */
public class JSONReportBuilder {
    private final String path;

    /**
     * Constructor, accepts the path where to save the report.
     *
     * @param path path where to save the report.
     */
    public JSONReportBuilder(String path) {
        this.path = path;
    }

    /**
     * Method to serialize all the data and save it.
     *
     * @param payload JSONReportObject with the data.
     */
    public void serialize(JSONReportObject payload) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        String jsonString = gson.toJson(payload);
        try (Writer writer = new FileWriter(this.path + "task-checker-report.json")) {
            writer.write(jsonString);
        } catch (IOException e) {
            Logger.log(ERROR, "Error when creating json report!", "JSON BUILDER");
        }
    }
}


