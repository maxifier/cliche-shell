package com.maxifier.cliche.example;

import com.maxifier.cliche.ShellFactory;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;

/**
 * @author aleksey.didik@maxifier.com Aleksey Didik
 */


public class ConsoleReaderTest {
    public static void main(String[] args) throws Exception {
        SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setPublickeyAuthenticator(new PublickeyAuthenticator() {
            @Override
            public boolean authenticate(String username, PublicKey key, ServerSession session) {
                return true;
            }
        });
//        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
//            @Override
//            public boolean authenticate(String username, String password, ServerSession session) {
//                return true;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
        sshServer.setPort(12890);
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
        ShellFactory.createSshShell(sshServer, "welcome", "Test ssh app with console reader", new Example());
        sshServer.start();


    }

}
