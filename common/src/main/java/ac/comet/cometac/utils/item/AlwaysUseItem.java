package ac.comet.cometac.utils.item;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.latency.CompensatedWorld;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;

public class AlwaysUseItem extends ItemBehaviour {

    public static final AlwaysUseItem INSTANCE = new AlwaysUseItem();

    @Override
    public boolean canUse(ItemStack item, CompensatedWorld world, CometPlayer player, InteractionHand hand) {
        return true;
    }

}
