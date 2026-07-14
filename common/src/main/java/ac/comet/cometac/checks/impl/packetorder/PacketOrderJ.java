package ac.comet.cometac.checks.impl.packetorder;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PostPredictionCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

@CheckData(name = "PacketOrderJ", stableKey = "cometac.packetorder.attack_interact_use_order", experimental = true)
public class PacketOrderJ extends Check implements PostPredictionCheck {
    public PacketOrderJ(final CometPlayer player) {
        super(player);
    }

    private int invalid;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT || event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
            // we don't check stabbing here because you don't need to target an entity to stab
            if (player.packetOrderProcessor.isAttacking() && !player.packetOrderProcessor.isInteracting()) {
                if (!player.canSkipTicks()) {
                    if (flagAndAlert() && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                } else {
                    invalid++;
                }
            }
        }
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (!player.canSkipTicks()) return;

        if (player.isTickingReliablyFor(3)) {
            for (; invalid >= 1; invalid--) {
                flagAndAlert();
            }
        }

        invalid = 0;
    }
}
