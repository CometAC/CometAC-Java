package ac.comet.cometac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.comet.cometac.player.CometPlayer;

public interface CometFeature {
    String getName();

    void setState(CometPlayer player, ConfigManager config, FeatureState state);

    boolean isEnabled(CometPlayer player);

    boolean isEnabledInConfig(CometPlayer player, ConfigManager config);
}
