package ac.comet.cometac.checks.impl.baritone;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.impl.aim.processor.AimProcessor;
import ac.comet.cometac.checks.type.RotationCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.RotationUpdate;
import ac.comet.cometac.utils.data.HeadRotation;
import ac.comet.cometac.utils.math.CometMath;

// This check has been patched by Baritone for a long time, and it also seems to false with cinematic camera now, so it is disabled.
@CheckData(name = "Baritone", stableKey = "cometac.baritone.baritone", verboseVersion = 1)
public class Baritone extends Check implements RotationCheck {
    public static final VerboseSchema V = VerboseSchema.of("divisor:f64");

    private int verbose;

    public Baritone(CometPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        final HeadRotation from = rotationUpdate.getFrom();
        final HeadRotation to = rotationUpdate.getTo();

        final float deltaPitch = Math.abs(to.pitch() - from.pitch());

        // Baritone works with small degrees, limit to 1 degree to pick up on baritone slightly moving aim to bypass anticheats
        if (rotationUpdate.getDeltaXRot() == 0 && deltaPitch > 0 && deltaPitch < 1 && Math.abs(to.pitch()) != 90.0f) {
            if (rotationUpdate.getProcessor().divisorY < CometMath.MINIMUM_DIVISOR) {
                verbose++;
                if (verbose > 8) {
                    double divisor = AimProcessor.convertToSensitivity(rotationUpdate.getProcessor().divisorX);
                    flagAndAlert(V.write(verbose()).f64(divisor));
                }
            } else {
                verbose = 0;
            }
        }
    }
}
