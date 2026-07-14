package ac.comet.cometac.utils.collisions.datatypes;

import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;

public interface CollisionFactory {
    CollisionBox fetch(CometPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z);
}
