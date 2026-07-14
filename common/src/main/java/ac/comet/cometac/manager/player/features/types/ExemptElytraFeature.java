package ac.comet.cometac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.feature.FeatureState;
import ac.comet.cometac.player.CometPlayer;

public class ExemptElytraFeature implements CometFeature {

    @Override
    public String getName() {
        return "ExemptElytra";
    }

    @Override
    public void setState(CometPlayer player, ConfigManager config, FeatureState state) {
        switch (state) {
            case ENABLED -> player.setExemptElytra(true);
            case DISABLED -> player.setExemptElytra(false);
            default -> player.setExemptElytra(isEnabledInConfig(player, config));
        }
    }

    @Override
    public boolean isEnabled(CometPlayer player) {
        return player.isExemptElytra();
    }

    @Override
    public boolean isEnabledInConfig(CometPlayer player, ConfigManager config) {
        return config.getBooleanElse("exempt-elytra", false);
    }

}
