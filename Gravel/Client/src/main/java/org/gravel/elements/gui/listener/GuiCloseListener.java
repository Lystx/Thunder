package org.gravel.elements.gui.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gravel.GravelClient;
import org.gravel.elements.gui.Gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@AllArgsConstructor @Getter
public class GuiCloseListener extends WindowAdapter {

    private final Gui gui;

    @Override
    public void windowClosed(WindowEvent e) {
        gui.getGravelClient().shutdown();
    }
}
