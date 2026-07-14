package ac.comet.cometac.checks.impl.badpackets;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;

@CheckData(name = "BadPacketsY", stableKey = "cometac.badpackets.oob_slot", verboseVersion = 1, description = "Sent out of bounds slot id")
public class BadPacketsY extends Check implements PacketCheck {
    public static final VerboseSchema V = VerboseSchema.of("slot:zz");

    public BadPacketsY(CometPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            final int slot = new WrapperPlayClientHeldItemChange(event).getSlot();
            if (slot > 8 || slot < 0) { // ban
                if (flagAndAlert(V.write(verbose()).zz(slot)) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}
