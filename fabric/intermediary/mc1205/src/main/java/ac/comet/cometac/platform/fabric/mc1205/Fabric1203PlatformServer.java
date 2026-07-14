package ac.comet.cometac.platform.fabric.mc1205;

import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.platform.fabric.CometACFabricIntermediaryLoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1194.Fabric1190PlatformServer;
import net.minecraft.commands.CommandSourceStack;

public class Fabric1203PlatformServer extends Fabric1190PlatformServer {

    @Override
    public double getTPS() {
        return Math.min(1000.0 / CometACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getCurrentSmoothedTickTime(), CometACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.tickRateManager().tickrate());
    }

    @Override
    public void dispatchCommand(Sender sender, String command) {
        CommandSourceStack commandSource = CometACFabricIntermediaryLoaderPlugin.LOADER.getFabricSenderFactory().unwrap(sender);
        CometACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getCommands().performPrefixedCommand(commandSource, command);
    }
}
