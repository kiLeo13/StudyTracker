package std.trck.database.repositories;

import std.trck.database.Repository;
import std.trck.database.records.ProfessorRec;
import std.trck.database.tables.Professors;

public class ProfessorsRepository implements Repository<Integer, Professors, ProfessorRec> {

    @Override
    public Professors getTable() {
        return Professors.PROFESSORS;
    }
}