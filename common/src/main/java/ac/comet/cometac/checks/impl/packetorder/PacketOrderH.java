package ac.comet.cometac.checks.impl.packetorder;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PostPredictionCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

@CheckData(name = "PacketOrderH", stableKey = "cometac.packetorder.sneak_sprint_order", experimental = true)
public class PacketOrderH extends Check implements PostPredictionCheck {
    public PacketOrderH(final CometPlayer player) {
        super(player);
    }

    private int invalid;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            switch (new WrapperPlayClientEntityAction(event).getAction()) {
                case START_SPRINTING, STOP_SPRINTING -> {
                    if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21_2) && player.packetOrderProcessor.isSneaking()) {
                        if (!player.canSkipTicks()) {
                            flagAndAlert();
                        } else {
                            invalid++;
                        }
                    }
                }
                case START_SNEAKING, STOP_SNEAKING -> {
                    if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2) && player.packetOrderProcessor.isSprinting()) {
                        if (!player.canSkipTicks()) {
                            flagAndAlert();
                        } else {
                            invalid++;
                        }
                    }
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
