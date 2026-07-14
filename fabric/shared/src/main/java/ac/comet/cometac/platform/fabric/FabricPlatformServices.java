package ac.comet.cometac.platform.fabric;

import ac.comet.cometac.platform.api.entity.CometEntity;
import ac.comet.cometac.platform.api.player.BlockTranslator;
import ac.comet.cometac.platform.fabric.player.AbstractFabricPlatformInventory;
import ac.comet.cometac.platform.fabric.player.AbstractFabricPlatformPlayer;
import ac.comet.cometac.platform.fabric.player.FabricOfflineProfile;
import ac.comet.cometac.platform.fabric.utils.convert.IFabricConversionUtil;

import java.util.Objects;
import java.util.function.Function;

public final class FabricPlatformServices {
    private static Function<AbstractFabricPlatformPlayer<?>, AbstractFabricPlatformInventory> inventoryFactory;
    private static Function<Object, CometEntity> entityFactory;
    private static Function<Object, BlockTranslator> blockTranslatorFactory;
    private static Function<String, Object> textLiteralFactory;
    private static Function<String, FabricOfflineProfile> profileLookup;
    private static IFabricConversionUtil conversionUtil;

    private FabricPlatformServices() {
    }

    public static void configure(
            Function<AbstractFabricPlatformPlayer<?>, AbstractFabricPlatformInventory> inventoryFactory,
            Function<Object, CometEntity> entityFactory,
            Function<Object, BlockTranslator> blockTranslatorFactory,
            Function<String, Object> textLiteralFactory,
            Function<String, FabricOfflineProfile> profileLookup,
            IFabricConversionUtil conversionUtil
    ) {
        FabricPlatformServices.inventoryFactory = Objects.requireNonNull(inventoryFactory);
        FabricPlatformServices.entityFactory = Objects.requireNonNull(entityFactory);
        FabricPlatformServices.blockTranslatorFactory = Objects.requireNonNull(blockTranslatorFactory);
        FabricPlatformServices.textLiteralFactory = Objects.requireNonNull(textLiteralFactory);
        FabricPlatformServices.profileLookup = Objects.requireNonNull(profileLookup);
        FabricPlatformServices.conversionUtil = Objects.requireNonNull(conversionUtil);
    }

    public static AbstractFabricPlatformInventory createInventory(AbstractFabricPlatformPlayer<?> player) {
        return require(inventoryFactory).apply(player);
    }

    public static CometEntity createEntity(Object entity) {
        return require(entityFactory).apply(entity);
    }

    public static BlockTranslator createBlockTranslator(Object player) {
        return require(blockTranslatorFactory).apply(player);
    }

    public static Object textLiteral(String message) {
        return require(textLiteralFactory).apply(message);
    }

    public static FabricOfflineProfile profileByName(String name) {
        return require(profileLookup).apply(name);
    }

    public static IFabricConversionUtil conversionUtil() {
        return Objects.requireNonNull(conversionUtil);
    }

    private static <T> T require(T value) {
        return Objects.requireNonNull(value);
    }
}
