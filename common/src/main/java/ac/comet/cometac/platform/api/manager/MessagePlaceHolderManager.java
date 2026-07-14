package ac.comet.cometac.platform.api.manager;

import ac.comet.cometac.platform.api.player.PlatformPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessagePlaceHolderManager {
    @NotNull
    String replacePlaceholders(@Nullable PlatformPlayer player, @NotNull String string);
}
