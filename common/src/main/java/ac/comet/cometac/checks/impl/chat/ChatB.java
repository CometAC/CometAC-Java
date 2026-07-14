package ac.comet.cometac.checks.impl.chat;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommandUnsigned;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;

// this can false from click events, but I doubt this would actually
// happen unless they're trying to flag, or if the server is set up badly
@CheckData(name = "ChatB", stableKey = "cometac.exploit.spigot_antispam_bypass", verboseVersion = 1, description = "Invalid chat message")
public class ChatB extends Check implements PacketCheck {
    public static final VerboseSchema V = VerboseSchema.of("message:str");

    public ChatB(CometPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            String message = new WrapperPlayClientChatMessage(event).getMessage();
            if (message.isEmpty() || !message.trim().equals(message)
                    || message.startsWith("/") && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19)) {
                String verbose = "message=" + message;
                if (flagAndAlert(V.write(verbose()).str(verbose)) && shouldModifyPackets()) {
                    player.onPacketCancel();
                    event.setCancelled(true);
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND_UNSIGNED) {
            String command = "/" + new WrapperPlayClientChatCommandUnsigned(event).getCommand();
            if (!command.stripTrailing().equals(command)) {
                String verbose = "command=" + command;
                if (flagAndAlert(V.write(verbose()).str(verbose))) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
            String command = "/" + new WrapperPlayClientChatCommand(event).getCommand();
            if (!command.trim().equals(command)) {
                String verbose = "command=" + command;
                if (flagAndAlert(V.write(verbose()).str(verbose))) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}
