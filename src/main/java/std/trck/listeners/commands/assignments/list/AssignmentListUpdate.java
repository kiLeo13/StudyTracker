package std.trck.listeners.commands.assignments.list;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import std.trck.database.fusions.AssignmentData;
import std.trck.database.repositories.AssignmentsRepository;
import std.trck.managers.MapperManager;

import java.util.List;
import java.util.Map;

public class AssignmentListUpdate extends ListenerAdapter {
    private static final MapperManager mngr = MapperManager.getManager();
    private static final AssignmentsRepository assgnRep = new AssignmentsRepository();
    private static final MessageEmbed NOTHING_FOUND = ListAssignmentsCommand.NOTHING_FOUND;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        Map<String, Object> data = mngr.getData(e.getComponentId());

        if (data == null)
            return;

        e.deferEdit().queue();

        boolean past = (boolean) data.get("past");
        int offset = (int) data.get("new_offset");
        int profId = (int) data.get("professor_id");
        int subjId = (int) data.get("subject_id");
        int assgnCount = assgnRep.fetchCount(profId, subjId, past);
        AssignmentData assgn = assgnRep.fetchFullAssignment(profId, subjId, past, offset);
        Guild guild = e.getGuild();

        if (assgn == null) {
            e.getHook().editOriginalEmbeds(NOTHING_FOUND).setReplace(true).queue();
            return;
        }

        boolean hasNext = offset + 1 < assgnCount;
        MessageEmbed embed = ListAssignmentsCommand.embed(guild, assgn, offset, assgnCount);
        List<Button> buttons = ListAssignmentsCommand.getButtons(offset, hasNext, profId, subjId, past);

        e.getHook()
                .editOriginalEmbeds(embed)
                .setActionRow(buttons)
                .queue();
    }
}