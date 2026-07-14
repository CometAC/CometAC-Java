package ac.comet.cometac.platform.fabric.mc1171;

import ac.comet.cometac.platform.fabric.AbstractFabricPlatformServer;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.fabric.CometACFabricIntermediaryLoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1171.player.Fabric1170PlatformPlayer;
import ac.comet.cometac.platform.fabric.mc1161.Fabric1140PlatformServer;
import ac.comet.cometac.platform.fabric.mc1161.player.Fabric1161PlatformInventory;
import ac.comet.cometac.platform.fabric.mc1171.entity.Fabric1170GrimEntity;
import ac.comet.cometac.platform.fabric.mc1161.util.convert.Fabric1140ConversionUtil;
import ac.comet.cometac.platform.fabric.mc1161.util.convert.Fabric1161MessageUtil;
import ac.comet.cometac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.comet.cometac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.comet.cometac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.comet.cometac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;


public class CometACFabric1170LoaderPlugin extends CometACFabricIntermediaryLoaderPlugin {

    public CometACFabric1170LoaderPlugin() {
        this(CometACFabricIntermediaryLoaderPlugin::createCommandArguments,
                new FabricPlatformPlayerFactory(
                        Fabric1170PlatformPlayer::new,
                        Fabric1170GrimEntity::new,
                        Fabric1161PlatformInventory::new
                ),
                PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_17)
                        ? new Fabric1171PlatformServer() : new Fabric1140PlatformServer(),
                new Fabric1161MessageUtil(),
                new Fabric1140ConversionUtil()
        );
    }

    protected CometACFabric1170LoaderPlugin(LazyHolder<CloudPlatformCommandArguments> commandArguments,
                                           FabricPlatformPlayerFactory playerFactory,
                                           AbstractFabricPlatformServer platformServer,
                                           IFabricMessageUtil fabricMessageUtil,
                                           IFabricConversionUtil fabricConversionUtil) {
        super(
                commandArguments,
                playerFactory,
                platformServer,
                fabricMessageUtil,
                fabricConversionUtil
        );
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_17_1;
    }
}
