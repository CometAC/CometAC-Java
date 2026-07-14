package ac.comet.cometac.utils.data.packetentity;

import ac.comet.cometac.player.CometPlayer;
import ac.comet.cometac.utils.data.VectorData;

import java.util.Set;

public interface JumpableEntity {

    boolean isJumping();

    void setJumping(boolean jumping);

    float getJumpPower();

    void setJumpPower(float jumpPower);

    boolean canPlayerJump(CometPlayer player);

    boolean hasSaddle();

    void executeJump(CometPlayer player, Set<VectorData> possibleVectors);

}
