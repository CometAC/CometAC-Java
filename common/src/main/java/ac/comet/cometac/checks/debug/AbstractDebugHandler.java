package ac.comet.cometac.checks.debug;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.player.CometPlayer;

public abstract class AbstractDebugHandler extends Check {
    public AbstractDebugHandler(CometPlayer player) {
        super(player);
    }

    public abstract boolean toggleListener(CometPlayer player);

    public abstract boolean toggleConsoleOutput();
}
