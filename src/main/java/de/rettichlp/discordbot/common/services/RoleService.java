package de.rettichlp.discordbot.common.services;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static de.rettichlp.discordbot.Application.discordBotProperties;
import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.time.Duration.ofDays;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;

@Log4j2
@EnableScheduling
@Service
public class RoleService {

    private static final Map<String, Duration> ROLE_DURATIONS = Map.ofEntries(
            Map.entry("1416128917017989160", ZERO), // Mickrige Mikrobe
            Map.entry("1416129463724539995", ofDays(14)), // Seltsames Samenkorn
            Map.entry("1416129868089004073", ofDays(30)), // Kleiner Keimling
            Map.entry("1416134449959010417", ofDays(60)), // Treue Topfpflanze
            Map.entry("1416132615248806028", ofDays(90)), // Unbekämpfbares Unkraut
            Map.entry("1416131773083160757", ofDays(150)), // Langlebiger Löwenzahn
            Map.entry("1416134085499424869", ofDays(250)), // Kräftige Kartoffel
            Map.entry("1500618135219212409", ofDays(365)), // Zornige Zwiebel
            Map.entry("1500625982745346129", ofDays(500)), // Baby Bambus
            Map.entry("1416130334566912142", ofDays(750)), // Kampflustiger Kaktus
            Map.entry("1500617714023010404", ofDays(1000)), // Auserwählte Avocado
            Map.entry("1416134852192899215", ofDays(1250)), // Paprika Pirat
            Map.entry("1416132207893942373", ofDays(1500)), // Brokkoli Baron
            Map.entry("1416130608702296255", ofDays(2000)), // Photosynthese Profi
            Map.entry("1416133651363791018", ofDays(2500)), // Kompost König
            Map.entry("1416131063159193691", ofDays(3000)), // Kosmischer Kürbis
            Map.entry("1500617161125793822", ofDays(3500)), // Mystisches Mushroom
            Map.entry("1500617433264951386", ofDays(4000)), // Chili Champion
            Map.entry("1416131253987709048", ofDays(4500)), // Sellerie Sensei
            Map.entry("1500618382972682300", ofDays(5000)) // Genmanipuliertes Gemüse
    );

    @Scheduled(cron = "0 0 */6 * * *", zone = "Europe/Berlin") // every day at 0:00, 6:00, 12:00, 18:00 (UTC+1)
    public void syncUserRoles() {
        log.info("Discord role synchronising: started");

        ofNullable(discordBotProperties.getGuild()).ifPresentOrElse(guild -> {
            guild.loadMembers(member -> {
                if (member.getUser().isBot()) {
                    return;
                }

                getRolesMemberShouldHave(member).forEach(role -> guild.addRoleToMember(member, role).queue(_ -> {
                    log.info("Discord role synchronising: Add role {} to member {}", role.getName(), member.getEffectiveName());
                }));
            });
        }, () -> log.warn("Discord role synchronising: Skipped! Guild is null"));
    }

    private List<Role> getRolesMemberShouldHave(@NotNull Member member) {
        Duration durationSinceJoin = between(member.getTimeJoined().toInstant(), now());
        return ROLE_DURATIONS.entrySet().stream()
                .filter(stringDurationEntry -> stringDurationEntry.getValue().compareTo(durationSinceJoin) <= 0)
                .map(Map.Entry::getKey)
                .map(s -> discordBotProperties.getGuild().getRoleById(s))
                .toList();
    }
}
