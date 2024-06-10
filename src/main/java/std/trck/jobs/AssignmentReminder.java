package std.trck.jobs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.StudyTracker;
import std.trck.database.fusions.AssignmentData;
import std.trck.database.records.ProfessorRec;
import std.trck.database.records.SubjectRec;
import std.trck.database.repositories.AssignmentsRepository;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class AssignmentReminder implements Job {
    private static final long NOTIFICATION_CHANNEL_ID = 1249055438968848498L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentReminder.class);
    private static final Color COLOR_YELLOW = new Color(255, 255, 100);
    private static final AssignmentsRepository rep = new AssignmentsRepository();

    @Override
    public void execute(JobExecutionContext ctx) {

        JDA api = StudyTracker.getApi();
        List<AssignmentData> assignments = fetchSoonAssignments();
        TextChannel chan = api.getTextChannelById(NOTIFICATION_CHANNEL_ID);

        if (chan == null) {
            LOGGER.warn("Announcements channel for id [{}] was not found", NOTIFICATION_CHANNEL_ID);
            return;
        }

        for (AssignmentData assgn : assignments) {

            Guild guild = chan.getGuild();
            SubjectRec subject = assgn.getSubject();
            MessageEmbed embed = embed(guild, assgn);

            chan.sendMessageFormat("@everyone Atividade para <t:%d> de `%s`.", assgn.getDueDate(), subject.getSimpleName())
                    .setEmbeds(embed)
                    .queue();
        }
    }

    private MessageEmbed embed(Guild guild, AssignmentData assgn) {

        EmbedBuilder builder = new EmbedBuilder();
        ProfessorRec professor = assgn.getProfessor();

        builder
                .setTitle(assgn.getTitle())
                .setColor(COLOR_YELLOW)
                .setDescription(assgn.getDescription())
                .addField("👤 Teacher", professor.getFullName(), true)
                .addField("📅 Since", "<t:" + assgn.getTimeCreated() + '>', true)
                .addField("🖨 Due Date", "<t:" + assgn.getDueDate() + '>', true)
                .setTimestamp(Instant.now())
                .setFooter(null, guild.getIconUrl());

        return builder.build();
    }

    private List<AssignmentData> fetchSoonAssignments() {

        long future = LocalDate.now()
                .plusDays(2)
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.ofHours(-3));

        return rep.fetchFullAssignments(false, a -> a.DUE_DATE.le(future));
    }
}