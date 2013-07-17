/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

/*
 *   Introducing the asg.cliche (http://cliche.sourceforge.net/)
 * Cliche is to be a VERY simple reflection-based command line shell
 * to provide simple CLI for simple applications.
 * The name formed as follows: "CLI Shell" --> "CLIShe" --> "Cliche".
 */

package com.maxifier.cliche;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Shell is the class interacting with user.
 * Provides the command loop or single command execution
 *
 * @author ASG
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class Shell {
    /**
     * Used to create subsequence of commands
     */
    public static final String COMMAND_UNION_SIGN = "&&";
    private final ConsoleIO consoleIO;
    private final String prompt;

    private Multimap<String, ShellCommand> commands = LinkedListMultimap.create();

    private final InputConversionEngine inputConversionEngine;
    private final OutputConversionEngine outputConversionEngine;



    private Multimap<Class<?>, InputConverter> inputConverters = LinkedListMultimap.create();
    private Multimap<Class<?>, OutputConverter> outputConverters = LinkedListMultimap.create();

    public Shell(ConsoleIO consoleIO, String prompt) {
        this.consoleIO = consoleIO;
        this.prompt = prompt;

        ConversionEngine conversionEngine = new ConversionEngine();
        inputConversionEngine = conversionEngine;
        outputConversionEngine = conversionEngine;
         //add default input and output converters


        //add built-in commands
    }

    /**
     * Add new command handler.
     * @param handler object with methods annotated with @Commands
     */
    public void addHandler(Object handler) {
        addHandler(handler, null);
    }

    /**
     * Add new command handler and all methods in this handler will be prefixed with provided prefix.
     * <br>
     * For example, instance of this class:
     * <pre>
     *     class Foo {
     *
     *        {@literal @}Command(name="hello")
     *         public String command() {
     *             return "Hello world!"
     *         }
     *     }
     *
     *     shell.addHandler(new Foo(), "!");
     * </pre>
     *
     * Will be translated to command '!hello'
     * <br>
     * Mostly usable to specific commands set like help and so on.
     *
     * @param handler object with methods annotated with @Commands
     * @param prefix prefix would be used for any command name from this handler
     */
    public void addHandler(Object handler, @Nullable String prefix) {
        Preconditions.checkNotNull(handler, "Handler instance can't be null");
        //TODO

    }

    public void addInputConverter(Class<?> classType, InputConverter inputConverter) {
        inputConverters.put(classType, inputConverter);
    }

    public void addOutputConverter(Class<?> classType, OutputConverter outputConverter) {
        outputConverters.put(classType, outputConverter);
    }

    /**
     * Process command line through ConsoleIO.
      * @param commandLine command line line to be processed.
     */
    public void processCommand(String commandLine) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(commandLine), "Command should not be null");
        //parse commandLine
        String[] commandsInLine = commandLine.split(COMMAND_UNION_SIGN);
        for (String basicCommand : commandsInLine) {
            processCommand0(basicCommand);
        }
    }

    @VisibleForTesting
    void processCommand0(String command) {
        List<Token> tokens = Token.tokenize(command);
        ShellCommand shellCommand = lookupCommand(command);
        Class<?>[] parameterTypes = shellCommand.getMethod().getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            String parameterString = tokens.get(i).getString();
        }
        for (Class<?> parameterType : parameterTypes) {
            synchronized (inputConverters) {

            }
        }
        Class[] paramClasses = parameterTypes;
        Object[] parameters = new Object[paramClasses.length];

        Object[] parameters = inputConverter.convertToParameters(tokens, paramClasses,
                commandToInvoke.getMethod().isVarArgs());
        Object result = commandToInvoke.invoke(parameters);

        if (invocationResult != null) {
            consoleIO.output();
        }

    }

    private ShellCommand lookupCommand(String command) {
        List<Token> tokens = Token.tokenize(command);
        String discriminator = tokens.get(0).getString();
    }

    /**
     * Runs the command session.
     * Create the Shell, then run this method to listen to the user,
     * and the Shell will invoke Handler's methods.
     *
     * @throws java.io.IOException when can't readLine() from input.
     */
    public void commandLoop() throws IOException {
        String command;
        do {
            command = consoleIO.readCommand(prompt);
            if (!Strings.isNullOrEmpty(command)) {
                List<Token> tokens = ;
                if (tokens.size() > 0) {
                    String discriminator = tokens.get(0).getString();
                    try {
                        processCommand(discriminator, tokens);
                    } catch (CLIException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while();
    }

}
