package ac.comet.cometac.checks.impl.combat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "EntityPierce", stableKey = "cometac.combat.entity_pierce", configName = "EntityPierce", setback = 30)
public class EntityPierce extends Check implements PacketCheck {
    public EntityPierce(CometPlayer player) {
        super(player);
    }
}
