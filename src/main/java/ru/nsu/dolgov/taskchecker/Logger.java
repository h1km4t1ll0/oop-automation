package ru.nsu.dolgov.taskchecker;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.nsu.dolgov.taskchecker.Logger.Colors.*;

/**
 * Logger implementation.
 */
public class Logger {
    private static String getCurrentDate() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }

    /**
     * Color token getter.
     *
     * @param level level of the log.
     * @return String, color token.
     */
    private static String getColorToken(LogLevel level) {
        String firstColorToken = RED;
        switch (level) {
            case INFO -> firstColorToken = BLUE;
            case ERROR -> firstColorToken = RED;
            case WARNING -> firstColorToken = YELLOW;
            case SUCCESS -> firstColorToken = GREEN;
        }
        return firstColorToken;
    }

    /**
     * Method to log without providing the source.
     *
     * @param level log level.
     * @param message message.
     */
    public static void log(LogLevel level, String message) {
        System.out.printf(
                "%s[%s][%s]%s %s%s%s\n",
                YELLOW_BOLD_BRIGHT, getCurrentDate(), "SYSTEM", RESET,
                getColorToken(level), message, RESET
        );
    }

    /**
     * Method used to log with specified source.
     *
     * @param level log level.
     * @param message message.
     * @param source source of the log.
     */
    public static void log(LogLevel level, String message, String source) {
        System.out.printf(
                "%s[%s][%s]%s %s%s%s\n",
                YELLOW_BOLD_BRIGHT, getCurrentDate(), source.toUpperCase(), RESET,
                getColorToken(level), message, RESET
        );
    }

    public enum LogLevel {
        ERROR, INFO, WARNING, SUCCESS
    }

    /**
     * Class used to store different colors.
     */
    public static class Colors {
        public static final String RESET = "\033[0m";

        public static final String RED = "\033[0;31m";
        public static final String GREEN = "\033[0;32m";
        public static final String YELLOW = "\033[0;33m";
        public static final String BLUE = "\033[0;34m";
        public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";
    }
}