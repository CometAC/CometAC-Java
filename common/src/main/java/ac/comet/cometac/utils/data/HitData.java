package ac.comet.cometac.utils.data;

import ac.comet.cometac.utils.math.Vector3dm;

public class HitData {
    Vector3dm blockHitLocation;

    public HitData(Vector3dm blockHitLocation) {
        this.blockHitLocation = blockHitLocation;
    }

    public ac.comet.cometac.utils.math.Vector3dm getBlockHitLocation() {
        return this.blockHitLocation;
    }
}
