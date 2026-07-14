package ac.comet.cometac.platform.fabric.mixins;

import ac.comet.cometac.platform.api.world.PlatformChunk;
import ac.comet.cometac.platform.api.world.PlatformWorld;
import ac.comet.cometac.platform.fabric.CometACFabricOfficialLoaderPlugin;
import ac.comet.cometac.platform.fabric.utils.world.FabricOfficialLevelChunkUtil;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.UUID;

@Mixin(Level.class)
@Implements(@Interface(iface = PlatformWorld.class, prefix = "cometac$"))
abstract class FabricOfficialLevelMixin implements LevelAccessor {

    @Shadow
    public abstract ResourceKey<Level> dimension();

    public boolean cometac$isChunkLoaded(int chunkX, int chunkZ) {
        return FabricOfficialLevelChunkUtil.hasChunkAt((Level) (Object) this, chunkX, chunkZ);
    }

    public WrappedBlockState cometac$getBlockAt(int x, int y, int z) {
        return WrappedBlockState.getByGlobalId(
                Block.getId(getBlockState(new BlockPos(x, y, z)))
        );
    }

    public String cometac$getName() {
        return this.dimension().identifier().toString();
    }

    public @Nullable UUID cometac$getUID() {
        return null;
    }

    public PlatformChunk cometac$getChunkAt(int currChunkX, int currChunkZ) {
        return (PlatformChunk) getChunk(currChunkX, currChunkZ);
    }

    public boolean cometac$isLoaded() {
        return CometACFabricOfficialLoaderPlugin.FABRIC_SERVER.getLevel(this.dimension()) != null;
    }
}
