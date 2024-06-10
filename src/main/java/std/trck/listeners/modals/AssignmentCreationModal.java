package std.trck.listeners.modals;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.database.records.AssignmentRec;
import std.trck.database.repositories.AssignmentsRepository;
import std.trck.database.tables.Assignments;
import std.trck.managers.MapperManager;
import std.trck.utils.Bot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class AssignmentCreationModal extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentCreationModal.class);
    private static final MapperManager MODALS = MapperManager.getManager();
    private static final AssignmentsRepository rep = new AssignmentsRepository();

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(Assignments.DATETIME_FORMATTER);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(Assignments.DATE_FORMATTER);

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        String modalId = e.getModalId();
        Map<String, Object> data = MODALS.getData(modalId);

        if (data == null) {
            e.reply("No data found for provided form.").setEphemeral(true).queue();
            return;
        }

        try {
            String title = getValue(e, "title");
            String desc = getValue(e, "desc");
            long dueDate = parseEpochDueDate(getValue(e, "due_date").strip());
            int professorId = (int) data.get("professor_id");
            int subjectId = (int) data.get("subject_id");

            if (dueDate <= Bot.unixNow()) {
                e.replyFormat("Due date cannot be in the past, provided: <t:%d>.", dueDate).setEphemeral(true).queue();
                return;
            }

            AssignmentRec assignment = new AssignmentRec(professorId, subjectId, title, desc, dueDate);

            rep.save(assignment);
            e.replyFormat("Assignment `%s` saved successfully.", title).queue();
        } catch (DateTimeParseException err) {
            e.reply("Due date format is incorrect, try following the given example in the form.").setEphemeral(true).queue();
            LOGGER.error("Could not parse due date", err);
        } catch (DataAccessException | ClassCastException err) {
            LOGGER.error("Could not save assignment", err);
        }
    }

    private long parseEpochDueDate(String input) {

        LocalDateTime dateTime;

        if (isDatePartial(input))
            dateTime = LocalDate.parse(input, DATE_FORMATTER).atStartOfDay();
        else
            dateTime = LocalDateTime.parse(input, DATETIME_FORMATTER);

        return dateTime.toInstant(ZoneOffset.ofHours(-3)).getEpochSecond();
    }

    private boolean isDatePartial(String input) {
        return input.length() < Assignments.DATETIME_FORMATTER.length();
    }

    private String getValue(ModalInteraction m, String key) {

        ModalMapping mapping = m.getValue(key);
        assert mapping != null: "Modal mapping for id [" + key + "] may not be null";

        return mapping.getAsString();
    }
}