package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.platform.api.command.PlayerSelector;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.player.PlatformPlayer;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;


public class CometProfile implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("comet", "cometac")
                        .literal("profile")
                        .permission("cometac.profile")
                        .required("target", arguments.singlePlayerSelectorParser())
                        .handler(this::handleProfile)
        );
    }

    private void handleProfile(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        PlayerSelector target = context.get("target");

        PlatformPlayer targetPlatformPlayer = target.getSinglePlayer() != null ? target.getSinglePlayer().getPlatformPlayer() : null;
        if (targetPlatformPlayer == null) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-found", "%prefix% &cPlayer not found!"));
            return;
        }
        if (targetPlatformPlayer.isExternalPlayer()) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender,"player-not-this-server", "%prefix% &cThis player isn't on this server!"));
            return;
        }

        CometPlayer cometPlayer = CometAPI.INSTANCE.getPlayerDataManager().getPlayer(targetPlatformPlayer.getUniqueId());
        if (cometPlayer == null) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-found", "%prefix% &cPlayer is exempt or offline!"));
            return;
        }

        for (String message : CometAPI.INSTANCE.getConfigManager().getConfig().getStringList("profile")) {
            final Component component = MessageUtil.miniMessage(message);
            sender.sendMessage(MessageUtil.replacePlaceholders(cometPlayer, component));
        }
    }
}
