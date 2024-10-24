package ru.nsu.dolgov.taskchecker.exceptions;

/**
 * Exception that is thrown when no group is found.
 */
public class NoSuchGroupException extends Exception {
    public NoSuchGroupException(String errorMessage) {
        super(errorMessage);
    }
}
