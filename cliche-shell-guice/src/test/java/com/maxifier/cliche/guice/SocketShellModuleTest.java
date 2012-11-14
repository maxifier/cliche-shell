package com.maxifier.cliche.guice;

import com.maxifier.cliche.Command;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class SocketShellModuleTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        Injector injector = Guice.createInjector(
                new SocketShellModule("test", "Test App", 12891),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Foo.class).asEagerSingleton();
                    }
                });
        Thread.sleep(50000);
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
