package ac.comet.cometac.checks.impl.elytra;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "ElytraN", stableKey = "cometac.elytra.rapid_reactivation", description = "Detects rapid elytra reactivation without position data", experimental = true, setback = 1)
public class ElytraN extends Check implements PacketCheck {

    private long lastPositionTime;
    private int activationsWithoutPosition;
    private double buffer;

    public ElytraN(CometPlayer player) {
        super(player);
        lastPositionTime = System.currentTimeMillis();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (player.packetStateData.didLastMovementIncludePosition) {
                lastPositionTime = System.currentTimeMillis();
                activationsWithoutPosition = 0;
                buffer = Math.max(0, buffer - 0.25);
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            var action = new WrapperPlayClientEntityAction(event);
            if (action.getAction() != WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA) return;

            long msSincePosition = System.currentTimeMillis() - lastPositionTime;

            if (msSincePosition > 500) {
                activationsWithoutPosition++;

                if (activationsWithoutPosition >= 3) {
                    buffer += 1.0;
                    if (buffer > 1.5 && flagAndAlert("acts=" + activationsWithoutPosition + " noPos=" + msSincePosition + "ms")) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                        player.getSetbackTeleportUtil().executeNonSimulatingForceResync();
                    }
                }
            }
        }
    }
}
