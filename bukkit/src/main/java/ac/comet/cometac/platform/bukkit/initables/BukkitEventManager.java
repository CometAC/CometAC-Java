package ac.comet.cometac.platform.bukkit.initables;

import ac.comet.cometac.manager.init.start.StartableInitable;
import ac.comet.cometac.platform.bukkit.CometACBukkitLoaderPlugin;
import ac.comet.cometac.platform.bukkit.events.PistonEvent;
import ac.comet.cometac.utils.anticheat.LogUtil;
import org.bukkit.Bukkit;

public class BukkitEventManager implements StartableInitable {
    public void start() {
        LogUtil.info("Registering singular bukkit event... (PistonEvent)");

        Bukkit.getPluginManager().registerEvents(new PistonEvent(), CometACBukkitLoaderPlugin.LOADER);
    }
}
