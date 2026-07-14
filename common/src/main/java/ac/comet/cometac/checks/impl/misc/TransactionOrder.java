package ac.comet.cometac.checks.impl.misc;

import ac.comet.cometac.checks.Check;
import ac.comet.cometac.checks.CheckData;
import ac.comet.cometac.player.CometPlayer;

@CheckData(name = "TransactionOrder", stableKey = "cometac.ping.invalid_transaction_order")
public class TransactionOrder extends Check {
    public TransactionOrder(CometPlayer player) {
        super(player);
    }
}
