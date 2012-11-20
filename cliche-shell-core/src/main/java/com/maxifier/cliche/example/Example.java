/*
 * This example code is in public domain.
 */

package com.maxifier.cliche.example;

import com.maxifier.cliche.Command;
import com.maxifier.cliche.InputConverter;
import com.maxifier.cliche.OutputConverter;
import com.maxifier.cliche.Param;
import com.maxifier.cliche.Shell;
import com.maxifier.cliche.ShellDependent;
import com.maxifier.cliche.ShellFactory;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

/**
 * 'more advanced' example.
 *
 * @author ASG
 */
public class Example implements ShellDependent {


    @Command(description="Varargs example")
    public Integer add(
            @Param(name="numbers", description="some numbers to add")
            Integer... numbers) {

        int result = 0;
        for (int i : numbers) {
            result += i;
        }
        return result;
    }

    public static final InputConverter[] CLI_INPUT_CONVERTERS = {

        // You can use Input Converters to support named constants
        new InputConverter() {
            public Integer convertInput(String original, Class toClass) throws Exception {
                if (toClass.equals(Integer.class)) {
                    if (original.equals("one")) return 1;
                    if (original.equals("two")) return 2;
                    if (original.equals("three")) return 3;
                }
                return null;
            }
        }

    };

    public static final OutputConverter[] CLI_OUTPUT_CONVERTERS = {
        
        new OutputConverter() {
            public Object convertOutput(Object o) {
                if (o.getClass().equals(Integer.class)) {
                    int num = (Integer) o;

                    if (num == 1) return "one";
                    if (num == 2) return "two";
                    if (num == 3) return "three";
                }
                return null;
            }
        }

    };


    // The shell which runs us. Needed to create subshells.
    private Shell shell;

    // to get the shell field set
    public void cliSetShell(Shell shell) {
        this.shell = shell;
    }

    @Command(description="Illustrates the concept of subshells, that can be used " +
            "to create a tree-like navigation")
    public void Hello() throws IOException {
        ShellFactory.createSubshell("hello", shell, "That 'Hello, World!' example", new HelloWorld())
                .commandLoop();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("line.separator", "\r\n");
        ShellFactory.createSocketShell(new ServerSocket(12890), "example", "The Cliche Shell example\n" +
                "Enter ?l to list available commands.", new Example());
        Thread.sleep(40545345L);
    }

    @Command
    public String longString() {
        return "Hello\r\nworld!\r\n And You too!";
    }


    @Command
    public List<String> listString() {
        return Lists.newArrayList("Hello", "world", "and", "you");
    }

}
