package ac.comet.cometac.utils.blockplace;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.BlockPlace;

public interface BlockPlaceFactory {
    void applyBlockPlaceToWorld(CometPlayer player, BlockPlace place);
}
