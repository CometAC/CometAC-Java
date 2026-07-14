package ac.comet.cometac.platform.fabric.mc1194;

import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.platform.fabric.CometACFabricIntermediaryLoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1171.Fabric1171PlatformServer;
import net.minecraft.commands.CommandSourceStack;

public class Fabric1190PlatformServer extends Fabric1171PlatformServer {
    @Override
    public void dispatchCommand(Sender sender, String command) {
        CommandSourceStack commandSource = CometACFabricIntermediaryLoaderPlugin.LOADER.getFabricSenderFactory().unwrap(sender);
        CometACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getCommands().performPrefixedCommand(commandSource, command);
    }
}
