package com.jonahseguin.drink.parametric;

import java.util.HashSet;
import java.util.Set;

public record BindingContainer<T>(Class<T> type, Set<DrinkBinding<T>> bindings) {

    public BindingContainer(Class<T> type) {
        this(type, new HashSet<>());
    }

}
