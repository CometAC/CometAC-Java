package ac.comet.cometac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.comet.cometac.utils.anticheat.update.VehiclePositionUpdate;

public interface VehicleCheck extends AbstractCheck {

    void process(final VehiclePositionUpdate vehicleUpdate);
}
