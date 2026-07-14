package ac.comet.cometac.platform.fabric.utils.thread;

import ac.comet.cometac.CometAPI;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FabricFutureUtil {
    public static <U> CompletableFuture<U> supplySync(Supplier<U> entityTeleportSupplier) {
        CompletableFuture<U> ret = new CompletableFuture<>();
        CometAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().run(CometAPI.INSTANCE.getGrimPlugin(),
                () -> ret.complete(entityTeleportSupplier.get()));
        return ret;
    }
}
