package std.trck.api;

import std.trck.api.routes.AssignmentsService;
import std.trck.api.routes.ErrorsService;
import std.trck.api.routes.ProfessorsService;
import std.trck.api.routes.SubjectsService;

import static spark.Spark.*;

import java.io.File;

public class StudyAPI {
    public static final File STATIC_DIR = new File("website/static");

    public static void init() {

        port(10040);

        staticFiles.externalLocation(STATIC_DIR.getAbsolutePath());

        AssignmentsService.basePageGet("/");
        AssignmentsService.setupAssignmentsPost("/api/assignments");
        AssignmentsService.setupAssignmentsGet("/api/assignments");

        SubjectsService.setupGetSubjects("/api/subjects");

        ProfessorsService.setupGetProfessors("/api/professors");

        ErrorsService.getMapping("/api/errors");
    }
}