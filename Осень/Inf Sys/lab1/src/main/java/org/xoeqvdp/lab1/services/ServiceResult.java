package org.xoeqvdp.lab1.services;

import lombok.Getter;

@Getter
public class ServiceResult<T> {
    private final T result;
    private final String message;
    private final boolean success;

    public ServiceResult(T result, String message) {
        this.result = result;
        this.message = message;
        this.success = true;
    }

    public ServiceResult(String message) {
        this.result = null;
        this.message = message;
        this.success = false;
    }
}
