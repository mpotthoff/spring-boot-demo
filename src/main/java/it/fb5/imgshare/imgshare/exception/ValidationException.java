package it.fb5.imgshare.imgshare.exception;

public class ValidationException extends Exception {

    private final String field;

    public ValidationException(String field, String message) {
        super(message);

        this.field = field;
    }

    public String getField() {
        return this.field;
    }
}
