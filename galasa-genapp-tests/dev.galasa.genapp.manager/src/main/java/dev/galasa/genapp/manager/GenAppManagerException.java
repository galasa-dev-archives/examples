/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
package dev.galasa.genapp.manager;

import dev.galasa.ManagerException;

public class GenAppManagerException extends ManagerException{

    private static final long serialVersionUID = 1L;

    public GenAppManagerException() {
    }

    public GenAppManagerException(String message) {
        super(message);
    }

    public GenAppManagerException(Throwable cause) {
        super(cause);
    }

    public GenAppManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenAppManagerException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}