package com.maxifier.cliche.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.maxifier.cliche.ResourceHostKeyProvider;
import com.maxifier.cliche.ShellFactory;
import com.maxifier.cliche.SshNonInteractiveShellCommand;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.keyprovider.ResourceKeyPairProvider;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Collection;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public class SshShellModule extends AbstractModule {

    private final String promt;
    private final String appName;
    private final int port;

    public SshShellModule(String promt, String appName, int port) {
        this.promt = promt;
        this.appName = appName;
        this.port = port;
    }

    @Override
    protected void configure() {
        SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setPublickeyAuthenticator(new PublickeyAuthenticator() {
            @Override
            public boolean authenticate(String username, PublicKey key, ServerSession session) {
                return true;
            }
        });
        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession session) {
                return true;
            }
        });
        sshServer.setPort(port);
        sshServer.setKeyPairProvider(new ResourceHostKeyProvider(".hostkey"));
        final Collection shellHandlers = ShellFactory.createSshShell(sshServer, promt, appName);
        sshServer.setCommandFactory(new CommandFactory() {
            @Override
            public Command createCommand(String command) {
                return new SshNonInteractiveShellCommand(command, shellHandlers);
            }
        });
        try {
            sshServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
