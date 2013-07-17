package com.maxifier.cliche;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * ConsoleIO
 *
 * @author aleksey.didik@maxifier.com (Aleksey Didik) (2013-07-17 13:32)
 */
public interface ConsoleIO {

    /**
     * Read command string with suggested prompt.
     * Command is trimmed and have no side spaces.
     *
     * @param prompt prompt message should be shown before cursor
     * @return trimmed string command representation
     */
    String readCommand(String prompt) throws IOException;

    /**
     * Write command execution result to output
     *
     * @param commandResult command result
     */
    void output(String commandResult);

    /**
     * Output command syntax exception
     * @param input command asked to be executed
     * @param error {@see TokenException} with syntax error explanation
     */
    void outputException(String input, TokenException error);

    /**
     * Output other exception what is result of command execution
     * @param e exception was thrown during command execution.
     */
    void outputException(Throwable e);
}
