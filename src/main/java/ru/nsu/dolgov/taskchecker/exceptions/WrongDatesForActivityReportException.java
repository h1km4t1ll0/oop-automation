package ru.nsu.dolgov.taskchecker.exceptions;

/**
 * Exception that is thrown when there are too few dates to get an activity report.
 */
public class WrongDatesForActivityReportException extends Exception {
    public WrongDatesForActivityReportException(String errorMessage) {
        super(errorMessage);
    }
}
