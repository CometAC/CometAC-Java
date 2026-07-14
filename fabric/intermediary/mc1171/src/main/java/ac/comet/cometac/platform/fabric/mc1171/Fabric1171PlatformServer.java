package ac.comet.cometac.platform.fabric.mc1171;

import ac.comet.cometac.platform.fabric.CometACFabricIntermediaryLoaderPlugin;
import ac.comet.cometac.platform.fabric.mc1161.Fabric1140PlatformServer;
import ac.comet.cometac.platform.fabric.player.FabricOfflineProfile;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Fabric1171PlatformServer extends Fabric1140PlatformServer {
    @Override
    public @Nullable FabricOfflineProfile getProfileByName(String name) {
        Optional<GameProfile> gameProfile = CometACFabricIntermediaryLoaderPlugin.FABRIC_SERVER.getProfileCache().get(name);
        return gameProfile
                .map(profile -> new FabricOfflineProfile(profile.getId(), profile.getName()))
                .orElse(null);
    }
}
