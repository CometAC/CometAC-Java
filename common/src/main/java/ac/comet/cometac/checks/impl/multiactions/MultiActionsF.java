package ac.comet.cometac.checks.impl.multiactions;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.BlockPlaceCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.BlockBreak;
import ac.comet.cometac.utils.anticheat.update.BlockPlace;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;

import java.util.ArrayList;
import java.util.List;

@CheckData(name = "MultiActionsF", stableKey = "cometac.multiactions.block_and_entity_interact", verboseVersion = 1, description = "Interacting with a block and an entity in the same tick", experimental = true)
public class MultiActionsF extends BlockPlaceCheck {
    public static final VerboseSchema V = VerboseSchema.of("action:vi");

    static final int ACTION_PLACE = 0;
    static final int ACTION_ENTITY = 1;
    static final int ACTION_DIG = 2;

    private final List<FlagData> flags = new ArrayList<>();
    private boolean entity, block;

    public MultiActionsF(CometPlayer player) {
        super(player);
    }

    static String verbose(int action) {
        return switch (action) {
            case ACTION_PLACE -> "place";
            case ACTION_ENTITY -> "entity";
            case ACTION_DIG -> "dig";
            default -> "unknown";
        };
    }

    @Override
    public void onBlockPlace(BlockPlace place) {
        block = true;
        if (entity) {
            if (!player.canSkipTicks()) {
                if (flagAndAlert(V.write(verbose()).vi(ACTION_PLACE)) && shouldModifyPackets() && shouldCancel()) {
                    place.resync();
                }
            } else {
                flags.add(new FlagData(ACTION_PLACE));
            }
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY
                || event.getPacketType() == PacketType.Play.Client.ATTACK
                || event.getPacketType() == PacketType.Play.Client.SPECTATE_ENTITY) {
            entity = true;
            if (block) {
                if (!player.canSkipTicks()) {
                    if (flagAndAlert(V.write(verbose()).vi(ACTION_ENTITY)) && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                } else {
                    flags.add(new FlagData(ACTION_ENTITY));
                }
            }
        }

        if (isTickPacket(event.getPacketType())) {
            block = entity = false;
        }
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (blockBreak.action == DiggingAction.START_DIGGING || blockBreak.action == DiggingAction.FINISHED_DIGGING) {
            block = true;
            if (entity) {
                if (!player.canSkipTicks()) {
                    if (flagAndAlert(V.write(verbose()).vi(ACTION_DIG)) && shouldModifyPackets()) {
                        blockBreak.cancel();
                    }
                } else {
                    flags.add(new FlagData(ACTION_DIG));
                }
            }
        }
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (!player.canSkipTicks()) return;

        if (player.isTickingReliablyFor(3)) {
            for (FlagData data : flags) {
                flagAndAlert(V.write(verbose()).vi(data.action()));
            }
        }

        flags.clear();
    }

    private record FlagData(int action) {
    }
}
