/**
 * This file groups the custom exception types used throughout the
 * Student Management System GUI to represent specific error conditions.
 * Keeping them as distinct types (rather than one generic Exception)
 * lets each event handler give the administrator a precise, helpful
 * error message instead of a generic failure notice.
 */

/** Thrown when a required field is missing or fails validation (e.g. malformed email). */
class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

/** Thrown when the administrator triggers an action without selecting a required item first. */
class NoSelectionException extends Exception {
    public NoSelectionException(String message) {
        super(message);
    }
}

/** Thrown when a grade value entered by the administrator does not match an accepted format. */
class InvalidGradeException extends Exception {
    public InvalidGradeException(String message) {
        super(message);
    }
}
