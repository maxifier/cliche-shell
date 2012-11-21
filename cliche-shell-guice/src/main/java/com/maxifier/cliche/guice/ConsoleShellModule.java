package com.maxifier.cliche.guice;

import com.maxifier.cliche.Shell;
import com.maxifier.cliche.ShellFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.io.IOException;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class ConsoleShellModule extends AbstractModule {

    private final String promt;
    private final String appName;

    public ConsoleShellModule(String promt, String appName) {
        this.promt = promt;
        this.appName = appName;
    }

    @Override
    protected void configure() {
        final Shell shell;
        try {
            shell = ShellFactory.createConsoleShell(promt, appName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bind(Shell.class).toInstance(shell);
        bindListener(AnnotatedClassMatcher.with(CommandHandler.class),
                new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        encounter.register(new InjectionListener<I>() {
                            @Override
                            public void afterInjection(I injectee) {
                                shell.addMainHandler(injectee, "");
                            }
                        });
                    }
                }
        );
    }

}
