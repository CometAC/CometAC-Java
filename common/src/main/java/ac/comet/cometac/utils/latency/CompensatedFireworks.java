package ac.comet.cometac.utils.latency;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.type.PostPredictionCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;

import java.util.HashSet;
import java.util.Set;

public class CompensatedFireworks extends Check implements PostPredictionCheck {

    // As this is sync to one player, this does not have to be concurrent
    private final Set<Integer> activeFireworks = new HashSet<>();
    private final Set<Integer> fireworksToRemoveNextTick = new HashSet<>();

    public CompensatedFireworks(CometPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // Remove all the fireworks that were removed in the last tick
        // Remember to remove with an int not an Integer
        activeFireworks.removeAll(fireworksToRemoveNextTick);
        fireworksToRemoveNextTick.clear();
    }

    public boolean hasFirework(int entityId) {
        return activeFireworks.contains(entityId);
    }

    public void addNewFirework(int entityID) {
        activeFireworks.add(entityID);
    }

    public void removeFirework(int entityID) {
        if (activeFireworks.contains(entityID)) {
            fireworksToRemoveNextTick.add(entityID);
        }
    }

    public int getMaxFireworksAppliedPossible() {
        return activeFireworks.size();
    }

    // Boost saturates at 1.7*look; cap N for magnitude-scaled tolerances (residual, ElytraM allowance).
    public static final int MAGNITUDE_FIREWORK_CAP = 4;

    public int getMaxFireworksForMagnitude() {
        return Math.min(activeFireworks.size(), MAGNITUDE_FIREWORK_CAP);
    }
}
