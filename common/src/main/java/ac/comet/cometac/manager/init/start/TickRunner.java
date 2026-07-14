package ac.comet.cometac.manager.init.start;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.platform.api.Platform;
import ac.comet.cometac.utils.anticheat.LogUtil;

public class TickRunner implements StartableInitable {
    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");

        if (CometAPI.INSTANCE.getPlatform() == Platform.FOLIA) {
            CometAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CometAPI.INSTANCE.getGrimPlugin(), () -> {
                CometAPI.INSTANCE.getTickManager().tickSync();
                CometAPI.INSTANCE.getTickManager().tickAsync();
            }, 1, 1);
        } else {
            CometAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().runAtFixedRate(CometAPI.INSTANCE.getGrimPlugin(), () -> CometAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
            CometAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CometAPI.INSTANCE.getGrimPlugin(), () -> CometAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        }
    }
}
