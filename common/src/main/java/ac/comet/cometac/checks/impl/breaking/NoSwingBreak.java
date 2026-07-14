package ac.comet.cometac.checks.impl.breaking;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.BlockBreakCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.BlockBreak;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;

@CheckData(name = "NoSwingBreak", stableKey = "cometac.breaking.no_swing_break", description = "Did not swing while breaking block", experimental = true)
public class NoSwingBreak extends Check implements BlockBreakCheck {
    private boolean sentAnimation;
    private boolean sentBreak;

    public NoSwingBreak(CometPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onBlockBreak(BlockBreak blockBreak) {
        if (blockBreak.action != DiggingAction.CANCELLED_DIGGING) {
            sentBreak = true;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            sentAnimation = true;
        }

        if (isTickPacket(event.getPacketType())) {
            if (sentBreak && !sentAnimation) {
                flagAndAlert();
            }

            sentAnimation = sentBreak = false;
        }
    }
}
