package br.com.braspag.exceptions;

import br.com.braspag.service.exception.BraspagApiException;

public class BraspagTimeoutException extends BraspagApiException {
    public BraspagTimeoutException(String message) {
        super(message);
    }
}