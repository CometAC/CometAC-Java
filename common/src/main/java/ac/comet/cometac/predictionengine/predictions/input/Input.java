package ac.comet.cometac.predictionengine.predictions.input;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.math.Vector3dm;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

public interface Input {

    Vector3dm vector();

    Input normalize(CometPlayer player);

    static Input createInput(CometPlayer player, float sideways, float vertical, float forward) {
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14)) {
            return new DoubleInput(sideways, vertical, forward);
        } else {
            return new FloatInput(sideways, vertical, forward);
        }
    }

}
