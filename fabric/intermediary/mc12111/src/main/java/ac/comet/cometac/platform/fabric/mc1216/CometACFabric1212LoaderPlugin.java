package ac.comet.cometac.platform.fabric.mc1216;

import ac.comet.cometac.platform.fabric.command.FabricPlayerSelectorParser;
import ac.comet.cometac.platform.fabric.manager.FabricCloudPlatformCommandArguments;
import ac.comet.cometac.platform.fabric.mc1194.CometACFabric1190LoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1194.entity.Fabric1194GrimEntity;
import ac.comet.cometac.platform.fabric.mc1194.player.Fabric1193PlatformInventory;
import ac.comet.cometac.platform.fabric.mc1205.Fabric1203PlatformServer;
import ac.comet.cometac.platform.fabric.mc1205.convert.Fabric1200MessageUtil;
import ac.comet.cometac.platform.fabric.mc1205.convert.Fabric1205ConversionUtil;
import ac.comet.cometac.platform.fabric.mc1216.convert.Fabric1216ConversionUtil;
import ac.comet.cometac.platform.fabric.mc1216.player.Fabric1212PlatformPlayer;
import ac.comet.cometac.platform.fabric.mc1216.player.Fabric1215PlatformInventory;
import ac.comet.cometac.platform.fabric.player.FabricPlatformPlayerFactory;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class CometACFabric1212LoaderPlugin extends CometACFabric1190LoaderPlugin {

    public CometACFabric1212LoaderPlugin() {
        super(
                CometACFabric1212LoaderPlugin::createCommandArguments,
                new FabricPlatformPlayerFactory(
                        Fabric1212PlatformPlayer::new,
                        Fabric1194GrimEntity::new,
                        PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_21_4)
                            ? Fabric1215PlatformInventory::new : Fabric1193PlatformInventory::new
                ),
                PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_21_10) ?
                        new Fabric12111PlatformServer() : new Fabric1203PlatformServer(),
                new Fabric1200MessageUtil(),
                PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_21_5)
                        ? new Fabric1216ConversionUtil() : new Fabric1205ConversionUtil()
        );
    }

    public static FabricCloudPlatformCommandArguments createCommandArguments() {
        return new FabricCloudPlatformCommandArguments(new FabricPlayerSelectorParser<>(
                selector -> LOADER.getFabricSenderFactory().wrap(selector.single().createCommandSourceStack()),
                selector -> selector.inputString()
        ));
    }

    @Override
    public ServerVersion getNativeVersion() {
        return ServerVersion.V_1_21_11;
    }
}
