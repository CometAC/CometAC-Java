package ac.comet.cometac.checks.impl.packetorder;

import ac.grim.grimac.api.storage.verbose.VerboseSchema;
import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.checks.impl.verbose.VerboseCodecs;
import ac.comet.cometac.checks.type.PacketCheck;
import ac.comet.cometac.player.CometPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBundle;
import it.unimi.dsi.fastutil.ints.IntArrayList;

@CheckData(name = "PacketOrderP", stableKey = "cometac.packetorder.transaction_response_order", experimental = true, verboseVersion = 2)
public class PacketOrderP extends Check implements PacketCheck {
    public static final VerboseSchema V = VerboseSchema.of(2, "kind:vi", "packetId:zz");

    static final int KIND_INVALID_RESPONSE = 0;
    static final int KIND_SKIPPED_RESPONSE = 1;

    public PacketOrderP(final CometPlayer player) {
        super(player);
    }

    private byte trimTimer; // let the list shrink eventually
    private final IntArrayList transactions = new IntArrayList(0);

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CHUNK_BATCH_ACK) {
            if (!transactions.rem(player.getLastTransactionReceived())) {
                flagAndAlert(V.write(verbose()).vi(KIND_INVALID_RESPONSE).zz(VerboseCodecs.PACKET_NONE));
            }
        } else if (!isAsync(event.getPacketType()) && !isTransaction(event.getPacketType())) {
            if (transactions.rem(player.getLastTransactionReceived())) {
                int packetId = VerboseCodecs.packetId(event.getPacketType(), player.getClientVersion());
                flagAndAlert(V.write(verbose()).vi(KIND_SKIPPED_RESPONSE).zz(packetId));
            }
        }
    }

    static String verbose(int kind, String packetType) {
        return switch (kind) {
            case KIND_INVALID_RESPONSE -> "invalid response";
            case KIND_SKIPPED_RESPONSE -> "skipped response, type=" + packetType;
            default -> "unknown";
        };
    }

    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.CHUNK_BATCH_END) {
            boolean sendingBundlePacket = player.packetStateData.sendingBundlePacket;
            if (!sendingBundlePacket) player.user.sendPacket(new WrapperPlayServerBundle());

            player.sendTransaction();
            int transaction = player.getLastTransactionSent();
            transactions.add(transaction);
            if (++trimTimer == 0) transactions.trim();
            player.addRealTimeTaskNext(() -> {
                if (transactions.rem(transaction)) {
                    flagAndAlert(V.write(verbose()).vi(KIND_SKIPPED_RESPONSE).zz(VerboseCodecs.PACKET_TRANSACTION));
                }
            });

            if (!sendingBundlePacket) {
                event.getTasksAfterSend().add(() -> player.user.sendPacket(new WrapperPlayServerBundle()));
            }
        }
    }
}
