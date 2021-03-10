
package io.vera.command;

public abstract class CommandDispatcher {

    public final boolean isMainCommand(String command) {
        String main = this.getCommand().name();
        return main.equalsIgnoreCase(command);
    }

    public final boolean isAlias(String command) {
        String[] aliases = this.getCommand().aliases();
        for (String string : aliases) {
            if (string != null && string.equalsIgnoreCase(command)) {
                return true;
            }
        }
        return false;
    }

    public abstract String getPlugin();

    public abstract Command getCommand();

    public abstract void run(String cmd, CommandSource source, String[] args);

    public abstract boolean isContainedBy(Class<? extends CommandListener> cls);

}
