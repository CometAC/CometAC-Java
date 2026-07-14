package ac.comet.cometac.command;

import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.sender.Sender;
import org.incendo.cloud.CommandManager;

public interface BuildableCommand {
    void register(CommandManager<Sender> manager, CloudPlatformCommandArguments arguments);
}
