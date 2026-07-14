package ac.comet.cometac.utils.data;

import ac.comet.cometac.utils.collisions.datatypes.SimpleCollisionBox;
import com.github.retrooper.packetevents.protocol.world.BlockFace;

import java.util.List;

public record PistonTemplate(BlockFace dir,
                      List<SimpleCollisionBox> boxes,
                      boolean push, boolean slime, boolean honey) {
}
