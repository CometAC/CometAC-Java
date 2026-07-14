package ac.comet.cometac.platform.fabric;

import ac.comet.cometac.platform.fabric.manager.FabricItemResetHandler;
import ac.comet.cometac.platform.fabric.manager.FabricCloudPlatformCommandArguments;
import ac.comet.cometac.platform.fabric.manager.FabricPermissionRegistrationManager;
import ac.comet.cometac.platform.fabric.command.FabricPlayerSelectorParser;
import ac.comet.cometac.platform.fabric.player.FabricPlatformPlayerFactory;
import ac.comet.cometac.platform.fabric.scheduler.FabricPlatformScheduler;
import ac.comet.cometac.platform.fabric.sender.AbstractFabricSenderFactory;
import ac.comet.cometac.platform.fabric.sender.FabricOfficialSenderFactory;
import me.lucko.fabric.api.permissions.v0.Permissions;
import ac.comet.cometac.platform.fabric.utils.FabricOfficialPolymerHook;
import ac.comet.cometac.platform.fabric.utils.convert.IFabricConversionUtil;
import ac.comet.cometac.platform.fabric.utils.message.IFabricMessageUtil;
import ac.comet.cometac.utils.lazy.LazyHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class CometACFabricOfficialLoaderPlugin extends AbstractCometACFabricLoaderPlugin<
        FabricPlatformPlayerFactory,
        AbstractFabricPlatformServer,
        FabricPlatformScheduler,
        FabricOfficialSenderFactory,
        FabricItemResetHandler,
        FabricCloudPlatformCommandArguments
        > {
    public static MinecraftServer FABRIC_SERVER;
    public static CometACFabricOfficialLoaderPlugin LOADER;

    public CometACFabricOfficialLoaderPlugin(
            FabricPlatformPlayerFactory playerFactory,
            AbstractFabricPlatformServer platformServer,
            IFabricMessageUtil fabricMessageUtil,
            IFabricConversionUtil fabricConversionUtil
    ) {
        super(
                LazyHolder.simple(FabricPlatformScheduler::new),
                LazyHolder.simple(FabricOfficialSenderFactory::new),
                LazyHolder.simple(() -> new FabricItemResetHandler(fabricConversionUtil)),
                LazyHolder.simple(CometACFabricOfficialLoaderPlugin::createCommandArguments),
                LazyHolder.simple(() -> new FabricPermissionRegistrationManager(
                        LOADER.getFabricSenderFactory(),
                        name -> {
                            if (AbstractFabricSenderFactory.HAS_PERMISSIONS_API) {
                                Permissions.check(FABRIC_SERVER.createCommandSourceStack(), name);
                            }
                        })),
                playerFactory,
                platformServer,
                fabricMessageUtil,
                fabricConversionUtil
        );
        FabricPlatformServices.configure(
                playerFactory::getPlatformInventory,
                playerFactory::getPlatformEntity,
                player -> FabricOfficialPolymerHook.createTranslator((ServerPlayer) player),
                fabricMessageUtil::textLiteral,
                platformServer::getProfileByName,
                fabricConversionUtil
        );
    }

    public FabricOfficialSenderFactory getFabricSenderFactory() {
        return senderFactory.get();
    }

    public static FabricCloudPlatformCommandArguments createCommandArguments() {
        return new FabricCloudPlatformCommandArguments(new FabricPlayerSelectorParser<>(
                selector -> LOADER.getFabricSenderFactory().wrap(selector.single().createCommandSourceStack()),
                selector -> selector.inputString()
        ));
    }

}
