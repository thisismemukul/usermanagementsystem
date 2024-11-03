package com.user.management.exceptions;


public class DefaultBaseError<T> implements IBaseError<T> {
    private String errorCode;
    private String errorMessage;
    private String userMessage;
    private T metaData;
    private String errorType;
    private boolean displayMsg;

    public DefaultBaseError(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public DefaultBaseError(String errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public DefaultBaseError(String errorCode, String errorMessage, String userMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userMessage = userMessage;
    }

    public DefaultBaseError(String errorCode, String errorMessage, String userMessage, boolean displayMsg) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userMessage = userMessage;
        this.displayMsg = displayMsg;
    }

    public DefaultBaseError(String errorCode, String errorMessage, String userMessage, String errorType, boolean displayMsg) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userMessage = userMessage;
        this.errorType = errorType;
        this.displayMsg = displayMsg;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String getUserMessage() {
        return this.userMessage;
    }

    @Override
    public T getMetadata() {
        return this.metaData;
    }

    @Override
    public String getErrorType() {
        return this.errorType;
    }

    @Override
    public boolean displayMsg() {
        return this.displayMsg;
    }

}
