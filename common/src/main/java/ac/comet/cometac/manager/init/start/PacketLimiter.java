package ac.comet.cometac.manager.init.start;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.player.CometPlayer;

public class PacketLimiter implements StartableInitable {
    @Override
    public void start() {
        CometAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CometAPI.INSTANCE.getGrimPlugin(), () -> {
            for (CometPlayer player : CometAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                // Avoid concurrent reading on an integer as it's results are unknown
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }
}
