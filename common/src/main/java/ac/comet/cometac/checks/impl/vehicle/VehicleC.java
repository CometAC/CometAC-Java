package ac.comet.cometac.checks.impl.vehicle;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "VehicleC", stableKey = "cometac.vehicle.vehicle_control")
public class VehicleC extends Check {
    public VehicleC(CometPlayer player) {
        super(player);
    }
}
