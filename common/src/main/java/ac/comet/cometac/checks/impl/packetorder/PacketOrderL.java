package ac.comet.cometac.checks.impl.packetorder;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PostPredictionCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

import java.util.ArrayDeque;

@CheckData(name = "PacketOrderL", stableKey = "cometac.packetorder.drop_item_order", experimental = true, verboseVersion = 1)
public class PacketOrderL extends Check implements PostPredictionCheck {
    public static final VerboseSchema V = VerboseSchema.of("action:vi");

    static final int ACTION_INVENTORY = 0;
    static final int ACTION_SWAP = 1;

    public PacketOrderL(final CometPlayer player) {
        super(player);
    }

    private final ArrayDeque<Integer> flags = new ArrayDeque<>();

    static String verbose(int action) {
        return switch (action) {
            case ACTION_INVENTORY -> "inventory";
            case ACTION_SWAP -> "swap";
            default -> "unknown";
        };
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            if (new WrapperPlayClientClientStatus(event).getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
                if (player.packetOrderProcessor.isDropping()) {
                    if (!player.canSkipTicks()) {
                        if (flagAndAlert(V.write(verbose()).vi(ACTION_INVENTORY)) && shouldModifyPackets()) {
                            event.setCancelled(true);
                            player.onPacketCancel();
                        }
                    } else {
                        flags.add(ACTION_INVENTORY);
                    }
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            if (new WrapperPlayClientPlayerDigging(event).getAction() == DiggingAction.SWAP_ITEM_WITH_OFFHAND) {
                if (player.packetOrderProcessor.isDropping()) {
                    if (!player.canSkipTicks()) {
                        if (flagAndAlert(V.write(verbose()).vi(ACTION_SWAP)) && shouldModifyPackets()) {
                            event.setCancelled(true);
                            player.onPacketCancel();
                        }
                    } else {
                        flags.add(ACTION_SWAP);
                    }
                }
            }
        }
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (!player.canSkipTicks()) return;

        if (player.isTickingReliablyFor(3)) {
            for (int action : flags) {
                flagAndAlert(V.write(verbose()).vi(action));
            }
        }

        flags.clear();
    }
}
