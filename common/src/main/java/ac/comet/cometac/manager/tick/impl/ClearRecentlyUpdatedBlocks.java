package ac.comet.cometac.manager.tick.impl;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.manager.tick.Tickable;
import ac.comet.cometac.player.CometPlayer;

public class ClearRecentlyUpdatedBlocks implements Tickable {

    private static final int maxTickAge = 2;

    @Override
    public void tick() {
        for (CometPlayer player : CometAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.blockHistory.cleanup(CometAPI.INSTANCE.getTickManager().currentTick - maxTickAge);
        }
    }
}
