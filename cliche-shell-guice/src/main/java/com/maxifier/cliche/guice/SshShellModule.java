package com.maxifier.cliche.guice;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.util.Collection;
import java.util.Collections;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class SshShellModule extends AbstractModule {

    private final String prompt;
    private final String appName;
    private final int port;
    private boolean postponedStart;

    public SshShellModule(String prompt, String appName, int port) {
        this(false, prompt, appName, port);
    }

    public SshShellModule(boolean postponedStart, String prompt, String appName, int port) {
        this.prompt = prompt;
        this.appName = appName;
        this.port = port;
        this.postponedStart = postponedStart;
    }

    @Override
    protected void configure() {
        final Collection shellHandlers = Collections.synchronizedCollection(Lists.newArrayList());

        SshShellLauncher serverStarter = new SshShellLauncher(prompt, appName, port, shellHandlers);
        if (postponedStart) {
            bind(SshShellLauncher.class).toInstance(serverStarter);
        } else {
            serverStarter.startSshServer();
        }
        bindListener(AnnotatedClassMatcher.with(CommandHandler.class),
                new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        encounter.register(new InjectionListener<I>() {
                            @Override
                            public void afterInjection(I injectee) {
                                shellHandlers.add(injectee);
                            }
                        });
                    }
                }
        );
    }


}
