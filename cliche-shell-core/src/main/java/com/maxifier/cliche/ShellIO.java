/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;

import com.maxifier.cliche.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * Console IO subsystem.
 * This is also one of special command handlers and is responsible
 * for logging (duplicating output) and execution of scripts.
 *
 * @author ASG
 * @author aleksey.didik
 */
public class ShellIO implements Input, Output {

    private static final String PROMPT_SUFFIX = "> ";

    private BufferedReader in;
    private PrintStream out;
    private PrintStream err;

    /**
     * Create new shell IO
     *
     * @param in  input stream
     * @param out output stream
     * @param err error stream
     */
    public ShellIO(BufferedReader in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }


    /**
     * Create new IO based on System.in, out and err
     */
    public ShellIO() {
        this(new BufferedReader(new InputStreamReader(System.in)),
                System.out, System.err);
    }

    private int lastCommandOffset = 0;

    public String readCommand(List<String> path) {
        try {
            String prompt = Strings.joinStrings(path, false, '/');
            String completePrompt = prompt + PROMPT_SUFFIX;
            print(completePrompt);
            lastCommandOffset = completePrompt.length();
            return in.readLine();
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    public void outputHeader(String text) {
        println(text);
    }

    public void output(Object obj, OutputConversionEngine oce) {
            obj = oce.convertOutput(obj);
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                output(Array.get(obj, i), 0, oce);
            }
        } else if (obj instanceof Collection) {
            for (Object elem : (Collection) obj) {
                output(elem, 0, oce);
            }
        } else {
            output(obj, 0, oce);
        }
    }

    private void output(Object obj,
                        int indent,
                        OutputConversionEngine oce) {
        obj = oce.convertOutput(obj);
        for (int i = 0; i < indent; i++) {
            print("\t");
        }
        if (obj == null) {
            println("(null)");
        } else if (obj.getClass().isPrimitive() || obj instanceof String) {
            println(obj);
        } else if (obj.getClass().isArray()) {
            println("Array");
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                output(Array.get(obj, i), indent + 1, oce);
            }
        } else if (obj instanceof Collection) {
            println("Collection");
            for (Object elem : (Collection) obj) {
                output(elem, indent + 1, oce);
            }
        } else if (obj instanceof Throwable) {
            println(obj); // class and its message
            ((Throwable) obj).printStackTrace(out);
        } else {
            println(obj);
        }
    }

    private void print(Object x) {
        out.print(x);
    }

    private void println(Object x) {
        out.println(x);
    }

    private void printErr(Object x) {
        err.print(x);
    }

    private void printlnErr(Object x) {
        err.println(x);
    }

    public void outputException(String input, TokenException error) {
        int errIndex = error.getToken().getIndex() + lastCommandOffset;
        while (errIndex-- > 0) {
            printErr("-");
        }
        for (int i = 0; i < error.getToken().getString().length(); i++) {
            printErr("^");
        }
        printlnErr("");
        printlnErr(error);
    }

    public void outputException(Throwable e) {
        printlnErr(e);
        if (e.getCause() != null) {
            printlnErr(e.getCause());
        }
    }
}
