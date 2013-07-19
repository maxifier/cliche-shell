package com.maxifier.cliche;

import com.maxifier.cliche.util.EmptyMultiMap;
import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.history.FileHistory;
import jline.console.history.History;
import jline.console.history.PersistentHistory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author aleksey.didik@maxifier.com Aleksey Didik
 */
public class SshNonInteractiveShellCommand implements Command {

    private final String command;
    private final Collection<?> handlersCollection;
    private InputStream in;
    private PrintStream out;
    private PrintStream err;
    private ExitCallback callback;
    private ConsoleReader consoleReader;

    public SshNonInteractiveShellCommand(String command,
                                         Collection<?> handlersCollection) {
        this.command = command;

        this.handlersCollection = handlersCollection;
    }

    @Override
    public void setInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public void setOutputStream(OutputStream out) {
        this.out = new PrintStream(new TtyFilterOutputStream(out));
    }

    @Override
    public void setErrorStream(OutputStream err) {
        this.err = new PrintStream(err);
    }

    @Override
    public void setExitCallback(ExitCallback callback) {
        this.callback = callback;
    }

    @Override
    public void start(Environment env) throws IOException {
        try {
            consoleReader = new ConsoleReader(in, out);
            consoleReader.setBellEnabled(true);
            consoleReader.setHistory(new FileHistory(new File(System.getProperty("user.home"), ".clhistory")));
            consoleReader.setHistoryEnabled(true);
            consoleReader.setExpandEvents(false);

            ConsoleIO io = new ConsoleIO(consoleReader, out, out);
            List<String> path = new ArrayList<String>(1);
            path.add("");
            Shell theShell = new Shell(new Shell.Settings(io, io, new EmptyMultiMap(), false),
                    new CommandTable(new DashJoinedNamer(true)), path);
            theShell.setAppName("");
            theShell.addMainHandler(theShell, "!");
            theShell.addMainHandler(new HelpCommandHandler(), "?");
            for (Object h : handlersCollection) {
                theShell.addMainHandler(h, "");
            }
            consoleReader.addCompleter(
                    new AggregateCompleter(
                            new StringsCompleter(theShell.getCommandTable().getAbbreviates("")),
                            new StringsCompleter(theShell.getCommandTable().getCommandsNames(""))));
            theShell.processLine(command);
            out.flush();
            callback.onExit(0);
        } catch (Exception e) {
            callback.onExit(1, e.getMessage());
        }

    }

    void flushHistory() {
        History history = consoleReader.getHistory();
        if (history instanceof PersistentHistory) {
            try {
                ((PersistentHistory) history).flush();
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public void destroy() {
        flushHistory();
    }


    protected class TtyFilterOutputStream extends FilterOutputStream {
        public TtyFilterOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int c) throws IOException {
            if (c == '\n') {
                super.write('\r');
            }/* else if (c == '\r') {
                c = '\n';
            }*/
            super.write(c);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            for (int i = off; i < len; i++) {
                write(b[i]);
            }
        }
    }

}
