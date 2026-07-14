package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.manager.init.start.SuperDebug;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.utils.anticheat.LogUtil;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import ac.comet.cometac.utils.common.arguments.CommonGrimArguments;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.NotNull;

import ac.comet.cometac.checks.impl.prediction.OffsetHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class CometLog implements BuildableCommand {
    public static void sendLogAsync(Sender sender, String log, Consumer<String> consumer, String type) {
        String success = CometAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log", "%prefix% &fUploaded debug to: %url%");
        String failure = CometAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-upload-failure", "%prefix% &cSomething went wrong while uploading this log, see console for more information.");
        String uploading = CometAPI.INSTANCE.getConfigManager().getConfig().getStringElse("upload-log-start", "%prefix% &fUploading log... please wait");
        uploading = MessageUtil.replacePlaceholders(sender, uploading);
        sender.sendMessage(MessageUtil.miniMessage(uploading));
        CometAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(CometAPI.INSTANCE.getGrimPlugin(), () -> {
            try {
                sendLog(sender, log, success, failure, consumer, type);
            } catch (Exception e) {
                String message = MessageUtil.replacePlaceholders(sender, failure);
                sender.sendMessage(MessageUtil.miniMessage(message));
                LogUtil.error("Failed to send log", e);
            }
        });
    }

    private static void sendLog(Sender sender, String log, String success, String failure, Consumer<String> consumer, String type) throws IOException {
        URL mUrl = new URL(CommonGrimArguments.PASTE_URL.value() + "data/post");
        HttpURLConnection urlConn = (HttpURLConnection) mUrl.openConnection();
        try {
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(CommonGrimArguments.URL_TIMEOUT.value());
            urlConn.setReadTimeout(CommonGrimArguments.URL_TIMEOUT.value());
            urlConn.addRequestProperty("User-Agent", "CometAC/" + CometAPI.INSTANCE.getExternalAPI().getGrimVersion());
            urlConn.addRequestProperty("Content-Type", type); // Not really yaml, but looks nicer than plaintext
            urlConn.setRequestProperty("Content-Length", Integer.toString(log.length()));
            try (OutputStream stream = urlConn.getOutputStream()) {
                stream.write(log.getBytes(StandardCharsets.UTF_8));
            }
            final int response = urlConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_CREATED) {
                String responseURL = urlConn.getHeaderField("Location");
                String message = success.replace("%url%", CommonGrimArguments.PASTE_URL.value() + responseURL);
                consumer.accept(message);
                message = MessageUtil.replacePlaceholders(sender, message);
                sender.sendMessage(MessageUtil.miniMessage(message));
            } else {
                String message = MessageUtil.replacePlaceholders(sender, failure);
                sender.sendMessage(MessageUtil.miniMessage(message));
                LogUtil.error("Returned response code " + response + ": " + urlConn.getResponseMessage());
            }
        } finally {
            urlConn.disconnect();
        }
    }

    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        Command<Sender> command = commandManager.commandBuilder("comet", "cometac")
                .literal("log", "logs")
                .permission("cometac.log")
                .required("flagId", IntegerParser.integerParser())
                .handler(this::handleLog)
                .manager(commandManager)
                .build();
        commandManager
                .command(command)
                .command(commandManager.commandBuilder("gl").proxies(command));

        Command<Sender> lastCommand = commandManager.commandBuilder("comet", "cometac")
                .literal("log", "logs")
                .literal("last")
                .permission("cometac.log")
                .optional("count", IntegerParser.integerParser(1, 256))
                .handler(this::handleLogLast)
                .manager(commandManager)
                .build();
        commandManager.command(lastCommand);

        commandManager.command(commandManager.commandBuilder("comet", "cometac")
                .literal("log", "logs")
                .literal("range")
                .permission("cometac.log")
                .required("range", StringParser.stringParser())
                .handler(this::handleLogRange));
    }

    private void handleLog(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        int flagId = context.get("flagId");

        StringBuilder builder = SuperDebug.getFlag(flagId);
        if (builder == null) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cUnable to find that log"));
            return;
        }
        sendLogAsync(sender, builder.toString(), string -> {}, "text/yaml");
    }

    private void handleLogLast(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        int count = context.getOrDefault("count", 1);

        int lastId = OffsetHandler.getLastFlagId();
        if (lastId == 0) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cNo flags recorded yet"));
            return;
        }

        StringBuilder combined = new StringBuilder();
        int found = 0;
        for (int i = 0; i < count; i++) {
            int id = ((lastId - 1 - i) & 255) + 1;
            StringBuilder flag = SuperDebug.getFlag(id);
            if (flag != null) {
                if (found > 0) combined.append("\n\n--- Flag #").append(id).append(" ---\n\n");
                combined.append(flag);
                found++;
            }
        }

        if (found == 0) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cNo flags found"));
            return;
        }

        sendLogAsync(sender, combined.toString(), string -> {}, "text/yaml");
    }

    private void handleLogRange(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        String range = context.get("range");

        String[] parts = range.split("-");
        if (parts.length != 2) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cFormat: /cometac log range <from>-<to> (e.g. 1-10)"));
            return;
        }

        int from, to;
        try {
            from = Integer.parseInt(parts[0].trim());
            to = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cInvalid range format"));
            return;
        }

        if (from > to) { int tmp = from; from = to; to = tmp; }
        if (to - from > 255) to = from + 255;

        StringBuilder combined = new StringBuilder();
        int found = 0;
        for (int id = from; id <= to; id++) {
            StringBuilder flag = SuperDebug.getFlag(id);
            if (flag != null) {
                if (found > 0) combined.append("\n\n--- Flag #").append(id).append(" ---\n\n");
                else combined.append("--- Flag #").append(id).append(" ---\n\n");
                combined.append(flag);
                found++;
            }
        }

        if (found == 0) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "upload-log-not-found", "%prefix% &cNo flags found in range " + from + "-" + to));
            return;
        }

        sendLogAsync(sender, combined.toString(), string -> {}, "text/yaml");
    }
}
