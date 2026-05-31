package com.jonahseguin.drink.command;

import javax.annotation.Nullable;

public record CommandFlag(char character, @Nullable String value) {

    public static final char FLAG_PREFIX = '-';

    public CommandFlag(char character) {
        this(character, null);
    }

    public String flagPrefixToString() {
        return String.valueOf(new char[]{FLAG_PREFIX, character});
    }

}
