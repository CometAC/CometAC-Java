package ac.comet.cometac.platform.fabric.mc1194;

import ac.comet.cometac.platform.fabric.AbstractFabricPlatformServer;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.fabric.CometACFabricIntermediaryLoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1171.CometACFabric1170LoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1171.player.Fabric1170PlatformPlayer;
import ac.comet.cometac.platform.fabric.mc1194.convert.Fabric1190MessageUtil;
import ac.comet.cometac.platform.fabric.mc1194.entity.Fabric1194GrimEntity;
import ac.comet.cometac.platform.fabric.mc1194.player.Fabric1193PlatformInventory;
import ac.comet.cometac.platform.fabric.mc1161.player.Fabric1161PlatformInventory;
import ac.comet.cometac.platform.fabric.mc1161.util.convert.Fabric1140ConversionUtil;
import ac.comet.cometac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.comet.cometac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.comet.cometac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.comet.cometac.utils.lazy.LazyHolder;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;


public class CometACFabric1190LoaderPlugin extends CometACFabric1170LoaderPlugin {

    public CometACFabric1190LoaderPlugin() {
        this(
            CometACFabricIntermediaryLoaderPlugin::createCommandArguments,
            new FabricPlatformPlayerFactory(
                    Fabric1170PlatformPlayer::new,
                    Fabric1194GrimEntity::new,
                    PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_19_2)
                            ? Fabric1193PlatformInventory::new : Fabric1161PlatformInventory::new
            ),
            new Fabric1190PlatformServer(),
            new Fabric1190MessageUtil(),
            new Fabric1140ConversionUtil()
        );
    }

    protected CometACFabric1190LoaderPlugin(
            LazyHolder<CloudPlatformCommandArguments> commandArguments,
            FabricPlatformPlayerFactory platformPlayerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil) {
        super(commandArguments, platformPlayerFactory, platformServer, fabricMessageUtil, fabricConversionUtil);
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_19_4;
    }
}
