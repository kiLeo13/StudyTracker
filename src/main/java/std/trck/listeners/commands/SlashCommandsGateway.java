package std.trck.listeners.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import std.trck.listeners.commands.assignments.AddAssignmentCommand;
import std.trck.listeners.commands.assignments.DeleteAssignmentCommand;
import std.trck.listeners.commands.assignments.list.ListAssignmentsCommand;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandsGateway extends ListenerAdapter {
    private static final Map<String, SlashCommand> commands = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        String subcmd = event.getSubcommandName();

        if (subcmd == null)
            return;

        SlashCommand cmd = commands.get(subcmd);

        if (cmd != null)
            cmd.onCommand(event);
    }

    public static void registerCommands(JDA api) {

        commands.put("add", new AddAssignmentCommand());
        commands.put("delete", new DeleteAssignmentCommand());
        commands.put("list", new ListAssignmentsCommand());

        pushCommands(api);
    }

    private static void pushCommands(JDA api) {

        api.updateCommands().addCommands(
                Commands.slash("assignment", "Manage the school assignments.")
                        .addSubcommands(
                                toSubcommand("add", commands.get("add")),
                                toSubcommand("delete", commands.get("delete")),
                                toSubcommand("list", commands.get("list"))
                        )
        ).queue();
    }

    private static SubcommandData toSubcommand(String name, SlashCommand cmd) {

        return new SubcommandData(name, cmd.getDescription())
                .addOptions(cmd.getOptions());
    }
}