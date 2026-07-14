package ac.comet.cometac.predictionengine.predictions.input;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.math.Vector3dm;

public record FloatInput(float sideways, float vertical, float forward) implements Input {
    @Override
    public Vector3dm vector() {
        return new Vector3dm(sideways, vertical, forward);
    }

    @Override
    public Input normalize(CometPlayer player) {
        // this does nothing because FloatInputTransformer#getMovementResultFromInput normalizes legacy input while applying speed
        // in 1.14+ DoubleInput can be normalized earlier because getMovementResultFromInput only rotates the input and scales it by speed
        return this;
    }
}
