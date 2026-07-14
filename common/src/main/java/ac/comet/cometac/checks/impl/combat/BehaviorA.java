package ac.comet.cometac.checks.impl.combat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.RotationCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.RotationUpdate;
import com.github.retrooper.packetevents.protocol.player.GameMode;

// Original check by DarknessAC
// https://github.com/1hendex/DarknessAC
@CheckData(name = "BehaviorA", stableKey = "cometac.combat.zero_pitch", configName = "Behavior", description = "Checks for zero pitch rotations")
public class BehaviorA extends Check implements RotationCheck {

    public BehaviorA(CometPlayer player) {
        super(player);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        if (!player.hasRotatedSinceSpawn
                || player.packetStateData.lastPacketWasTeleport
                || player.gamemode == GameMode.SPECTATOR
                || player.inVehicle()
                || player.compensatedEntities.self.isDead
                || System.currentTimeMillis() - player.joinTime < 5000
                || System.currentTimeMillis() - player.lastAttackTime > 500) {
            return;
        }

        final float pitch = rotationUpdate.getTo().pitch();

        if (pitch == 0) {
            flagAndAlert("");
        }
    }
}
