/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Command table entry
 */
public class MethodShellCommand extends AbstractShellCommand {

    private Method method;
    private Object handler;

    public MethodShellCommand(Object handler, Method method, String prefix, String name) {
        super(prefix, name, ShellCommandParamSpec.forMethod(method));
        this.method = method;
        this.handler = handler;
        setDescription(makeCommandDescription(method, getParamSpecs()));
    }

    private static String makeCommandDescription(Method method, ShellCommandParamSpec[] paramSpecs) {
        StringBuilder result = new StringBuilder();
        result.append(method.getName());
        result.append('(');
        Class[] paramTypes = method.getParameterTypes();
        assert paramTypes.length == paramSpecs.length;
        boolean first = true;
        for (int i = 0; i < paramTypes.length; i++) {
            if (!first) {
                result.append(", ");
            }
            first = false;
            if (paramSpecs[i] != null) {
                result.append(paramSpecs[i].getName());
                result.append(":");
                result.append(paramTypes[i].getSimpleName());
            } else {
                result.append(paramTypes[i].getSimpleName());
            }
        }
        result.append(") : ");
        result.append(method.getReturnType().getSimpleName());
        return result.toString();
    }

    @Override
    public Object invoke(Object[] parameters) throws CLIException {
        try {
            return method.invoke(handler, parameters);
        } catch (InvocationTargetException ite) {
            return ite.getCause();
        } catch (Exception ex) {
            throw new CLIException(ex);
        }
    }

    public Method getMethod() {
        return method;
    }

    public Object getHandler() {
        return handler;
    }
}
