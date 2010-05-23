package com.dafrito.economy;

public class FailedActionException extends OrderException {

    public FailedActionException(Throwable ex) {
        super(ex);
    }

    @Override
    public String getMessage() {
        return "An action failed, returning this exception: " + this.getCause();
    }
}
