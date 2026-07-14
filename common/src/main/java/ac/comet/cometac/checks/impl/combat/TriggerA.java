package ac.comet.cometac.checks.impl.combat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.grim.grimac.api.config.ConfigManager;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.data.packetentity.PacketEntity;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "TriggerA", stableKey = "cometac.combat.trigger_interval", configName = "Trigger", description = "Checks for inhuman attack interval consistency", decay = 0.05)
public class TriggerA extends Check implements PacketCheck {

    private boolean playersOnly = true;
    private boolean debug = false;
    private int windowSize = 10;
    private long maxSpread = 50;

    private final long[] intervals = new long[20];
    private int count;
    private long lastAttackMs;
    private double buffer;
    private int ticksSinceAttack;

    public TriggerA(CometPlayer player) {
        super(player);
    }

    @Override
    public void onReload(ConfigManager config) {
        playersOnly = config.getBooleanElse("Trigger.a.players-only", true);
        debug = config.getBooleanElse("Trigger.a.debug", false);
        windowSize = config.getIntElse("Trigger.a.sample-size", 10);
        maxSpread = config.getIntElse("Trigger.a.max-spread", 50);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            ticksSinceAttack++;
            return;
        }

        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;

        WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(event);
        if (interact.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;
        if (player.gamemode == GameMode.CREATIVE || player.gamemode == GameMode.SPECTATOR) return;
        if (player.inVehicle()) return;
        if (System.currentTimeMillis() - player.joinTime < 5000) return;

        if (ticksSinceAttack <= 3) {
            ticksSinceAttack = 0;
            lastAttackMs = System.currentTimeMillis();
            return;
        }
        ticksSinceAttack = 0;

        if (playersOnly) {
            PacketEntity entity = player.compensatedEntities.entityMap.get(interact.getEntityId());
            if (entity == null || entity.getType() != EntityTypes.PLAYER) return;
        }

        long now = System.currentTimeMillis();
        long interval = now - lastAttackMs;
        lastAttackMs = now;

        if (interval > 2000 || interval < 1) {
            count = 0;
            return;
        }

        intervals[count++] = interval;

        if (count >= windowSize) {
            long min = intervals[0], max = intervals[0];
            for (int i = 1; i < count; i++) {
                if (intervals[i] < min) min = intervals[i];
                if (intervals[i] > max) max = intervals[i];
            }
            long spread = max - min;

            if (debug) {
                ac.comet.cometac.utils.anticheat.LogUtil.info("[TriggerA] " + player.user.getName()
                        + " spread=" + spread + " min=" + min + " max=" + max);
            }

            if (spread < maxSpread) {
                if (++buffer > 4) flagAndAlert("spread=" + spread + "ms");
            } else {
                buffer = Math.max(0, buffer - 0.025);
                reward();
            }

            count = 0;
        }
    }
}
