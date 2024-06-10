package std.trck.listeners.commands.assignments.list;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import std.trck.database.fusions.AssignmentData;
import std.trck.database.records.ProfessorRec;
import std.trck.database.records.SubjectRec;
import std.trck.database.repositories.AssignmentsRepository;
import std.trck.database.repositories.ProfessorsRepository;
import std.trck.database.repositories.SubjectsRepository;
import std.trck.listeners.commands.SlashCommand;
import std.trck.managers.MapperManager;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ListAssignmentsCommand implements SlashCommand {
    private static final MapperManager mngr = MapperManager.getManager();
    private static final ProfessorsRepository professorsRep = new ProfessorsRepository();
    private static final AssignmentsRepository assgnRep = new AssignmentsRepository();
    private static final SubjectsRepository subjectRep = new SubjectsRepository();
    static final MessageEmbed NOTHING_FOUND;

    private static final Color COLOR_EXPIRED = new Color(255, 100, 100);
    private static final Color COLOR_VALID   = new Color(100, 255, 100);

    @Override
    public void onCommand(SlashCommandInteraction cmd) {

        boolean past = cmd.getOption("past", false, OptionMapping::getAsBoolean);
        int professorId = cmd.getOption("professor", -1, OptionMapping::getAsInt);
        int subjectId = cmd.getOption("subject", -1, OptionMapping::getAsInt);
        int assgnCount = assgnRep.fetchCount(professorId, subjectId, past);
        AssignmentData assgn = assgnRep.fetchFullAssignment(professorId, subjectId, past, 0);
        Guild guild = cmd.getGuild();

        if (assgn == null) {
            cmd.replyEmbeds(NOTHING_FOUND).queue();
            return;
        }

        boolean hasNext = assgnCount > 1;
        MessageEmbed embed = embed(guild, assgn, 0, assgnCount);
        List<Button> buttons = getButtons(0, hasNext, professorId, subjectId, past);

        cmd.replyEmbeds(embed)
                .addActionRow(buttons)
                .queue();
    }

    protected static MessageEmbed embed(Guild guild, AssignmentData assgn, int currOffset, int totalFound) {

        EmbedBuilder builder = new EmbedBuilder();
        Color color = assgn.isExpired() ? COLOR_EXPIRED : COLOR_VALID;
        ProfessorRec professor = assgn.getProfessor();
        String pages = String.format("Page %02d/%02d", currOffset + 1, totalFound);

        builder
                .setTitle(assgn.getTitle())
                .setColor(color)
                .setDescription(assgn.getDescription())
                .addField("👤 Teacher", professor.getFullName(), true)
                .addField("📅 Saved At", "<t:" + assgn.getTimeCreated() + '>', true)
                .addField("🖨 Due Date", "<t:" + assgn.getDueDate() + '>', true)
                .addField("🔖 Subject", assgn.getSubject().getName(), false)
                .setTimestamp(Instant.now())
                .setFooter(pages, guild.getIconUrl());

        return builder.build();
    }

    protected static List<Button> getButtons(int currentOffset, boolean hasNext, int professorId, int subjectId, boolean past) {

        boolean hasPrev = currentOffset > 0;
        String prevId = "prev_" + UUID.randomUUID();
        String nextId = "next_" + UUID.randomUUID();

        Button previous = Button.primary(prevId, Emoji.fromUnicode("◀")).withDisabled(!hasPrev);
        Button next     = Button.primary(nextId, Emoji.fromUnicode("▶")).withDisabled(!hasNext);

        registerButton(previous, currentOffset - 1, professorId, subjectId, past);
        registerButton(next,     currentOffset + 1, professorId, subjectId, past);

        return List.of(previous, next);
    }

    private static void registerButton(Button button, int toOffset, int professorId, int subjectId, boolean past) {
        mngr.create(button.getId())
                .set("new_offset", toOffset)
                .set("professor_id", professorId)
                .set("subject_id", subjectId)
                .set("past", past)
                .save();
    }

    @Override
    public String getDescription() {
        return "Lists the assignments given in the class.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.BOOLEAN, "past", "Whether should also be displayed old assignments (defaults to False)."),

                new OptionData(OptionType.INTEGER, "subject", "The subject of the assignment.")
                        .addChoices(getSubjectChoices()),

                new OptionData(OptionType.INTEGER, "professor", "The professor responsible for the given assignment.")
                        .addChoices(getProfessorChoices())
        );
    }

    private List<Command.Choice> getProfessorChoices() {

        List<ProfessorRec> professors = professorsRep.fetchAll();

        return professors.stream()
                .map(p -> new Command.Choice(p.getName(), p.getId()))
                .toList();
    }

    private List<Command.Choice> getSubjectChoices() {

        List<SubjectRec> subjects = subjectRep.fetchAll();

        return subjects.stream()
                .map(s -> new Command.Choice(s.getSimpleName(), s.getId()))
                .toList();
    }

    static {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(COLOR_EXPIRED)
                .setAuthor("❌ Nothing found");

        NOTHING_FOUND = builder.build();
    }
}