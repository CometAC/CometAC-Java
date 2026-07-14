package ac.comet.cometac.manager.tick.impl;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.manager.tick.Tickable;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;

public class ClientVersionSetter implements Tickable {
    @Override
    public void tick() {
        for (CometPlayer player : CometAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            // channel was somehow closed without us getting a disconnect event
            if (!ChannelHelper.isOpen(player.user.getChannel())) {
                CometAPI.INSTANCE.getPlayerDataManager().onDisconnect(player.user);
                continue;
            }

            player.pollData();
        }
    }
}
