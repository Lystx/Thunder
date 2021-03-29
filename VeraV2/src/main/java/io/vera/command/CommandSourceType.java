
package io.vera.command;

import io.vera.entity.living.Player;
import io.vera.inventory.Substance;
import io.vera.server.VeraServer;
import io.vera.server.world.Block;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum CommandSourceType {
    PLAYER {
        @Override
        public boolean isTypeOf(CommandSource o) {
            return o instanceof Player;
        }
    }, CONSOLE {
        @Override
        public boolean isTypeOf(CommandSource o) {
            return o instanceof VeraServer;
        }
    }, BLOCK {
        @Override
        public boolean isTypeOf(CommandSource o) {
            return o instanceof Block && ((Block) o).getSubstance() == Substance.COMMAND_BLOCK;
        }
    }, ALL {
        @Override
        public boolean isTypeOf(CommandSource o) {
            return true;
        }
    };

    public abstract boolean isTypeOf(CommandSource o);
}
