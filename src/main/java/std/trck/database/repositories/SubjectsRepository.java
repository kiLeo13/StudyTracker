package std.trck.database.repositories;

import std.trck.database.Repository;
import std.trck.database.records.SubjectRec;
import std.trck.database.tables.Subjects;

public class SubjectsRepository implements Repository<Integer, Subjects, SubjectRec> {

    @Override
    public Subjects getTable() {
        return Subjects.SUBJECTS;
    }
}