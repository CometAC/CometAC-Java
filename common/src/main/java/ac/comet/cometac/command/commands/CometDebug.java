package ac.comet.cometac.command.commands;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.checks.debug.HitboxDebugHandler;
import ac.comet.cometac.checks.impl.prediction.DebugHandler;
import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.platform.api.command.PlayerSelector;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CometDebug implements BuildableCommand {

    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        var comet = commandManager.commandBuilder("comet", "cometac");

        commandManager.command(comet
                .literal("debug")
                .literal("start", Description.of("Start debug output"))
                .permission("cometac.debug")
                .required("target", arguments.singlePlayerSelectorParser())
                .handler(this::handleStart));

        commandManager.command(comet
                .literal("debug")
                .literal("stop", Description.of("Stop debug output"))
                .permission("cometac.debug")
                .required("target", arguments.singlePlayerSelectorParser())
                .handler(this::handleStop));

        commandManager.command(comet
                .literal("debug")
                .literal("filter")
                .literal("add", Description.of("Add a filter"))
                .permission("cometac.debug")
                .required("filterType", EnumParser.enumParser(DebugHandler.Filter.class))
                .handler(this::handleFilterAdd));

        commandManager.command(comet
                .literal("debug")
                .literal("filter")
                .literal("remove", Description.of("Remove a filter"))
                .permission("cometac.debug")
                .required("filterType", EnumParser.enumParser(DebugHandler.Filter.class))
                .handler(this::handleFilterRemove));

        commandManager.command(comet
                .literal("debug")
                .literal("filter")
                .literal("reset", Description.of("Reset filters to show all"))
                .permission("cometac.debug")
                .handler(this::handleFilterReset));

        commandManager.command(comet
                .literal("debug")
                .literal("threshold", Description.of("Set offset threshold for THRESHOLD filter"))
                .permission("cometac.debug")
                .required("value", DoubleParser.doubleParser(0.0))
                .handler(this::handleThreshold));

        commandManager.command(comet
                .literal("debug")
                .literal("time", Description.of("Set auto-stop timeout (0 = infinite)"))
                .permission("cometac.debug")
                .required("seconds", IntegerParser.integerParser(0))
                .handler(this::handleTime));

        commandManager.command(comet
                .literal("debug")
                .literal("buffer", Description.of("Set paste buffer size in ticks"))
                .permission("cometac.debug")
                .required("size", IntegerParser.integerParser(50, 5000))
                .handler(this::handleBuffer));

        commandManager.command(comet
                .literal("debug")
                .literal("paste", Description.of("Upload debug buffer to paste.cometac.ac"))
                .permission("cometac.debug")
                .required("target", arguments.singlePlayerSelectorParser())
                .handler(this::handlePaste));

        commandManager.command(comet
                .literal("debug")
                .literal("status", Description.of("Show debug status"))
                .permission("cometac.debug")
                .required("target", arguments.singlePlayerSelectorParser())
                .handler(this::handleStatus));

        // Legacy toggle: /cometac debug [player]
        commandManager.command(comet
                .literal("debug", Description.of("Toggle debug output"))
                .permission("cometac.debug")
                .optional("target", arguments.singlePlayerSelectorParser())
                .handler(this::handleToggle));

        commandManager.command(comet
                .literal("consoledebug", Description.of("Toggle console debug output"))
                .permission("cometac.consoledebug")
                .required("target", arguments.singlePlayerSelectorParser())
                .handler(this::handleConsoleDebug));

        commandManager.command(comet
                .literal("hitboxdebug", Description.of("Toggle hitbox debug"))
                .permission("cometac.hitboxdebug")
                .optional("target", arguments.singlePlayerSelectorParser(), Description.of("Player to debug"))
                .handler(this::handleHitboxDebug));
    }

    private void handleStart(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        CometPlayer target = parseTarget(sender, context.<PlayerSelector>get("target").getSinglePlayer());
        if (target == null) return;
        CometPlayer senderPlayer = resolveSenderPlayer(sender);
        if (senderPlayer == null) return;

        DebugHandler handler = target.checkManager.getDebugHandler();
        if (handler.isListening(senderPlayer)) {
            sender.sendMessage(Component.text("Already active. Use /cometac debug stop first.", NamedTextColor.RED));
            return;
        }

        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(sender.getUniqueId().toString());
        handler.startListening(senderPlayer, settings);

        sender.sendMessage(Component.text()
                .append(Component.text("Debug started for ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.WHITE))
                .append(Component.text(" [" + settings.filtersString()
                        + (settings.hasFilter(DebugHandler.Filter.THRESHOLD) ? " >=" + settings.threshold() : "")
                        + (settings.timeoutSeconds() > 0 ? " timeout=" + settings.timeoutSeconds() + "s" : "")
                        + " buf=" + settings.bufferSize()
                        + "]", NamedTextColor.AQUA))
                .build());
    }

    private void handleStop(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        CometPlayer target = parseTarget(sender, context.<PlayerSelector>get("target").getSinglePlayer());
        if (target == null) return;
        CometPlayer senderPlayer = resolveSenderPlayer(sender);
        if (senderPlayer == null) return;

        if (target.checkManager.getDebugHandler().stopListening(senderPlayer)) {
            sender.sendMessage(Component.text("Debug stopped for " + target.getName() + ".", NamedTextColor.GRAY));
        } else {
            sender.sendMessage(Component.text("Debug was not active for " + target.getName() + ".", NamedTextColor.RED));
        }
    }

    private void handleToggle(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        PlayerSelector selector = context.getOrDefault("target", null);
        CometPlayer target = parseTarget(sender, selector == null ? sender : selector.getSinglePlayer());
        if (target == null) return;

        DebugHandler handler = target.checkManager.getDebugHandler();

        if (sender.isConsole()) {
            boolean enabled = handler.toggleConsoleOutput();
            sender.sendMessage(Component.text("Console debug for " + target.getName() + (enabled ? " enabled." : " disabled."), NamedTextColor.GRAY));
            return;
        }

        CometPlayer senderPlayer = resolveSenderPlayer(sender);
        if (senderPlayer == null) return;

        if (handler.isListening(senderPlayer)) {
            handler.stopListening(senderPlayer);
            sender.sendMessage(Component.text("Debug stopped for " + target.getName() + ".", NamedTextColor.GRAY));
        } else {
            DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(sender.getUniqueId().toString());
            handler.startListening(senderPlayer, settings);
            sender.sendMessage(Component.text()
                    .append(Component.text("Debug started for ", NamedTextColor.GREEN))
                    .append(Component.text(target.getName(), NamedTextColor.WHITE))
                    .append(Component.text(" [" + settings.filtersString() + "]", NamedTextColor.AQUA))
                    .build());
        }
    }

    private void handleFilterAdd(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        DebugHandler.Filter filterType = context.get("filterType");
        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(senderKey(sender));
        settings.addFilter(filterType);
        sender.sendMessage(Component.text("Added filter: " + filterType + ". Active: " + settings.filtersString(), NamedTextColor.GREEN));
    }

    private void handleFilterRemove(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        DebugHandler.Filter filterType = context.get("filterType");
        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(senderKey(sender));
        settings.removeFilter(filterType);
        sender.sendMessage(Component.text("Removed filter: " + filterType + ". Active: " + settings.filtersString(), NamedTextColor.GREEN));
    }

    private void handleFilterReset(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(senderKey(sender));
        settings.resetFilters();
        sender.sendMessage(Component.text("Filters reset. Showing ALL ticks.", NamedTextColor.GREEN));
    }

    private void handleThreshold(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        double value = context.get("value");
        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(senderKey(sender));
        settings.setThreshold(value);
        sender.sendMessage(Component.text("Threshold set to: " + value, NamedTextColor.GREEN));
    }

    private void handleTime(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        int seconds = context.get("seconds");
        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(senderKey(sender));
        settings.setTimeoutSeconds(seconds);
        sender.sendMessage(Component.text("Timeout set to: " + (seconds == 0 ? "infinite" : seconds + "s"), NamedTextColor.GREEN));
    }

    private void handleBuffer(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        int size = context.get("size");
        DebugHandler.DebugSettings settings = DebugHandler.getOrCreatePendingSettings(senderKey(sender));
        settings.setBufferSize(size);
        sender.sendMessage(Component.text("Buffer size set to: " + size + " ticks", NamedTextColor.GREEN));
    }

    private void handlePaste(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        CometPlayer target = parseTarget(sender, context.<PlayerSelector>get("target").getSinglePlayer());
        if (target == null) return;
        target.checkManager.getDebugHandler().pasteBuffer(sender);
    }

    private void handleStatus(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        CometPlayer target = parseTarget(sender, context.<PlayerSelector>get("target").getSinglePlayer());
        if (target == null) return;
        CometPlayer senderPlayer = resolveSenderPlayer(sender);
        if (senderPlayer == null) return;

        DebugHandler handler = target.checkManager.getDebugHandler();
        boolean active = handler.isListening(senderPlayer);
        DebugHandler.DebugSettings activeSettings = handler.getListenerSettings(senderPlayer);
        DebugHandler.DebugSettings pending = DebugHandler.getOrCreatePendingSettings(senderKey(sender));

        sender.sendMessage(Component.text()
                .append(Component.text("Debug for ", NamedTextColor.GRAY))
                .append(Component.text(target.getName() + ": ", NamedTextColor.WHITE))
                .append(Component.text(active ? "ACTIVE" : "INACTIVE", active ? NamedTextColor.GREEN : NamedTextColor.RED))
                .build());
        if (active) {
            sender.sendMessage(Component.text("  Active: filter=" + activeSettings.filtersString()
                    + " threshold=" + activeSettings.threshold()
                    + " timeout=" + (activeSettings.timeoutSeconds() == 0 ? "none" : activeSettings.timeoutSeconds() + "s")
                    + " buffer=" + activeSettings.bufferSize(), NamedTextColor.AQUA));
        }
        sender.sendMessage(Component.text("  Pending: filter=" + pending.filtersString()
                + " threshold=" + pending.threshold()
                + " timeout=" + (pending.timeoutSeconds() == 0 ? "none" : pending.timeoutSeconds() + "s")
                + " buffer=" + pending.bufferSize(), NamedTextColor.GRAY));
    }

    private void handleConsoleDebug(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        PlayerSelector targetName = context.getOrDefault("target", null);
        CometPlayer cometPlayer = parseTarget(sender, targetName.getSinglePlayer());
        if (cometPlayer == null) return;

        boolean isOutput = cometPlayer.checkManager.getDebugHandler().toggleConsoleOutput();
        sender.sendMessage(Component.text()
                .append(Component.text("Console output for ", NamedTextColor.GRAY))
                .append(Component.text(cometPlayer.user.getProfile().getName(), NamedTextColor.WHITE))
                .append(Component.text(isOutput ? " enabled." : " disabled.", NamedTextColor.GRAY))
                .build());
    }

    private void handleHitboxDebug(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        PlayerSelector selector = context.getOrDefault("target", null);

        if (!sender.isPlayer()) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "hitboxdebug-player-only", "%prefix% &cHitbox debug can only be toggled by players."));
            return;
        }

        CometPlayer target = parseTarget(sender, selector == null ? sender : selector.getSinglePlayer());
        if (target == null) return;
        CometPlayer senderPlayer = resolveSenderPlayer(sender);
        if (senderPlayer == null) return;

        HitboxDebugHandler hitboxHandler = target.checkManager.getCheck(HitboxDebugHandler.class);
        if (hitboxHandler == null) {
            sender.sendMessage(Component.text("HitboxDebugHandler not found.", NamedTextColor.RED));
            return;
        }

        boolean enabled = hitboxHandler.toggleListener(senderPlayer);
        sender.sendMessage(Component.text()
                .append(Component.text("Hitbox debug for ", NamedTextColor.GRAY))
                .append(Component.text(target.getName(), NamedTextColor.WHITE))
                .append(Component.text(enabled ? " enabled." : " disabled.", NamedTextColor.GRAY))
                .build());
    }

    private String senderKey(Sender sender) {
        return sender.isPlayer() ? sender.getUniqueId().toString() : "console";
    }

    private @Nullable CometPlayer resolveSenderPlayer(Sender sender) {
        if (!sender.isPlayer()) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "run-as-player-or-console", "%prefix% &cThis command can only be used by players!"));
            return null;
        }
        CometPlayer p = CometAPI.INSTANCE.getPlayerDataManager().getPlayer(sender.getUniqueId());
        if (p == null) sender.sendMessage(MessageUtil.getParsedComponent(sender, "sender-not-found", "%prefix% &cYou cannot be exempt to use this command!"));
        return p;
    }

    private @Nullable CometPlayer parseTarget(@NotNull Sender sender, @Nullable Sender t) {
        if (sender.isConsole() && t == null) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "console-specify-target", "%prefix% &cYou must specify a target as the console!"));
            return null;
        }
        Sender target = t == null ? sender : t;
        CometPlayer p = CometAPI.INSTANCE.getPlayerDataManager().getPlayer(target.getUniqueId());
        if (p == null) sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-found", "%prefix% &cPlayer is exempt or offline!"));
        return p;
    }
}
