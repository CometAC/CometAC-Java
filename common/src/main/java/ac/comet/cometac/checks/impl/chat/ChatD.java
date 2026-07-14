package ac.comet.cometac.checks.impl.chat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.common.client.WrapperCommonClientSettings;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

@CheckData(name = "ChatD", stableKey = "cometac.exploit.chat_while_hidden", description = "Chatting while chat is hidden")
public class ChatD extends Check implements PacketCheck {
    private boolean hidden;

    public ChatD(CometPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE
                || event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND_UNSIGNED
                || event.getPacketType() == PacketType.Play.Client.CHAT_COMMAND) {
            if (hidden && flagAndAlert() && shouldModifyPackets()) {
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS || event.getPacketType() == PacketType.Configuration.Client.CLIENT_SETTINGS) {
            hidden = new WrapperPlayClientSettings(event).getChatVisibility() == WrapperCommonClientSettings.ChatVisibility.HIDDEN;
        }
    }
}
