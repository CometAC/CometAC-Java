package ac.comet.cometac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.comet.cometac.utils.anticheat.update.RotationUpdate;

public interface RotationCheck extends AbstractCheck {

    default void process(final RotationUpdate rotationUpdate) {
    }
}
