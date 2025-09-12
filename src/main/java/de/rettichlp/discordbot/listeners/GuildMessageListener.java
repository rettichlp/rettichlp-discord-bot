package de.rettichlp.discordbot.listeners;

import de.rettichlp.discordbot.common.registry.EventListener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

@EventListener
public class GuildMessageListener extends ListenerAdapter {

    private static final Pattern INVITE_LINK_PATTERN = compile("(https?://)?(www\\.)?discord\\.(gg|io|me|li|com)/(invite/\\w+|\\w+)");
    private static final Pattern WHY_PATTERN = compile("(?i)^warum");
    private static final String[] LINK_DELETED_MESSAGES = {
            "Autsch, dieser Link war verboten. Keine Sorge, ich hab ihn unschädlich gemacht.",
            "Dieser Invite-Link war wohl auf Abwegen. Jetzt ist er weg!",
            "Dieser Invite-Link? Nicht heute, Freundchen!",
            "Dieser Link war wohl nicht ganz richtig. Ab in den digitalen Papierkorb!",
            "Du weißt, dass verbotene Invite-Links hier keinen Platz haben, oder?",
            "Huch, hast du da gerade einen verbotenen Invite-Link fallen lassen? Weg damit!",
            "Ich hab diesen Link mal schnell durch den digitalen Aktenvernichter gejagt.",
            "Link entfernt. Versuch's doch mal mit Katzenvideos, die sind immer erlaubt!",
            "Link gelöscht. Keine Panik, das Internet ist immer noch da (größtenteils).",
            "Link gelöscht. Wie wär's mit einem schönen GIF stattdessen? Die sind lustiger!",
            "Netter Versuch, aber dieser Invite-Link gehört in den Müll!",
            "Nice try, aber der Invite-Link fliegt raus!",
            "Oh nein, ein verirrter Invite-Link! Keine Sorge, hab ihn entsorgt.",
            "Oha, ein verbotener Invite-Link. Nicht mit mir!",
            "Oops! Das war wohl der falsche Link, versuch's doch nochmal ohne!",
            "Oops, ein falscher Link! Keine Sorge, ich hab ihn beseitigt. Weitermachen!",
            "Oops, ein verbotener Invite-Link! Weggeblasen wie Staub im Wind.",
            "Pech gehabt, dein verbotener Invite-Link hat hier nichts verloren.",
            "Raus mit dem Invite-Link! Versuchs mal ohne Schleichwerbung.",
            "Schön versucht, aber der Invite-Link ist jetzt Geschichte.",
            "Schön, dass du uns besuchst, aber der Invite-Link bleibt draußen.",
            "Sorry, dein Invite-Link hat's nicht durch die Sicherheitskontrolle geschafft.",
            "Ups, dieser Invite-Link ist wohl aus Versehen hier gelandet. Gelöscht!",
            "Verbotener Invite-Link aufgespürt und entsorgt!",
            "Verbotener Invite-Link? Nicht heute, nicht hier!",
            "Verbotener Link erkannt und entfernt! Lass uns den Server sauber halten.",
            "Verbotener Link gesichtet und eliminiert!",
            "Versuch's nochmal ohne den verbotenen Invite-Link. Danke!",
            "Wir mögen Gäste, aber dieser Invite-Link war nicht willkommen."
    };

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        String messageContentRaw = message.getContentRaw();
        Member member = event.getMember();

        Matcher inviteLinkMatcher = INVITE_LINK_PATTERN.matcher(messageContentRaw);
        if (inviteLinkMatcher.find() && nonNull(member) && isDisallowedToSendInviteLinks(member)) {
            message.reply(member.getAsMention() + " " + getRandomDeletionMessage()).queue(_ -> message.delete().queue());
            return;
        }

        Matcher whyMatcher = WHY_PATTERN.matcher(messageContentRaw);
        if (whyMatcher.find()) {
            event.getChannel().sendMessage("Darum.").queue();
        }
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        Message message = event.getMessage();
        String messageContentRaw = message.getContentRaw();
        Member member = event.getMember();

        Matcher inviteLinkMatcher = INVITE_LINK_PATTERN.matcher(messageContentRaw);
        if (inviteLinkMatcher.find() && nonNull(member) && isDisallowedToSendInviteLinks(member)) {
            message.reply(member.getAsMention() + " " + getRandomDeletionMessage()).queue(_ -> message.delete().queue());
        }
    }

    private boolean isDisallowedToSendInviteLinks(Member member) {
        return ofNullable(member)
                .map(m -> m.hasPermission(MESSAGE_MANAGE))
                .orElse(false);
    }

    private String getRandomDeletionMessage() {
        return LINK_DELETED_MESSAGES[new Random().nextInt(LINK_DELETED_MESSAGES.length)];
    }
}
