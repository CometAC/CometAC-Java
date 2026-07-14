package ac.comet.cometac.platform.fabric.initables;

import ac.comet.cometac.CometAPI;
import ac.comet.cometac.manager.init.start.StartableInitable;
import ac.comet.cometac.manager.init.stop.StoppableInitable;
import ac.comet.cometac.platform.fabric.utils.metrics.MetricsFabric;
import ac.comet.cometac.utils.anticheat.Constants;

public class FabricBStats implements StartableInitable, StoppableInitable {

    private MetricsFabric metricsFabric;

    @Override
    public void start() {
        try {
            metricsFabric = new MetricsFabric(CometAPI.INSTANCE.getGrimPlugin(), Constants.BSTATS_PLUGIN_ID);
        } catch (Exception ignored) {}
    }

    @Override
    public void stop() {
        if (metricsFabric != null)
            metricsFabric.shutdown();
    }
}
