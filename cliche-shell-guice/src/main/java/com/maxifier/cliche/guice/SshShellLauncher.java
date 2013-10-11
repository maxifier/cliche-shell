/*
* Copyright (c) 2008-2013 Maxifier Ltd. All Rights Reserved.
*/
package com.maxifier.cliche.guice;

import com.maxifier.cliche.ResourceHostKeyProvider;
import com.maxifier.cliche.ShellFactory;
import com.maxifier.cliche.SshNonInteractiveShellCommand;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Collection;

/**
 * It is intended for launch ssh-server with specified port, name and shellHandlers.<p/>
 * {@link SshShellLauncher#shellHandlers} is collection of shell handlers, which will be used for initializing the new shell,
 * when a new user connects to the ssh-server.
 *
 * @author Igor Yankov (igor.yankov@maxifier.com) (2013-10-11 10:00)
 */
public class SshShellLauncher {
    private final String prompt;
    private final String appName;
    private final int port;
    private final Collection shellHandlers;

    public SshShellLauncher(String prompt, String appName, int port, Collection shellHandlers) {
        this.prompt = prompt;
        this.appName = appName;
        this.port = port;
        this.shellHandlers = shellHandlers;
    }

    public void startSshServer() {
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
        ShellFactory.createSshShell(sshServer, prompt, appName, shellHandlers);
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

    }
}
