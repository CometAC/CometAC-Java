package ac.comet.cometac.predictionengine.predictions;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.data.VectorData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PredictionEngineDualState extends PredictionEngineNormal {
    private static final PredictionEngineElytra ELYTRA = new PredictionEngineElytra();

    @Override
    public List<VectorData> applyInputsToVelocityPossibilities(CometPlayer player, Set<VectorData> possibleVectors, float speed) {
        List<VectorData> normal = super.applyInputsToVelocityPossibilities(player, possibleVectors, speed);
        List<VectorData> elytra = ELYTRA.applyInputsToVelocityPossibilities(player, possibleVectors, speed);
        List<VectorData> combined = new ArrayList<>(normal.size() + elytra.size());
        combined.addAll(normal);
        combined.addAll(elytra);
        return combined;
    }
}
