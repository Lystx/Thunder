package de.lystx.messenger.manager.console;

import de.ingotstudios.terminal.IngotTerminal;
import de.ingotstudios.terminal.abstracts.IngotAbstractHandler;
import de.ingotstudios.terminal.model.terminal.TerminalColor;
import de.lystx.messenger.MessageAPI;
import de.lystx.messenger.manager.setup.AbstractSetup;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Console {

    private final IngotTerminal ingotTerminal;
    private final String promt;

    @Setter
    private AbstractSetup<?> setup;

    public Console(String promt) {
        this.ingotTerminal = new IngotTerminal(new IngotAbstractHandler() {

            @Override
            public void handle(String[] strings) {
                if (setup == null) {
                    MessageAPI.getInstance().getCommandManager().execute(strings);
                } else {
                    setup.read(strings);
                }
            }
        });
        this.ingotTerminal.setClearEnabled(false);
        this.promt = this.stripColor(promt);
        this.ingotTerminal.setPrompt(this.promt);
    }

    public void sendMessage(String prefix, String message) {
        this.ingotTerminal.setPrompt(stripColor( "§f[&e" + prefix + "§f] &7"));
        final String msg = this.stripColor(message + "§f");
        this.ingotTerminal.write(msg, this.setup != null);
        this.ingotTerminal.setPrompt(this.promt);
    }

    public void sendMessage(String message) {
        this.ingotTerminal.setPrompt("");
        final String msg = this.stripColor(message);
        this.ingotTerminal.write(msg, false);
        this.ingotTerminal.setPrompt(this.promt);
    }


    public void clearScreen() {
        this.ingotTerminal.clearScreen();
    }

    public String stripColor(String input) {
        input = this.stripColor(input, '&');
        input = this.stripColor(input, '§');
        return input;
    }
    
    public String stripColor(String input, char c) {
        input = input.replace(c + "a", TerminalColor.BRIGHT_GREEN);
        input = input.replace(c + "b", TerminalColor.BRIGHT_CYAN);
        input = input.replace(c + "c", TerminalColor.BRIGHT_RED);
        input = input.replace(c + "d", TerminalColor.BRIGHT_MAGENTA);
        input = input.replace(c + "e", TerminalColor.YELLOW);
        input = input.replace(c + "f", TerminalColor.BRIGHT_WHITE);
        input = input.replace(c + "1", TerminalColor.BLUE);
        input = input.replace(c + "2", TerminalColor.GREEN);
        input = input.replace(c + "3", "");
        input = input.replace(c + "4", TerminalColor.RED);
        input = input.replace(c + "5", TerminalColor.MAGENTA);
        input = input.replace(c + "6", TerminalColor.YELLOW);
        input = input.replace(c + "7", TerminalColor.BRIGHT_BLACK);
        input = input.replace(c + "8", TerminalColor.BLACK);
        input = input.replace(c + "9", TerminalColor.BRIGHT_BLUE);
        return input;
    }

    public void stop() {
        this.ingotTerminal.interrupt();
    }
}
