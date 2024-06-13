package std.trck.database.fusions;

import org.jooq.Record;
import std.trck.database.records.ProfessorRec;
import std.trck.database.records.SubjectRec;
import std.trck.utils.Bot;

import static std.trck.database.tables.Assignments.ASSIGNMENTS;
import static std.trck.database.tables.Professors.PROFESSORS;
import static std.trck.database.tables.Subjects.SUBJECTS;

public class AssignmentData {
    private final String uuid;
    private final String title;
    private final String description;
    private final SubjectRec subject;
    private final ProfessorRec professor;
    private final long dueDate;
    private final long timeCreated;

    public AssignmentData(Record rec) {
        this.uuid = rec.get(ASSIGNMENTS.ID);
        this.title = rec.get(ASSIGNMENTS.TITLE);
        this.description = rec.get(ASSIGNMENTS.DESCRIPTION);
        this.subject = new SubjectRec(rec.get(SUBJECTS.ID), rec.get(SUBJECTS.SIMPLE_NAME), rec.get(SUBJECTS.NAME));
        this.professor = new ProfessorRec(rec.get(PROFESSORS.ID), rec.get(PROFESSORS.NAME), rec.get(PROFESSORS.FULL_NAME));
        this.dueDate = rec.get(ASSIGNMENTS.DUE_DATE);
        this.timeCreated = rec.get(ASSIGNMENTS.CREATED_AT);
    }

    public String getUUID() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SubjectRec getSubject() {
        return subject;
    }

    public ProfessorRec getProfessor() {
        return professor;
    }

    public long getDueDate() {
        return dueDate;
    }

    public boolean isExpired() {
        return getDueDate() < Bot.unixNow();
    }

    public long getTimeCreated() {
        return timeCreated;
    }
}