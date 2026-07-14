package ac.comet.cometac.platform.fabric;

import ac.comet.cometac.platform.fabric.inject.FabricMinecraftServerHandle;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class CometACFabricIntermediaryEntryPoint extends AbstractCometACFabricEntryPoint<CometACFabricIntermediaryLoaderPlugin> {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(FabricServerEvents::fireServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(FabricServerEvents::fireServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(FabricServerEvents::fireEndTick);
        initialize(
                "cometMainLoad",
                CometACFabricIntermediaryLoaderPlugin.class,
                false
        );
    }

    @Override
    protected void setPlatformLoader(CometACFabricIntermediaryLoaderPlugin platformLoader) {
        CometACFabricIntermediaryLoaderPlugin.LOADER = platformLoader;
    }

    @Override
    protected void setNativeServer(FabricMinecraftServerHandle server) {
        CometACFabricIntermediaryLoaderPlugin.FABRIC_SERVER = (MinecraftServer) server;
    }
}
