package ac.comet.cometac.platform.fabric.mixins;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.platform.api.player.PlatformPlayer;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.platform.fabric.CometACFabricOfficialLoaderPlugin;
import ac.comet.cometac.platform.fabric.sender.FabricOfficialSenderFactory;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(CommandSourceStack.class)
@Implements(@Interface(iface = Sender.class, prefix = "comet$"))
abstract class FabricOfficialCommandSourceStackMixin {

    @Unique
    private CommandSourceStack cometac$self() {
        return (CommandSourceStack) (Object) this;
    }

    @Unique
    private FabricOfficialSenderFactory cometac$factory() {
        return CometACFabricOfficialLoaderPlugin.LOADER.getFabricSenderFactory();
    }

    public UUID comet$getUniqueId() {
        return cometac$factory().getUniqueId(cometac$self());
    }

    public String comet$getName() {
        return cometac$factory().getName(cometac$self());
    }

    public void comet$sendMessage(String message) {
        cometac$factory().sendMessage(cometac$self(), message);
    }

    public void comet$sendMessage(Component message) {
        cometac$factory().sendMessage(cometac$self(), message);
    }

    public boolean comet$hasPermission(String permission) {
        return cometac$factory().hasPermission(cometac$self(), permission);
    }

    public boolean comet$hasPermission(String permission, boolean defaultIfUnset) {
        return cometac$factory().hasPermission(cometac$self(), permission, defaultIfUnset);
    }

    public void comet$performCommand(String commandLine) {
        cometac$factory().performCommand(cometac$self(), commandLine);
    }

    public boolean comet$isConsole() {
        return cometac$factory().isConsole(cometac$self());
    }

    public Object comet$getNativeSender() {
        return cometac$self();
    }

    public @Nullable PlatformPlayer comet$getPlatformPlayer() {
        return CometAPI.INSTANCE.getPlatformPlayerFactory().getFromUUID(comet$getUniqueId());
    }
}
