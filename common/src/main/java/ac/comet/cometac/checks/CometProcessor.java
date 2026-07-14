package ac.comet.cometac.checks;

import ac.comet.cometac.CometAPI;
import ac.grim.grimac.api.AbstractProcessor;
import ac.grim.grimac.api.config.ConfigReloadable;
import ac.comet.cometac.utils.common.ConfigReloadObserver;

public abstract class CometProcessor implements AbstractProcessor, ConfigReloadable, ConfigReloadObserver {

    // Not everything has to be a check for it to process packets & be configurable

    @Override
    public void reload() {
        reload(CometAPI.INSTANCE.getConfigManager().getConfig());
    }

}
