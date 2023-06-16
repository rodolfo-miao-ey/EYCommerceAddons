package br.com.braspag.service.exception;

public class BraspagApiException extends Exception {

    private int statusCode;

    public BraspagApiException(String message) {
        super(message);
    }

    public BraspagApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String toString() {
        return "ApiException{statusCode=" + this.statusCode + ", message=" + this.getMessage() + '}';
    }
}