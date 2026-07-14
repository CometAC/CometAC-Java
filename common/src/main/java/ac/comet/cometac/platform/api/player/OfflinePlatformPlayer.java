package ac.comet.cometac.platform.api.player;

import ac.grim.grimac.api.GrimIdentity;

public interface OfflinePlatformPlayer extends GrimIdentity {

    boolean isOnline();

    String getName();
}
