package ac.comet.cometac.platform.fabric.utils.message;

import ac.comet.cometac.platform.api.sender.Sender;

public interface IFabricMessageUtil {
    Object textLiteral(String message);

    void sendMessage(Sender target, Object message, boolean overlay);
}
