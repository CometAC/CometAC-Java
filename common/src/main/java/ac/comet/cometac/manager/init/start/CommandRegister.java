package ac.comet.cometac.manager.init.start;

import ac.comet.cometac.platform.api.command.CommandService;
import ac.comet.cometac.utils.anticheat.LogUtil;

public record CommandRegister(CommandService service) implements StartableInitable {

    @Override
    public void start() {
        try {
            if (service != null) {
                service.registerCommands();
            }
        } catch (Throwable t) {
            // This is the ultimate safety net. If command registration fails, Comet keeps running.
            LogUtil.error("Failed to register commands! Comet will run without command support.", t);
        }
    }
}
