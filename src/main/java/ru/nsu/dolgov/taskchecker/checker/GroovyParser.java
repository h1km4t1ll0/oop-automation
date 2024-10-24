package ru.nsu.dolgov.taskchecker.checker;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import ru.nsu.dolgov.taskchecker.models.core.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to get Configuration from the Groovy script.
 */
public class GroovyParser {
    private final String groovyBuilderPath;

    /**
     * Constructor. Accepts path to the initial groovy script.
     *
     * @param groovyBuilderFile
     */
    public GroovyParser(String groovyBuilderFile) {
        this.groovyBuilderPath = groovyBuilderFile;
    }

    /**
     * Method used to evaluate Groovy script and get the configuration.
     *
     * @return Configuration object.
     * @throws IOException is thrown when the app cant initialize Groovy script.
     */
    public Configuration parse() throws IOException {
        Configuration configuration = new Configuration();
        Map<String, Configuration> map = new HashMap<>();
        map.put("config", configuration);
        Binding binding = new Binding(map);
        GroovyShell shell = new GroovyShell(binding);

        try {
            GroovyCodeSource source = new GroovyCodeSource(
                    new File(this.groovyBuilderPath));
            shell.run(source, Collections.emptyList());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return configuration;
    }
}
