package ac.comet.cometac.checks.impl.inventory;

import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.InventoryCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.BlockPlace;

@CheckData(name = "InventoryC", stableKey = "cometac.inventory.place_while_open", setback = 3, description = "Placed a block while inventory is open")
public class InventoryC extends InventoryCheck {

    public InventoryC(CometPlayer player) {
        super(player);
    }

    public void onBlockPlace(final BlockPlace place) {
        // It is not possible to place a block while the inventory is open
        if (player.hasInventoryOpen) {
            if (flagAndAlert()) {
                if (shouldModifyPackets()) {
                    place.resync();
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
