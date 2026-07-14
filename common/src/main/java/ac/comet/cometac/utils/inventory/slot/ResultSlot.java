package ac.comet.cometac.utils.inventory.slot;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.inventory.InventoryStorage;
import com.github.retrooper.packetevents.protocol.item.ItemStack;

public class ResultSlot extends Slot {

    public ResultSlot(InventoryStorage container, int slot) {
        super(container, slot);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public void onTake(CometPlayer player, ItemStack itemStack) {
        // Resync the player's inventory
    }
}
