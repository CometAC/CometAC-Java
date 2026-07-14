package ac.comet.cometac.checks.impl.badpackets;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "BadPacketsN", stableKey = "cometac.badpackets.invalid_teleport", setback = 0)
public class BadPacketsN extends Check {
    public BadPacketsN(final CometPlayer player) {
        super(player);
    }
}
