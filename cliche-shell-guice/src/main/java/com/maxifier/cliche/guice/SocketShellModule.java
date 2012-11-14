package com.maxifier.cliche.guice;

import com.maxifier.cliche.ShellFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class SocketShellModule extends AbstractModule {

    private final String promt;
    private final String appName;
    private final int port;

    public SocketShellModule(String promt, String appName, int port) {
        this.promt = promt;
        this.appName = appName;
        this.port = port;
    }

    @Override
    protected void configure() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final Collection<Object> socketShell = (Collection<Object>) ShellFactory.createSocketShell(serverSocket, promt, appName);
        bindListener(AnnotatedClassMatcher.with(CommandHandler.class),
                new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        encounter.register(new InjectionListener<I>() {
                            @Override
                            public void afterInjection(I injectee) {
                                socketShell.add(injectee);
                            }
                        });
                    }
                }
        );
    }


}
