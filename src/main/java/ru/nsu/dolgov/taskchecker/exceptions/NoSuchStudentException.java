package ru.nsu.dolgov.taskchecker.exceptions;

/**
 * Exception that is thrown when no student is found.
 */
public class NoSuchStudentException extends Exception {
    public NoSuchStudentException(String errorMessage) {
        super(errorMessage);
    }
}
