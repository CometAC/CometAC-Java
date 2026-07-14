package ac.comet.cometac.platform.fabric;

import ac.comet.cometac.platform.api.PlatformServer;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.platform.fabric.player.FabricOfflineProfile;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFabricPlatformServer implements PlatformServer {

    public abstract int getOperatorPermissionLevel();

    public abstract boolean hasPermission(Sender sender, int level);

    @Override
    public String getPlatformImplementationString() {
        return "Fabric " + FabricLoader.getInstance().getModContainer("fabricloader").orElseThrow().getMetadata().getVersion().getFriendlyString() + " (MC: " + AbstractCometACFabricEntryPoint.server().getServerVersion() + ")";
    }

    @Override
    public Sender getConsoleSender() {
        return AbstractCometACFabricEntryPoint.server().createCommandSender();
    }

    @Override
    public void registerOutgoingPluginChannel(String name) {
    }

    @Nullable
    public abstract FabricOfflineProfile getProfileByName(String name);
}
