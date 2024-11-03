package com.user.management.exceptions;

import java.io.Serial;

public class UserMgmtException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final IBaseError<?> iBaseError;

    public UserMgmtException(IBaseError<?> iBaseError) {
        super(iBaseError.getErrorMessage());
        this.iBaseError = iBaseError;
    }
    public IBaseError<?> getIBaseError() {
        return iBaseError;
    }

    public String getUserMessage() {
        return iBaseError.getUserMessage();
    }
}