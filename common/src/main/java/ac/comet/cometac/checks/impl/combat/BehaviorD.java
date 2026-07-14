package ac.comet.cometac.checks.impl.combat;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.utils.anticheat.LogUtil;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.grim.grimac.api.config.ConfigManager;
import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.comet.cometac.utils.data.packetentity.PacketEntity;
import ac.comet.cometac.utils.nmsutil.ReachUtils;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

@CheckData(name = "BehaviorD", stableKey = "cometac.combat.attack_without_look", configName = "Behavior", description = "Checks for attacking without looking at target")
public class BehaviorD extends Check implements PacketCheck {

    private double angleThreshold = 45.0;
    private boolean playersOnly = true;
    private boolean debug = false;
    private final IntList attackQueue = new IntArrayList();

    public BehaviorD(CometPlayer player) {
        super(player);
    }

    @Override
    public void onReload(ConfigManager config) {
        angleThreshold = config.getDoubleElse("Behavior.d.angle-threshold", 45.0);
        playersOnly = config.getBooleanElse("Behavior.d.players-only", true);
        debug = config.getBooleanElse("Behavior.d.debug", false);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(event);
            if (interact.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

            if (player.gamemode == GameMode.CREATIVE || player.gamemode == GameMode.SPECTATOR) return;
            if (player.inVehicle()) return;
            if (player.isGliding || player.wasGliding) return;
            if (System.currentTimeMillis() - player.joinTime < 5000) return;
            if (player.packetStateData.lastPacketWasTeleport) return;

            PacketEntity entity = player.compensatedEntities.entityMap.get(interact.getEntityId());
            if (entity == null || !entity.canHit()) return;
            if (!entity.isLivingEntity && entity.getType() != EntityTypes.END_CRYSTAL) return;
            if (playersOnly && entity.getType() != EntityTypes.PLAYER) return;

            if (attackQueue.size() <= 10) {
                attackQueue.add(interact.getEntityId());
            }
        }

        // Process queued attacks on movement/tick packets (rotation is now up to date)
        if (isUpdate(event.getPacketType())) {
            processQueue();
        }
    }

    private void processQueue() {
        for (int i = 0; i < attackQueue.size(); i++) {
            int entityId = attackQueue.getInt(i);
            PacketEntity entity = player.compensatedEntities.entityMap.get(entityId);
            if (entity == null) continue;

            SimpleCollisionBox box = entity.getPossibleCollisionBoxes();

            // Skip if player is inside or very close to the hitbox
            if (ReachUtils.getMinReachToBox(player, box) < 0.5) continue;

            double minAngle = ReachUtils.getMinAngleToBox(player, box);

            if (debug) {
                LogUtil.info("[BehaviorD DEBUG] " + player.getName() + " " + String.format("angle=%.1f threshold=%.1f", minAngle, angleThreshold));
            }

            if (minAngle >= angleThreshold) {
                flagAndAlert(String.format("angle=%.1f", minAngle));
            }
        }
        attackQueue.clear();
    }
}
