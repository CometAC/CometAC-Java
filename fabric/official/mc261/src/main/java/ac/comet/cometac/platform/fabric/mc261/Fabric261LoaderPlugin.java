package ac.comet.cometac.platform.fabric.mc261;

import ac.comet.cometac.platform.fabric.CometACFabricOfficialLoaderPlugin;
import ac.comet.cometac.platform.fabric.player.FabricPlatformPlayerFactory;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class Fabric261LoaderPlugin extends CometACFabricOfficialLoaderPlugin {

    public Fabric261LoaderPlugin() {
        super(
                new FabricPlatformPlayerFactory(
                        Fabric261PlatformPlayer::new,
                        Fabric261GrimEntity::new,
                        Fabric261PlatformInventory::new
                ),
                new Fabric261PlatformServer(),
                new Fabric261MessageUtil(),
                new Fabric261ConversionUtil()
        );
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_26_1;
    }
}
