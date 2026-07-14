package ac.comet.cometac.manager.init.start;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.commands.CometVersion;

public class UpdateChecker implements StartableInitable {
    @Override
    public void start() {
        if (CometAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("check-for-updates", true)) {
            CometVersion.checkForUpdatesAsync(CometAPI.INSTANCE.getPlatformServer().getConsoleSender());
        }
    }
}
