package io.lightning.manager.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.User;

@Getter @AllArgsConstructor
public class TicTacToe {

    private final User user;
    private final User enemy;
}
