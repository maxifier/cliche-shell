/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package com.maxifier.cliche;

import com.maxifier.cliche.util.ArrayHashMultiMap;
import com.maxifier.cliche.util.EmptyMultiMap;
import com.maxifier.cliche.util.MultiMap;

import com.google.common.collect.Lists;
import jline.console.ConsoleReader;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ASG
 */
public class ShellFactory {

    private ShellFactory() {
    } // this class has only static methods.

    /**
     * One of facade methods for operating the Shell.
     * <p/>
     * Run the obtained Shell with commandLoop().
     *
     * @param prompt   Prompt to be displayed
     * @param appName  The app name string
     * @param handlers Command handlers
     * @return Shell that can be either further customized or run directly by calling commandLoop().
     */
    public static Shell createConsoleShell(String prompt, String appName, Object... handlers) throws IOException {
        ConsoleIO io = new ConsoleIO();

        List<String> path = new ArrayList<String>(1);
        path.add(prompt);

        MultiMap<String, Object> modifAuxHandlers = new ArrayHashMultiMap<String, Object>();
        modifAuxHandlers.put("!", io);

        Shell theShell = new Shell(new Shell.Settings(io, io, modifAuxHandlers, false),
                new CommandTable(new DashJoinedNamer(true)), path);
        theShell.setAppName(appName);

        theShell.addMainHandler(theShell, "!");
        theShell.addMainHandler(new HelpCommandHandler(), "?");
        for (Object h : handlers) {
            theShell.addMainHandler(h, "");
        }

        return theShell;
    }

    public static Collection<?> createSocketShell(final ServerSocket serverSocket,
                                                  final String promt,
                                                  final String appName,
                                                  final Object... handlers) {
        final Collection<?> handlersCollection = Lists.newArrayList(handlers);
        Thread shellThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Executor executor = Executors.newCachedThreadPool();
                while (true) {
                    try {
                        final Socket socket = serverSocket.accept();
                        executor.execute(new Runnable() {
                            public void run() {
                                try {
                                    PrintStream out = new PrintStream(socket.getOutputStream());
                                    ConsoleReader consoleReader = new ConsoleReader(socket.getInputStream(), out);
                                    ConsoleIO io = new ConsoleIO(consoleReader, out, out);

                                    List<String> path = new ArrayList<String>(1);
                                    path.add(promt);

                                    MultiMap<String, Object> modifAuxHandlers = new ArrayHashMultiMap<String, Object>();
                                    modifAuxHandlers.put("!", io);

                                    Shell theShell = new Shell(new Shell.Settings(io, io, modifAuxHandlers, false),
                                            new CommandTable(new DashJoinedNamer(true)), path);
                                    theShell.setAppName(appName);

                                    theShell.addMainHandler(theShell, "!");
                                    theShell.addMainHandler(new HelpCommandHandler(), "?");
                                    for (Object h : handlersCollection) {
                                        theShell.addMainHandler(h, "");
                                    }
                                    theShell.commandLoop();
                                    out.println("Bye!");
                                } catch (IOException e) {
                                } finally {
                                    try {

                                        socket.close();
                                    } catch (IOException ignore) {
                                    }
                                }
                            }
                        });
                    } catch (IOException ignore) {
                    }
                }
            }
        }, "Shell thread");
        shellThread.setDaemon(true);
        shellThread.start();
        return handlersCollection;
    }

    public static Collection<?> createSshShell(final SshServer server,
                                               final String promt,
                                               final String appName,
                                               final Object... handlers) {
        final Collection<?> handlersCollection = Lists.newArrayList(handlers);
        Thread shellThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final ExecutorService executor = Executors.newCachedThreadPool();
                server.setShellFactory(new Factory<org.apache.sshd.server.Command>() {
                    @Override
                    public Command create() {
                        return new SshShellCommand(executor, promt, appName, handlersCollection);
                    }
                });
            }
        }, "Shell thread");
        shellThread.setDaemon(true);
        shellThread.start();
        return handlersCollection;


    }

    /**
     * Facade method for operating the Shell allowing specification of auxiliary
     * handlers (i.e. handlers that are to be passed to all subshells).
     * <p/>
     * Run the obtained Shell with commandLoop().
     *
     * @param prompt      Prompt to be displayed
     * @param appName     The app name string
     * @param mainHandler Main command handler
     * @param auxHandlers Aux handlers to be passed to all subshells.
     * @return Shell that can be either further customized or run directly by calling commandLoop().
     */
    public static Shell createConsoleShell(String prompt, String appName, Object mainHandler,
                                           MultiMap<String, Object> auxHandlers) throws IOException {
        ConsoleIO io = new ConsoleIO();

        List<String> path = new ArrayList<String>(1);
        path.add(prompt);

        MultiMap<String, Object> modifAuxHandlers = new ArrayHashMultiMap<String, Object>(auxHandlers);
        modifAuxHandlers.put("!", io);

        Shell theShell = new Shell(new Shell.Settings(io, io, modifAuxHandlers, false),
                new CommandTable(new DashJoinedNamer(true)), path);
        theShell.setAppName(appName);

        theShell.addMainHandler(theShell, "!");
        theShell.addMainHandler(new HelpCommandHandler(), "?");
        theShell.addMainHandler(mainHandler, "");

        return theShell;
    }

    /**
     * Facade method for operating the Shell.
     * <p/>
     * Run the obtained Shell with commandLoop().
     *
     * @param prompt      Prompt to be displayed
     * @param appName     The app name string
     * @param mainHandler Command handler
     * @return Shell that can be either further customized or run directly by calling commandLoop().
     */
    public static Shell createConsoleShell(String prompt, String appName, Object mainHandler) throws IOException {
        return createConsoleShell(prompt, appName, mainHandler, new EmptyMultiMap<String, Object>());
    }

    /**
     * Facade method facilitating the creation of subshell.
     * Subshell is created and run inside Command method and shares the same IO and naming strategy.
     * <p/>
     * Run the obtained Shell with commandLoop().
     *
     * @param pathElement sub-prompt
     * @param parent      Shell to be subshell'd
     * @param appName     The app name string
     * @param mainHandler Command handler
     * @param auxHandlers Aux handlers to be passed to all subshells.
     * @return subshell
     */
    public static Shell createSubshell(String pathElement, Shell parent, String appName, Object mainHandler,
                                       MultiMap<String, Object> auxHandlers) {

        List<String> newPath = new ArrayList<String>(parent.getPath());
        newPath.add(pathElement);

        Shell subshell = new Shell(parent.getSettings().createWithAddedAuxHandlers(auxHandlers),
                new CommandTable(parent.getCommandTable().getNamer()), newPath);

        subshell.setAppName(appName);
        subshell.addMainHandler(subshell, "!");
        subshell.addMainHandler(new HelpCommandHandler(), "?");

        subshell.addMainHandler(mainHandler, "");
        return subshell;
    }

    /**
     * Facade method facilitating the creation of subshell.
     * Subshell is created and run inside Command method and shares the same IO and naming strtategy.
     * <p/>
     * Run the obtained Shell with commandLoop().
     *
     * @param pathElement sub-prompt
     * @param parent      Shell to be subshell'd
     * @param appName     The app name string
     * @param mainHandler Command handler
     * @return subshell
     */
    public static Shell createSubshell(String pathElement, Shell parent, String appName, Object mainHandler) {
        return createSubshell(pathElement, parent, appName, mainHandler, new EmptyMultiMap<String, Object>());
    }


}
