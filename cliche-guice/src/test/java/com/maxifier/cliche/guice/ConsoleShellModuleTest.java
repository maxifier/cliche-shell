package com.maxifier.cliche.guice;

import com.maxifier.cliche.Command;
import com.maxifier.cliche.Shell;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class ConsoleShellModuleTest {

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(
                new ConsoleShellModule("test", "Test App"),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Foo.class).asEagerSingleton();
                    }
                });
        Shell shell = injector.getInstance(Shell.class);
        shell.commandLoop();
    }

    @CommandHandler
    public static class Foo {

        @Command
        public int add(int... operands) {
            int sum = 0;
            for (int operand : operands) {
                sum += operand;
            }
            return sum;
        }
    }
}
