package ac.comet.cometac.platform.fabric;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.platform.fabric.initables.FabricBStats;
import ac.comet.cometac.platform.fabric.initables.FabricLuckPermsInitable;
import ac.comet.cometac.platform.fabric.initables.FabricTickEndEvent;
import ac.comet.cometac.platform.fabric.inject.FabricMinecraftServerHandle;
import ac.comet.cometac.platform.fabric.scheduler.FabricPlatformScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.util.List;
import java.util.Objects;

public abstract class AbstractCometACFabricEntryPoint<P extends AbstractCometACFabricLoaderPlugin<?, ?, ?, ?, ?, ?>>
        implements PreLaunchEntrypoint, ModInitializer {
    private static volatile FabricMinecraftServerHandle server;

    @Override
    public void onPreLaunch() {
    }

    public static FabricMinecraftServerHandle server() {
        return Objects.requireNonNull(server);
    }

    public static FabricMinecraftServerHandle serverOrNull() {
        return server;
    }

    protected void initialize(
            String entryPointName,
            Class<P> pluginClass,
            boolean allowMissingEntryPoint
    ) {
        List<P> entryPoints = FabricLoader.getInstance().getEntrypoints(entryPointName, pluginClass);
        entryPoints.sort((a, b) -> b.getNativeVersion().getProtocolVersion() - a.getNativeVersion().getProtocolVersion());

        if (entryPoints.isEmpty()) {
            if (allowMissingEntryPoint) return;
            throw new IllegalStateException("No Fabric platform entrypoint found for " + entryPointName);
        }

        P platformLoader = entryPoints.get(0);
        setPlatformLoader(platformLoader);

        CometAPI.INSTANCE.load(
                platformLoader,
                new FabricBStats(),
                new FabricTickEndEvent(),
                new FabricLuckPermsInitable()
        );

        CometAPI.INSTANCE.getCommandService().registerCommands();

        FabricServerEvents.onServerStarting(nativeServer -> {
            setNativeServer(nativeServer);
            server = nativeServer;
            CometAPI.INSTANCE.start();
        });

        FabricServerEvents.onServerStopping(nativeServer -> {
            CometAPI.INSTANCE.stop();
            ((FabricPlatformScheduler) platformLoader.getScheduler()).shutdown();
        });
    }

    protected abstract void setPlatformLoader(P platformLoader);

    protected abstract void setNativeServer(FabricMinecraftServerHandle server);
}
