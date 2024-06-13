package std.trck;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jooq.exception.DataAccessException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.api.StudyAPI;
import std.trck.database.DatabaseInitializer;
import std.trck.internal.BotData;
import std.trck.jobs.AssignmentReminder;
import std.trck.listeners.autocomplete.AssignmentsAutoCompletion;
import std.trck.listeners.commands.SlashCommandsGateway;
import std.trck.listeners.commands.assignments.list.AssignmentListUpdate;
import std.trck.listeners.modals.AssignmentCreationModal;

public class StudyTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudyTracker.class);
    private static JDA api;

    public static void main(String[] args) {

        try {
            api = JDABuilder.createDefault(BotData.fetch("app.token"))
                    .build()
                    .awaitReady();

            DatabaseInitializer.init();
            StudyAPI.init();

            SlashCommandsGateway.registerCommands(api);
            registerListeners(api);
            registerJobs();

        } catch (DataAccessException e) {
            LOGGER.error("Could not initialize database", e);
        } catch (InterruptedException e) {
            LOGGER.error("Could not log in, exiting...", e);
        } catch (SchedulerException e) {
            LOGGER.error("Could not register chron job", e);
        }
    }

    public static JDA getApi() {
        return api;
    }

    private static void registerListeners(JDA api) {

        api.addEventListener(
                new SlashCommandsGateway(),

                new AssignmentsAutoCompletion(),
                new AssignmentCreationModal(),

                new AssignmentListUpdate()
        );
    }

    private static void registerJobs() throws SchedulerException {

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail detail = JobBuilder.newJob(AssignmentReminder.class)
                .withIdentity("assignment_reminder")
                .build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("assignment_reminder")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 15 ? * * *")) // Every day at 3:00 PM
                .build();

        scheduler.scheduleJob(detail, trigger);
        scheduler.start();
    }
}