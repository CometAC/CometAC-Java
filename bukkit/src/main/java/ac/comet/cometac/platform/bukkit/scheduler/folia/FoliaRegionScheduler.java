package ac.comet.cometac.platform.bukkit.scheduler.folia;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.comet.cometac.platform.api.scheduler.RegionScheduler;
import ac.comet.cometac.platform.api.scheduler.TaskHandle;
import ac.comet.cometac.platform.api.world.PlatformWorld;
import ac.comet.cometac.platform.bukkit.CometACBukkitLoaderPlugin;
import ac.comet.cometac.platform.bukkit.world.BukkitPlatformWorld;
import ac.comet.cometac.utils.math.Location;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class FoliaRegionScheduler implements RegionScheduler {

    private final io.papermc.paper.threadedregions.scheduler.RegionScheduler regionScheduler = Bukkit.getRegionScheduler();

    @Override
    public void execute(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
        regionScheduler.execute(CometACBukkitLoaderPlugin.LOADER, ((BukkitPlatformWorld) world).bukkitWorld(), chunkX, chunkZ, task);
    }

    @Override
    public void execute(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
        execute(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task) {
        return new FoliaTaskHandle(regionScheduler.run(
                CometACBukkitLoaderPlugin.LOADER,
                ((BukkitPlatformWorld) world).bukkitWorld(),
                chunkX,
                chunkZ,
                ignored -> task.run()
        ));
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task) {
        return run(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task);
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long delayTicks) {
        return new FoliaTaskHandle(regionScheduler.runDelayed(
                CometACBukkitLoaderPlugin.LOADER,
                ((BukkitPlatformWorld) world).bukkitWorld(),
                chunkX,
                chunkZ,
                ignored -> task.run(),
                delayTicks
        ));
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long delayTicks) {
        return runDelayed(plugin, location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, task, delayTicks);
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull PlatformWorld world, int chunkX, int chunkZ, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
        return new FoliaTaskHandle(regionScheduler.runAtFixedRate(
                CometACBukkitLoaderPlugin.LOADER,
                ((BukkitPlatformWorld) world).bukkitWorld(),
                chunkX,
                chunkZ,
                ignored -> task.run(),
                initialDelayTicks,
                periodTicks
        ));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Location location, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
        return runAtFixedRate(
                plugin,
                location.getWorld(),
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4,
                task,
                initialDelayTicks,
                periodTicks
        );
    }
}
