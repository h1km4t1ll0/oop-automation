package ru.nsu.dolgov.taskchecker.reportbuilder;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Class used to provide parsing fot the LocalDate.
 */
public class LocalDateTypeAdapter
        implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Method overriding.
     *
     * @param date      LocalDate.
     * @param typeOfSrc type.
     * @param context   context.
     * @return JsonElement.
     */
    @Override
    public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(date.format(formatter));
    }

    /**
     * Method overriding.
     *
     * @param json    Json to be parsed.
     * @param typeOfT type.
     * @param context context.
     * @return parsed LocalDate.
     * @throws JsonParseException when unable to parse the provided JSON.
     */
    @Override
    public LocalDate deserialize(final JsonElement json, final Type typeOfT,
                                 final JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsString(), formatter);
    }
}
