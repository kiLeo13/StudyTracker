package std.trck.listeners.commands.assignments;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.database.records.AssignmentRec;
import std.trck.database.repositories.AssignmentsRepository;
import std.trck.listeners.commands.SlashCommand;

import java.util.List;

public class DeleteAssignmentCommand implements SlashCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAssignmentCommand.class);
    private static final AssignmentsRepository rep = new AssignmentsRepository();

    @Override
    public void onCommand(SlashCommandInteraction cmd) {

        String assgnId = cmd.getOption("assignment", OptionMapping::getAsString);
        AssignmentRec assignment = rep.fetchById(assgnId);

        if (assignment == null) {
            cmd.reply("The given assignment does not exist.").setEphemeral(true).queue();
            return;
        }

        try {
            rep.delete(assignment);
            cmd.replyFormat("Successfully deleted assignment `%s`!", assignment.getTitle()).queue();
        } catch (DataAccessException e) {
            cmd.reply("Could not delete assignment.").queue();
            LOGGER.error("Could not delete assignment '{}' [{}]", assignment.getTitle(), assgnId, e);
        }
    }

    @Override
    public String getDescription() {
        return "Deletes a given assignment.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "assignment", "The assignment to be deleted", true, true)
        );
    }
}