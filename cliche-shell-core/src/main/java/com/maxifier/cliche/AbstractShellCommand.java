package com.maxifier.cliche;

/**
 * @author aleksey.didik@maxifier.com (Aleksey Didik)
 */
public abstract class AbstractShellCommand {

    private String prefix;
    private String name;
    private String description;
    private ShellCommandParamSpec[] paramSpecs;
    private String abbreviation;
    private String header;

    public AbstractShellCommand(String prefix, String name, ShellCommandParamSpec[] parameters) {
        this.prefix = prefix;
        this.name = name;
        this.paramSpecs = parameters;
    }

    public abstract Object invoke(Object[] parameters) throws CLIException;

    public boolean canBeDenotedBy(String commandName) {
        return commandName.equals(prefix + name) || commandName.equals(prefix + abbreviation);
    }

    public int getArity() {
        return paramSpecs.length;
    }

    public String getDescription() {
        return description;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ShellCommandParamSpec[] getParamSpecs() {
        return paramSpecs;
    }

    public boolean startsWith(String prefix) {
        return (this.prefix + abbreviation).startsWith(prefix) ||
                (this.prefix + name).startsWith(prefix);
    }

    @Override
    public String toString() {
        return prefix + name + "\t" + (abbreviation != null ? prefix + abbreviation : "") + "\t" +  description;
    }

    public String getHeader() {
        return header;
    }
}
