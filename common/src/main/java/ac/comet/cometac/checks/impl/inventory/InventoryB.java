package ac.comet.cometac.checks.impl.inventory;

import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.InventoryCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

@CheckData(name = "InventoryB", stableKey = "cometac.inventory.dig_while_open", setback = 3, description = "Started digging blocks while inventory is open")
public class InventoryB extends InventoryCheck {
    public InventoryB(CometPlayer player) {
        super(player);
    }

    public void handle(PacketReceiveEvent event, WrapperPlayClientPlayerDigging wrapper) {
        if (wrapper.getAction() != DiggingAction.START_DIGGING) return;

        // Is not possible to start digging a block while the inventory is open.
        if (player.hasInventoryOpen) {
            if (flagAndAlert()) {
                // Cancel the packet
                if (shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
                if (!isNoSetbackPermission()) {
                    closeInventory();
                }
            }
        } else {
            reward();
        }
    }
}
