package ac.comet.cometac.command.commands;

import ac.comet.cometac.command.BuildableCommand;
import ac.comet.cometac.manager.SuspectManager;
import ac.comet.cometac.platform.api.manager.cloud.CloudPlatformCommandArguments;
import ac.comet.cometac.platform.api.sender.Sender;
import ac.comet.cometac.utils.anticheat.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;

import java.util.List;
import java.util.Map;

// /cometac suspects (and standalone /suspects): a live, in-memory list of players who
// flagged in the last 12h. Hover a name to see the per-check breakdown.
public class CometSuspects implements BuildableCommand {

    @Override
    public void register(CommandManager<Sender> commandManager, CloudPlatformCommandArguments arguments) {
        commandManager.command(commandManager.commandBuilder("comet", "cometac")
                .literal("suspects", Description.of("Show players who recently flagged checks"))
                .permission("cometac.suspects")
                .handler(ctx -> handle(ctx.sender())));

        // Standalone alias so staff can just type /suspects.
        commandManager.command(commandManager.commandBuilder("suspects")
                .permission("cometac.suspects")
                .handler(ctx -> handle(ctx.sender())));
    }

    private void handle(Sender sender) {
        List<SuspectManager.Suspect> suspects = SuspectManager.active();

        if (suspects.isEmpty()) {
            sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender,
                    "%prefix% &7No suspects have flagged in the last 12h.")));
            return;
        }

        TextComponent.Builder builder = Component.text();
        builder.append(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender,
                "%prefix% &fSuspects &7(last 12h) &8» &b" + suspects.size())));

        for (SuspectManager.Suspect s : suspects) {
            builder.append(Component.newline());
            builder.append(Component.text(" › ", NamedTextColor.DARK_GRAY));
            builder.append(Component.text(s.name, NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.suggestCommand("/cometac profile " + s.name))
                    .hoverEvent(HoverEvent.showText(hover(s))));
            builder.append(Component.text(" ×" + s.totalFlags(), NamedTextColor.RED));
        }

        sender.sendMessage(builder.build());
    }

    private Component hover(SuspectManager.Suspect s) {
        TextComponent.Builder h = Component.text();
        h.append(Component.text(s.name, NamedTextColor.AQUA)).append(Component.newline());
        h.append(Component.text("Total flags: ", NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(s.totalFlags()), NamedTextColor.RED)).append(Component.newline());
        long secondsAgo = Math.max(0, (System.currentTimeMillis() - s.lastFlag) / 1000);
        h.append(Component.text("Last flag: ", NamedTextColor.GRAY))
                .append(Component.text(formatAgo(secondsAgo), NamedTextColor.WHITE)).append(Component.newline());
        h.append(Component.newline());
        h.append(Component.text("Checks flagged:", NamedTextColor.GRAY));

        s.checks.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(entry -> appendCheck(h, entry));

        return h.build();
    }

    private void appendCheck(TextComponent.Builder h, Map.Entry<String, Integer> entry) {
        h.append(Component.newline())
                .append(Component.text("  " + entry.getKey() + " ", NamedTextColor.WHITE))
                .append(Component.text("×" + entry.getValue(), NamedTextColor.RED));
    }

    private String formatAgo(long seconds) {
        if (seconds < 60) return seconds + "s ago";
        if (seconds < 3600) return (seconds / 60) + "m ago";
        return (seconds / 3600) + "h ago";
    }
}
