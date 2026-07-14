package ac.comet.cometac.platform.api.manager;

import ac.comet.cometac.platform.api.PlatformPlugin;

public interface PlatformPluginManager {

    PlatformPlugin[] getPlugins();

    PlatformPlugin getPlugin(String pluginName);

    default boolean isPluginEnabled(String pluginName) {
        PlatformPlugin plugin = getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
