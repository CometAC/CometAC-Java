package ac.comet.cometac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.comet.cometac.utils.anticheat.update.PositionUpdate;

public interface PositionCheck extends AbstractCheck {

    default void onPositionUpdate(final PositionUpdate positionUpdate) {
    }
}
