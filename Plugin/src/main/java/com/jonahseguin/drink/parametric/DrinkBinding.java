package com.jonahseguin.drink.parametric;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Set;

public record DrinkBinding<T>(Class<T> type, Set<Class<? extends Annotation>> annotations, DrinkProvider<T> provider) {

    public boolean canProvideFor(@Nonnull CommandParameter parameter) {
        Preconditions.checkNotNull(parameter, "Parameter cannot be null");
        // The parameter and binding need to have exact same annotations
        for (Annotation c : parameter.getClassifierAnnotations()) {
            if (!annotations.contains(c.annotationType())) {
                return false;
            }
        }
        for (Class<? extends Annotation> annotation : annotations) {
            if (parameter.getClassifierAnnotations().stream().noneMatch(a -> a.annotationType().equals(annotation))) {
                return false;
            }
        }
        return true;
    }

}
