package com.jonahseguin.drink.parametric;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public record CommandParameters(int count, CommandParameter[] parameters) {

    public CommandParameters(Method method) {
        CommandParameter[] parameters = new CommandParameter[method.getParameters().length];
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter param = method.getParameters()[i];
            parameters[i] = new CommandParameter(param.getType(), param, method.getParameterAnnotations()[i]);
        }
        this(method.getParameterCount(), parameters);
    }

    public boolean isLastArgument(int index) {
        if (index != parameters.length - 1) {
            for (int i = index + 1; i < parameters.length; i++) {
                CommandParameter parameter = parameters[i];
                if (!parameter.isFlag()) {
                    return false;
                }
            }
        }
        return true;
    }

}
