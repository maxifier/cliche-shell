/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Console IO subsystem.
 * This is also one of special command handlers and is responsible
 * for logging (duplicating output) and execution of scripts.
 *
 * @author ASG
 */
public class JLineConsoleIO implements ConsoleIO {

    private ConsoleReader in;
    private PrintStream out;
    private PrintStream err;

    public JLineConsoleIO(ConsoleReader in,
                          PrintStream out,
                          PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    @Override
    public String readCommand(String prompt) throws IOException {
        return in.readLine(String.format("%s> ", prompt));
    }

    @Override
    public void output(String commandResult) {
        out.println(commandResult);
        out.flush();
    }

    @Override
    public void outputException(String input, TokenException error) {
        int errIndex = error.getToken().getIndex();
        while (errIndex-- > 0) {
            err.print("-");
        }
        for (int i = 0; i < error.getToken().getString().length(); i++) {
            err.print("^");
        }
        err.println(error.getMessage());
    }

    @Override
    public void outputException(Throwable e) {
        err.println(e);
        if (e.getCause() != null) {
            err.println(e.getCause());
        }
    }


}
