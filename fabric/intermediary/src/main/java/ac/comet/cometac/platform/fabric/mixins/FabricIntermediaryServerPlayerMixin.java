package ac.comet.cometac.platform.fabric.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = ac.comet.cometac.platform.fabric.inject.FabricServerPlayerHandle.class, prefix = "comet$"))
abstract class FabricIntermediaryServerPlayerMixin {

    public boolean comet$isSneaking() {
        return ((ServerPlayer) (Object) this).isShiftKeyDown();
    }

    public void comet$setSneaking(boolean sneaking) {
        ((ServerPlayer) (Object) this).setShiftKeyDown(sneaking);
    }

    public boolean comet$isDead() {
        return ((ServerPlayer) (Object) this).isDeadOrDying();
    }

    public void comet$sendSystemText(Object nativeComponent) {
        ((ServerPlayer) (Object) this).displayClientMessage((Component) nativeComponent, false);
    }

    public boolean comet$isDisconnected() {
        return ((ServerPlayer) (Object) this).hasDisconnected();
    }

    public String comet$usernameString() {
        return ((ServerPlayer) (Object) this).getName().getString();
    }

    public void comet$broadcastInventoryChanges() {
        ((ServerPlayer) (Object) this).containerMenu.broadcastChanges();
    }

    public void comet$stopUsingItem() {
        ((ServerPlayer) (Object) this).stopUsingItem();
    }

    public boolean comet$isUsingItem() {
        return ((ServerPlayer) (Object) this).isUsingItem();
    }

    public double comet$posX() {
        return ((ServerPlayer) (Object) this).getX();
    }

    public double comet$posY() {
        return ((ServerPlayer) (Object) this).getY();
    }

    public double comet$posZ() {
        return ((ServerPlayer) (Object) this).getZ();
    }

    public UUID comet$uuid() {
        return ((ServerPlayer) (Object) this).getUUID();
    }

    public Object comet$vehicleEntity() {
        return ((ServerPlayer) (Object) this).getVehicle();
    }

    public Object comet$gameMode() {
        return ((ServerPlayer) (Object) this).gameMode.getGameModeForPlayer();
    }

    public Object comet$heldItemStack() {
        return ((ServerPlayer) (Object) this).inventory.getSelected();
    }

    public Object comet$inventoryItemAt(int slot) {
        return ((ServerPlayer) (Object) this).inventory.getItem(slot);
    }

    public Object comet$usedItemHand() {
        return ((ServerPlayer) (Object) this).getUsedItemHand();
    }

    public int comet$inventorySlotCount() {
        return ((ServerPlayer) (Object) this).inventory.getContainerSize();
    }
}
