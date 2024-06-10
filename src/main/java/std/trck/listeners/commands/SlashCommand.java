package std.trck.listeners.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface SlashCommand {

    void onCommand(SlashCommandInteraction cmd);

    String getDescription();

    default List<OptionData> getOptions() {
        return List.of();
    }
}