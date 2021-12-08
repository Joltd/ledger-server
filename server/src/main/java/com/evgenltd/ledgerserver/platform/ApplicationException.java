package com.evgenltd.ledgerserver.platform;

public class ApplicationException extends RuntimeException {

    public ApplicationException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    public ApplicationException(final Throwable throwable, final String message, final Object... args) {
        super(String.format(message, args), throwable);
    }

}
