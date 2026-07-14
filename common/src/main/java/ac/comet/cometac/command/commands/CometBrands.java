package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.manager.AlertManagerImpl;
import ac.comet.cometac.manager.datastore.PlayerToggleStore;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.player.PlatformPlayer;
import ac.comet.cometac.platform.api.sender.Sender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CometBrands implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("comet", "cometac")
                        .literal("brands", Description.of("Toggle brands for the sender"))
                        .permission("cometac.brand")
                        .handler(this::handleBrands)
        );
    }

    private void handleBrands(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        if (sender.isPlayer()) {
            PlatformPlayer p = Objects.requireNonNull(context.sender().getPlatformPlayer());
            AlertManagerImpl am = CometAPI.INSTANCE.getAlertManager();
            boolean newState = !am.hasBrandsEnabled(p);
            am.setBrandsEnabled(p, newState, false);
            CometAPI.INSTANCE.getDataStoreLifecycle().playerToggleStore()
                    .applyUserToggle(p.getUniqueId(), PlayerToggleStore.KEY_BRANDS, newState);
        } else if (sender.isConsole()) {
            CometAPI.INSTANCE.getAlertManager().toggleConsoleBrands();
        }
    }
}
