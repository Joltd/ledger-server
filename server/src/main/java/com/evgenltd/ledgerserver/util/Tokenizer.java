package com.evgenltd.ledgerserver.util;

import java.util.StringTokenizer;

public class Tokenizer {

    private final String value;
    private final StringTokenizer tokenizer;

    public Tokenizer(final String value) {
        this.value = value;
        this.tokenizer = new StringTokenizer(value, " \t\n\r\f", true);
    }

    public static Tokenizer of(final String value) {
        return new Tokenizer(value);
    }

    public String next() {
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final char symbol = token.charAt(0);
            if (" \t\n\r\f".indexOf(symbol) >= 0) {
                continue;
            }
            return token;
        }

        return "";
    }

    public String whole() {
        final StringBuilder sb = new StringBuilder();
        while (tokenizer.hasMoreElements()) {
            sb.append(tokenizer.nextElement());
        }
        return sb.toString().trim();
    }

}
