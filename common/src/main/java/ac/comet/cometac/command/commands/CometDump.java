package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.platform.api.PlatformPlugin;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import ac.comet.cometac.utils.common.PropertiesUtil;
import ac.comet.cometac.utils.reflection.ReflectionUtils;
import ac.comet.cometac.utils.viaversion.ViaVersionUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Properties;

public class CometDump implements BuildableCommand {

    private static final boolean PAPER = ReflectionUtils.hasClass("com.destroystokyo.paper.PaperConfig")
            || ReflectionUtils.hasClass("io.papermc.paper.configuration.Configuration");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String link = null; // these links should not expire for a while

    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(
                commandManager.commandBuilder("comet", "cometac")
                        .literal("dump", Description.of("Generate a debug dump"))
                        .permission("cometac.dump")
                        .handler(this::handleDump)
        );
    }

    private void handleDump(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();

        if (link != null) {
            sender.sendMessage(MessageUtil.miniMessage(CometAPI.INSTANCE.getConfigManager().getConfig()
                    .getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%")
                    .replace("%url%", link)));
            return;
        }
        // TODO: change this back to application/json once allowed
        CometLog.sendLogAsync(sender, generateDump(), string -> link = string, "text/yaml");
    }

    public static JsonObject getDumpInfo() {
        JsonObject base = new JsonObject();
        base.addProperty("type", "dump");
        base.addProperty("timestamp", System.currentTimeMillis());
        // versions
        JsonObject versions = new JsonObject();
        base.add("versions", versions);
        versions.addProperty("comet", CometAPI.INSTANCE.getExternalAPI().getGrimVersion());
        versions.addProperty("packetevents", PacketEvents.getAPI().getVersion().toString());
        versions.addProperty("server", PacketEvents.getAPI().getServerManager().getVersion().getReleaseName());
        versions.addProperty("implementation", CometAPI.INSTANCE.getPlatformServer().getPlatformImplementationString());
        // state of different properties
        JsonObject states = new JsonObject();
        base.add("states", states);
        if (CometAPI.INSTANCE.isInitialized()) states.addProperty("platform", CometAPI.INSTANCE.getPlatform().toString());
        if (ViaVersionUtil.isAvailable) states.addProperty("has_viaversion", true);
        if (PAPER) states.addProperty("has_paper", true);
        // include some relevant settings if not default
        JsonObject settings = new JsonObject();
        if (CometAPI.INSTANCE.getAlertManager().hasConsoleVerboseEnabled()) settings.addProperty("console_verbose", true);
        if (!CometAPI.INSTANCE.getAlertManager().hasConsoleAlertsEnabled()) settings.addProperty("console_alerts", false);
        if (settings.size() > 0) states.add("settings", settings);
        // system
        JsonObject system = new JsonObject();
        base.add("system", system);
        system.addProperty("os_name", System.getProperty("os.name"));
        system.addProperty("java_version", System.getProperty("java.version"));
        system.addProperty("user_language", System.getProperty("user.language"));
        // build
        base.add("build", getBuildInfo());
        // plugins
        JsonArray plugins = new JsonArray();
        base.add("plugins", plugins);
        for (PlatformPlugin plugin : CometAPI.INSTANCE.getPluginManager().getPlugins()) {
            JsonObject pluginJson = new JsonObject();
            pluginJson.addProperty("enabled", plugin.isEnabled());
            pluginJson.addProperty("name", plugin.getName());
            pluginJson.addProperty("version", plugin.getVersion());
            plugins.add(pluginJson);
        }
        return base;
    }

    private static JsonObject getBuildInfo() {
        JsonObject object = new JsonObject();
        try {
            Properties properties = PropertiesUtil.readProperties(CometAPI.INSTANCE.getClass(), "cometac.properties");
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                object.addProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (Exception ignored) {}
        return object;
    }

    /**
     * Generates a diagnostic dump in JSON format that contains various metadata
     * about the system, platform, and plugins. This dump is primarily used for
     * debugging and finding potential issues with the environment.
     * @return A JSON-formatted string containing the diagnostic dump.
     */
    private String generateDump() {
        JsonObject base = getDumpInfo();
        return gson.toJson(base);
    }
}
