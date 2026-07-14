package ac.comet.cometac.manager.tick.impl;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.manager.tick.Tickable;
import ac.comet.cometac.player.CometPlayer;

public class TickInventory implements Tickable {
    @Override
    public void tick() {
        for (CometPlayer player : CometAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.inventory.inventory.getInventoryStorage().tickWithBukkit();
        }
    }
}
