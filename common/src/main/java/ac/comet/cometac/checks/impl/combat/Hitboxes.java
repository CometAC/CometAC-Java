package ac.comet.cometac.checks.impl.combat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "Hitboxes", stableKey = "cometac.combat.hitboxes", setback = 10, decay = 0.15)
public class Hitboxes extends Check {
    public Hitboxes(CometPlayer player) {
        super(player);
    }
}
