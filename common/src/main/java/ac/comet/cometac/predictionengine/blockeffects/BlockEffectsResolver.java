package ac.comet.cometac.predictionengine.blockeffects;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.math.Vector3dm;

import java.util.List;

public interface BlockEffectsResolver {

    void applyEffectsFromBlocks(CometPlayer player, Vector3dm clientVelocity, boolean onlyApplyVelocity, List<CometPlayer.Movement> movements);

}
