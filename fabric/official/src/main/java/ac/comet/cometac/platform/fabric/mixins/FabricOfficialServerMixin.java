package ac.comet.cometac.platform.fabric.mixins;

import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.platform.fabric.inject.FabricMinecraftServerHandle;
import ac.comet.cometac.platform.fabric.inject.FabricServerPlayerHandle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(MinecraftServer.class)
@Implements(@Interface(iface = FabricMinecraftServerHandle.class, prefix = "comet$", remap = Interface.Remap.NONE))
abstract class FabricOfficialServerMixin {

    public boolean comet$isPlayerOnline(UUID uuid) {
        return ((MinecraftServer) (Object) this).getPlayerList().getPlayer(uuid) != null;
    }

    public FabricServerPlayerHandle comet$playerByUuid(UUID uuid) {
        return (FabricServerPlayerHandle) ((MinecraftServer) (Object) this).getPlayerList().getPlayer(uuid);
    }

    public FabricServerPlayerHandle comet$playerByName(String name) {
        return (FabricServerPlayerHandle) ((MinecraftServer) (Object) this).getPlayerList().getPlayerByName(name);
    }

    @SuppressWarnings("unchecked")
    public Collection<FabricServerPlayerHandle> comet$onlinePlayers() {
        return (Collection<FabricServerPlayerHandle>) (Collection<?>) ((MinecraftServer) (Object) this).getPlayerList().getPlayers();
    }

    public Collection<UUID> comet$savedPlayerUuids() {
        PlayerDataStorage storage = ((MinecraftServer) (Object) this).playerDataStorage;
        String[] files = storage.playerDir.list((dir, name) -> name.endsWith(".dat"));
        Set<UUID> uuids = new HashSet<>();
        if (files == null) return uuids;

        for (String file : files) {
            try {
                uuids.add(UUID.fromString(file.substring(0, file.length() - 4)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return uuids;
    }

    // getTickCount/getServerVersion/usesAuthentication/isRunning/getPlayerCount are NOT bodied
    // here: on the mojmap runtime the vanilla MinecraftServer methods of the same name satisfy
    // the injected interface directly. A comet$ body would graft a same-named synonym and the
    // ((MinecraftServer)this).getTickCount() call would resolve to it -> self-recursion ->
    // StackOverflow on the first server tick. The intermediary mixin DOES body them (vanilla is
    // method_3780 etc. there, so the comet$-stripped name doesn't clash).

    public Sender comet$createCommandSender() {
        return (Sender) (Object) ((MinecraftServer) (Object) this).createCommandSourceStack();
    }
}
