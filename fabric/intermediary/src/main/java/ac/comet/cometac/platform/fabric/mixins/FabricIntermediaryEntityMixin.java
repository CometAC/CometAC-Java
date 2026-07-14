package ac.comet.cometac.platform.fabric.mixins;

import ac.comet.cometac.platform.fabric.inject.FabricEntityHandle;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(Entity.class)
@Implements(@Interface(iface = FabricEntityHandle.class, prefix = "comet$"))
abstract class FabricIntermediaryEntityMixin {

    public UUID comet$fabricEntityUuid() {
        return ((Entity) (Object) this).getUUID();
    }

    public boolean comet$fabricEjectPassengers() {
        Entity entity = (Entity) (Object) this;
        if (entity.isVehicle()) {
            entity.ejectPassengers();
            return true;
        }
        return false;
    }

    public Object comet$fabricWorld() {
        return ((Entity) (Object) this).level;
    }

    public double comet$fabricPosX() {
        return ((Entity) (Object) this).getX();
    }

    public double comet$fabricPosY() {
        return ((Entity) (Object) this).getY();
    }

    public double comet$fabricPosZ() {
        return ((Entity) (Object) this).getZ();
    }

    public float comet$fabricYaw(float partialTick) {
        return ((Entity) (Object) this).getViewYRot(partialTick);
    }

    public float comet$fabricPitch(float partialTick) {
        return ((Entity) (Object) this).getViewXRot(partialTick);
    }
}
