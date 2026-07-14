package ac.comet.cometac.manager.init.stop;

import ac.comet.cometac.manager.init.Initable;

public interface StoppableInitable extends Initable {
    void stop();
}
