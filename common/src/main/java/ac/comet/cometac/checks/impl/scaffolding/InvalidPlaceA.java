package ac.comet.cometac.checks.impl.scaffolding;

import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.BlockPlaceCheck;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.update.BlockPlace;
import com.github.retrooper.packetevents.util.Vector3f;

@CheckData(name = "InvalidPlaceA", stableKey = "cometac.scaffolding.invalid_place_a", description = "Sent invalid cursor position")
public class InvalidPlaceA extends BlockPlaceCheck {
    public InvalidPlaceA(CometPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        Vector3f cursor = place.cursor;
        if (cursor == null) return;
        if (!Float.isFinite(cursor.x) || !Float.isFinite(cursor.y) || !Float.isFinite(cursor.z)) {
            if (flagAndAlert() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}
