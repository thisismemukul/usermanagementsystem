package com.user.management.exceptions;

public interface IBaseError<T> {
    public String getErrorCode();

    public String getErrorMessage();

    public String getUserMessage();

    public T getMetadata();

    public String getErrorType();

    public boolean displayMsg();
}
