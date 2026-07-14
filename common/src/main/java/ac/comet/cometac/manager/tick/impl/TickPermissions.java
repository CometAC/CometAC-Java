package ac.comet.cometac.manager.tick.impl;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.manager.config.BaseConfigManager;
import ac.comet.cometac.manager.tick.Tickable;
import ac.comet.cometac.player.CometPlayer;

public class TickPermissions implements Tickable {

    @Override
    public void tick() {
        BaseConfigManager config = CometAPI.INSTANCE.getConfigManager();
        int interval = config.getUpdatePermissionTicks();
        if (interval <= 0 || CometAPI.INSTANCE.getTickManager().currentTick % interval != 0) return;

        for (CometPlayer player : CometAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.updatePermissions();
        }
    }
}
