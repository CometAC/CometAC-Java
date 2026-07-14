package ac.comet.cometac.platform.fabric.initables;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.manager.init.start.AbstractTickEndEvent;
import ac.comet.cometac.platform.fabric.FabricServerEvents;
import ac.comet.cometac.player.CometPlayer;

public class FabricTickEndEvent extends AbstractTickEndEvent {

    @Override
    public void start() {
        if (!super.shouldInjectEndTick()) {
            return;
        }

        FabricServerEvents.onEndTick(server -> tickAllPlayers());
    }

    private void tickAllPlayers() {
        for (CometPlayer player : CometAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            if (player.disableGrim) continue;
            super.onEndOfTick(player, true);
        }
    }
}
