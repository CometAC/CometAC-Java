package ac.comet.cometac.checks.impl.combat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "WallHit", stableKey = "cometac.combat.wall_hit", configName = "WallHit", setback = 20)
public class WallHit extends Check implements PacketCheck {
    public WallHit(CometPlayer player) {
        super(player);
    }
}
