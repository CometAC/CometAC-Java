package ac.comet.cometac.checks.impl.prediction;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PostPredictionCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;

@CheckData(name = "GroundSpoof", stableKey = "cometac.groundspoof.fake", verboseVersion = 1, setback = 10, decay = 0.01)
public class GroundSpoof extends Check implements PostPredictionCheck {
    public static final VerboseSchema V = VerboseSchema.of("claimed:bool");

    // A single-tick onGround disagreement is almost always a prediction/lag hiccup
    // (slab/stair edges, teleport residue, ghost blocks). Real nofall/fly holds the
    // false ground claim for the whole fall, so requiring a short streak kills the
    // one-off false setbacks without letting an actual spoof through.
    private static final int REQUIRED_DESYNC_STREAK = 2;
    private int desyncStreak;

    public GroundSpoof(CometPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        // Exemptions
        // Don't check players in spectator
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_8) && player.gamemode == GameMode.SPECTATOR)
            return;
        // And don't check this long list of ground exemptions
        if (player.exemptOnGround() || !predictionComplete.isChecked()) return;
        // Don't check if the player was on a ghost block
        if (player.getSetbackTeleportUtil().blockOffsets) return;
        // Viaversion sends wrong ground status... (doesn't matter but is annoying)
        if (player.packetStateData.lastPacketWasTeleport) return;

        boolean claimed = player.clientClaimsLastOnGround;
        if (claimed != player.onGround) {
            if (++desyncStreak < REQUIRED_DESYNC_STREAK) return;
            flagAndAlertWithSetback(V.write(verbose()).bool(claimed));
            player.checkManager.getNoFall().flipPlayerGroundStatus = true;
        } else {
            desyncStreak = 0;
        }
    }
}
