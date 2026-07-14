package ac.comet.cometac.manager.player.features;

import ac.comet.cometac.manager.player.features.types.CometFeature;
import ac.comet.cometac.utils.anticheat.LogUtil;
import com.google.common.collect.ImmutableMap;

import java.util.regex.Pattern;

public class FeatureBuilder {

    private static final Pattern VALID = Pattern.compile("[a-zA-Z0-9_]{1,64}");
    private final ImmutableMap.Builder<String, CometFeature> mapBuilder = ImmutableMap.builder();

    public <T extends CometFeature> void register(T feature) {
        if (!VALID.matcher(feature.getName()).matches()) {
            LogUtil.error("Invalid feature name: " + feature.getName());
            return;
        }
        mapBuilder.put(feature.getName(), feature);
    }

    public ImmutableMap<String, CometFeature> buildMap() {
        return mapBuilder.build();
    }

}
