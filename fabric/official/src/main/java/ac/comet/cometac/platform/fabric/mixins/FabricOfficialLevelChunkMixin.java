package ac.comet.cometac.platform.fabric.mixins;

import ac.comet.cometac.platform.api.world.PlatformChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
@Implements(@Interface(iface = PlatformChunk.class, prefix = "cometac$"))
abstract class FabricOfficialLevelChunkMixin {
    @Unique
    private static final BlockPos.MutableBlockPos cometac$sharedPos = new BlockPos.MutableBlockPos();

    public int cometac$getBlockID(int x, int y, int z) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        cometac$sharedPos.set(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
        return Block.getId(chunk.getBlockState(cometac$sharedPos));
    }
}
