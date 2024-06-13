package std.trck.listeners.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import std.trck.database.records.AssignmentRec;
import std.trck.database.repositories.AssignmentsRepository;

import java.util.List;

public class AssignmentsAutoCompletion extends ListenerAdapter {
    private static final AssignmentsRepository rep = new AssignmentsRepository();

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {

        AutoCompleteQuery opt = e.getFocusedOption();
        String name = opt.getName();
        String value = opt.getValue();

        if (!name.equals("assignment"))
            return;

        List<AssignmentRec> assignments = fetchAssignments(value);

        e.replyChoices(intoChoices(assignments)).queue();
    }

    private List<AssignmentRec> fetchAssignments(String search) {
        return rep.fetchWhere(20, t -> t.TITLE.like('%' + search + '%'));
    }

    private List<Command.Choice> intoChoices(List<AssignmentRec> assgn) {

        return assgn.stream()
                .map(a -> new Command.Choice(a.getTitle(), a.getUUID()))
                .toList();
    }
}