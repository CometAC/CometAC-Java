package ac.comet.cometac.checks.impl.badpackets;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "BadPacketsW", stableKey = "cometac.badpackets.invalid_entity_target", description = "Interacted with non-existent entity", experimental = true)
public class BadPacketsW extends Check {
    public BadPacketsW(CometPlayer player) {
        super(player);
    }
}
