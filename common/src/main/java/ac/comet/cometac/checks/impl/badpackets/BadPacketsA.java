package ac.comet.cometac.checks.impl.badpackets;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;

@CheckData(name = "BadPacketsA", stableKey = "cometac.badpackets.duplicate_slot", verboseVersion = 1, description = "Sent duplicate slot id")
public class BadPacketsA extends Check implements PacketCheck {
    public static final VerboseSchema V = VerboseSchema.of("slot:zz");

    private int lastSlot = -1;

    public BadPacketsA(final CometPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            final int slot = new WrapperPlayClientHeldItemChange(event).getSlot();

            if (slot == lastSlot) {
                if (flagAndAlert(V.write(verbose()).zz(slot)) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }

            lastSlot = slot;
        }
    }
}
