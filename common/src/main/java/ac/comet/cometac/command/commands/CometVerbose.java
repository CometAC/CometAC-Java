package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.manager.AlertManagerImpl;
import ac.comet.cometac.manager.datastore.PlayerToggleStore;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.player.PlatformPlayer;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CometVerbose implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("comet", "cometac")
                        .literal("verbose")
                        .permission("cometac.verbose")
                        .optional("player", StringParser.stringParser(), arguments.onlinePlayerSuggestions())
                        .handler(this::handleVerbose)
        );
    }

    private void handleVerbose(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        String targetFilter = context.getOrDefault("player", null);

        if (sender.isPlayer()) {
            PlatformPlayer p = Objects.requireNonNull(context.sender().getPlatformPlayer());
            AlertManagerImpl am = CometAPI.INSTANCE.getAlertManager();
            boolean newState = !am.hasVerboseEnabled(p);
            am.setVerboseEnabled(p, newState, false);
            PlayerToggleStore toggles = CometAPI.INSTANCE.getDataStoreLifecycle().playerToggleStore();
            toggles.applyUserToggle(p.getUniqueId(), PlayerToggleStore.KEY_VERBOSE, newState);
            // setVerboseEnabled(true) cascades to setAlertsEnabled(true) in AlertManager
            // — mirror that into the toggle store so the persisted alerts row tracks the
            // implied state, otherwise a verbose-on staff member would re-toggle alerts
            // off on next reconnect when persisted alerts is still false.
            if (newState) toggles.applyUserToggle(p.getUniqueId(), PlayerToggleStore.KEY_ALERTS, true);

            if (newState) {
                am.setPlayerFilter(p, targetFilter);
                if (targetFilter != null) {
                    String msg = CometAPI.INSTANCE.getConfigManager().getConfig()
                            .getStringElse("verbose-filter", "%prefix% &fFiltering verbose for: &b%player%")
                            .replace("%player%", targetFilter);
                    sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, msg)));
                }
            } else {
                am.setPlayerFilter(p, null);
            }
        } else if (sender.isConsole()) {
            CometAPI.INSTANCE.getAlertManager().toggleConsoleVerbose();
        }
    }
}
