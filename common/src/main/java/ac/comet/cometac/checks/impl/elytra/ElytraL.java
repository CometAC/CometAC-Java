package ac.comet.cometac.checks.impl.elytra;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PostPredictionCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;
import ac.comet.cometac.utils.nmsutil.BlockUtil;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;

@CheckData(name = "ElytraL", stableKey = "cometac.elytra.impossible_hover", description = "Checks for impossible elytra hovering", experimental = true, decay = 0.05, setback = 3)
public class ElytraL extends Check implements PostPredictionCheck {

    private int hoverTicks = 0;

    public ElytraL(CometPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;
        if (!player.isGliding && !player.wasGliding) {
            hoverTicks = 0;
            return;
        }

        if (player.packetStateData.lastPacketWasTeleport
                || player.wasTouchingWater
                || player.wasSwimming
                || player.onGround
                || player.lastOnGround
                || player.isFlying
                || player.verticalCollision
                || player.horizontalCollision
                || player.inVehicle()
                || player.isClimbing
                || player.packetStateData.tryingToRiptide
                || player.predictedVelocity.isKnockback()
                || player.predictedVelocity.isExplosion()
                || player.compensatedEntities.getSlowFallingAmplifier().isPresent()
                || player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.LEVITATION).isPresent()
                || BlockUtil.isPlayerInBlockType(player, StateTypes.BUBBLE_COLUMN)
                // Covers cobweb/berry/powder-snow entry, the stuck window, AND the ~5-tick exit recovery where
                // a glider crushed to near-zero velocity is still re-accelerating and looks like hovering.
                || player.uncertaintyHandler.wasAffectedByStuckSpeed()
                || player.getTransactionPing() > 500) {
            hoverTicks = 0;
            return;
        }

        double deltaY = player.y - player.lastY;

        if (Math.abs(deltaY) < 0.001) {
            hoverTicks++;
            if (hoverTicks >= 8) {
                if (flagAndAlert("deltaY=" + String.format("%.5f", deltaY) + " ticks=" + hoverTicks)) {
                    setbackIfAboveSetbackVL();
                }
            }
        } else {
            hoverTicks = Math.max(0, hoverTicks - 2);
            reward();
        }
    }
}
