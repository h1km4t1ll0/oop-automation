package ru.nsu.dolgov.taskchecker.exceptions;

/**
 * Exception that is thrown when only one student provided to check for plagiarism.
 */
public class NotEnoughPlagiarismCandidatesException extends Exception {
    public NotEnoughPlagiarismCandidatesException(String errorMessage) {
        super(errorMessage);
    }
}
