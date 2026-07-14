package ac.comet.cometac.platform.bukkit.initables;

import ac.comet.cometac.manager.init.start.StartableInitable;
import ac.comet.cometac.platform.bukkit.CometACBukkitLoaderPlugin;
import ac.comet.cometac.utils.anticheat.Constants;
import io.github.retrooper.packetevents.bstats.bukkit.Metrics;

public class BukkitBStats implements StartableInitable {
    @Override
    public void start() {
        try {
            new Metrics(CometACBukkitLoaderPlugin.LOADER, Constants.BSTATS_PLUGIN_ID);
        } catch (Exception ignored) {}
    }
}
