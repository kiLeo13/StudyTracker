package std.trck.listeners.commands.assignments;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import std.trck.database.records.SubjectRec;
import std.trck.database.repositories.SubjectsRepository;
import std.trck.listeners.commands.SlashCommand;
import std.trck.managers.MapperManager;

import java.util.List;
import java.util.UUID;

public class AddAssignmentCommand implements SlashCommand {
    private static final MapperManager MODALS = MapperManager.getManager();
    private static final SubjectsRepository rep = new SubjectsRepository();

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onCommand(SlashCommandInteraction cmd) {

        int subjectId = cmd.getOption("subject", OptionMapping::getAsInt);
        String modalId = "assgn_" + UUID.randomUUID();
        Modal modal = Modal.create(modalId, "New Assignment")
                .addComponents(getTextInputs())
                .build();

        SubjectRec subject = rep.fetchById(subjectId);

        if (subject == null) {
            cmd.reply("The provided subject is unknown.").setEphemeral(true).queue();
            return;
        }

        cmd.replyModal(modal).queue();

        MODALS.create(modalId)
                .set("subject_id", subjectId)
                .set("professor_id", subject.getProfessorId())
                .save();
    }

    private List<ActionRow> getTextInputs() {

        TextInput title = TextInput.create("title", "📖 Title", TextInputStyle.SHORT)
                .setRequired(true)
                .setRequiredRange(2, 50)
                .setPlaceholder("Shakespeare's Influence on Literature.")
                .build();

        TextInput description = TextInput.create("desc", "📚 Description", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setRequiredRange(5, 1000)
                .setPlaceholder(
                        "Explore Shakespeare's impact on modern literature through his language, themes, and storytelling."
                )
                .build();

        int DATE_SIZE = "xx/xx/xxxx".length();
        int DATETIME_SIZE = "xx/xx/xxxx xx:xx".length();
        TextInput dueDate = TextInput.create("due_date", "📅 Due Date", TextInputStyle.SHORT)
                .setRequired(true)
                .setRequiredRange(DATE_SIZE, DATETIME_SIZE)
                .setPlaceholder("18/04/2024 23:59")
                .build();

        return List.of(
                ActionRow.of(title),
                ActionRow.of(description),
                ActionRow.of(dueDate)
        );
    }

    @Override
    public String getDescription() {
        return "Saves a new assignment in the database.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "subject", "The subject of the given assignment.", true)
                        .addChoices(getChoices())
        );
    }

    private List<Command.Choice> getChoices() {

        List<SubjectRec> subjects = rep.fetchAll();

        return subjects.stream()
                .map(s -> new Command.Choice(s.getSimpleName(), s.getId()))
                .toList();
    }
}