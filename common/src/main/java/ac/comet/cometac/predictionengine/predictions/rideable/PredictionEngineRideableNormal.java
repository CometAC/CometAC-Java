package ac.comet.cometac.predictionengine.predictions.rideable;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.predictionengine.predictions.PredictionEngineNormal;
import ac.comet.cometac.predictionengine.predictions.input.Input;
import ac.comet.cometac.utils.data.VectorData;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PredictionEngineRideableNormal extends PredictionEngineNormal {
    private final Input movementVector;

    @Override
    public void addJumpsToPossibilities(CometPlayer player, Set<VectorData> existingVelocities) {
        PredictionEngineRideableUtils.handleJumps(player, existingVelocities);
    }

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(CometPlayer player, Set<VectorData> possibleVectors, float speed) {
        return PredictionEngineRideableUtils.applyInputsToVelocityPossibilities(movementVector, player, possibleVectors, speed);
    }

}
