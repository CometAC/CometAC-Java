package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

public class CometHelp implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("comet", "cometac")
                        .literal("help", Description.of("Display help information"))
                        .permission("cometac.help")
                        .handler(this::handleHelp)
        );
    }

    private void handleHelp(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();

        for (String string : CometAPI.INSTANCE.getConfigManager().getConfig().getStringList("help")) {
            if (string == null) continue;
            string = MessageUtil.replacePlaceholders(sender, string);
            sender.sendMessage(MessageUtil.miniMessage(string));
        }
    }
}
