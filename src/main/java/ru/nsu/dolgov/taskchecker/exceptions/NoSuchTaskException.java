package ru.nsu.dolgov.taskchecker.exceptions;

/**
 * Exception that is thrown when no task is found.
 */
public class NoSuchTaskException extends Exception {
    public NoSuchTaskException(String errorMessage) {
        super(errorMessage);
    }
}
