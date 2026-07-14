package ac.comet.cometac.platform.api.manager.cloud;

import ac.comet.cometac.platform.api.command.PlayerSelector;
import ac.comet.cometac.platform.api.sender.Sender;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.SuggestionProvider;

public interface CloudPlatformCommandArguments {
    ParserDescriptor<Sender, PlayerSelector> singlePlayerSelectorParser();

    SuggestionProvider<Sender> onlinePlayerSuggestions();
}
