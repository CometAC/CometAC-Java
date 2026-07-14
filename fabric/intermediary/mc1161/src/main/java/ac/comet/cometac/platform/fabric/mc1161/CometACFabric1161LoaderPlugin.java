package ac.comet.cometac.platform.fabric.mc1161;

import ac.comet.cometac.platform.fabric.AbstractFabricPlatformServer;
import ac.comet.cometac.platform.fabric.CometACFabricIntermediaryLoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1161.entity.Fabric1161GrimEntity;
import ac.comet.cometac.platform.fabric.mc1161.player.Fabric1161PlatformInventory;
import ac.comet.cometac.platform.fabric.mc1161.player.Fabric1161PlatformPlayer;
import ac.comet.cometac.platform.fabric.mc1161.util.convert.Fabric1140ConversionUtil;
import ac.comet.cometac.platform.fabric.mc1161.util.convert.Fabric1161MessageUtil;
import ac.comet.cometac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.comet.cometac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.comet.cometac.platform.fabric.utils.message.IFabricMessageUtil;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class CometACFabric1161LoaderPlugin extends CometACFabricIntermediaryLoaderPlugin {

    public CometACFabric1161LoaderPlugin() {
        this(
            new FabricPlatformPlayerFactory(
                Fabric1161PlatformPlayer::new,
                Fabric1161GrimEntity::new,
                Fabric1161PlatformInventory::new
            ),
            new Fabric1140PlatformServer(),
            new Fabric1161MessageUtil(),
            new Fabric1140ConversionUtil()
        );
    }

    protected CometACFabric1161LoaderPlugin(
            FabricPlatformPlayerFactory playerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil
    ) {
        super(CometACFabricIntermediaryLoaderPlugin::createCommandArguments,
            playerFactory,
            platformServer,
            fabricMessageUtil,
            fabricConversionUtil
        );
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_16_1;
    }
}
