package de.rettichlp.discordbot.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static de.rettichlp.discordbot.Application.discordBotProperties;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.components.buttons.Button.secondary;
import static net.dv8tion.jda.api.components.buttons.Button.success;
import static net.dv8tion.jda.api.components.textinput.TextInputStyle.PARAGRAPH;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.modals.Modal.create;

@Getter
@AllArgsConstructor
public enum TicketCategory {

    GENERALLY("ticket_create", "Allgemeines Ticket", fromUnicode("U+1F3AB")),
    PKUTILS("ticket_create_pkutils", "Ticket (PKUtils)", fromUnicode("U+1F9E9")),
    RLPTP("ticket_create_rlptp", "Ticket (RLPTP)", fromUnicode("U+1F5BC"));

    private final String id;
    private final String buttonLabel;
    private final Emoji emoji;

    @NotNull
    public Button getTicketCreateButton() {
        return this == GENERALLY
                ? success("btn_" + this.id, this.buttonLabel).withEmoji(this.emoji)
                : secondary("btn_" + this.id, this.buttonLabel).withEmoji(this.emoji);
    }

    @NotNull
    public Modal getTicketModal() {
        TextInput logInput = TextInput.create("tip_topic", "Anliegen", PARAGRAPH)
                .setRequired(true)
                .setPlaceholder("Hey, ich brauche bitte Hilfe bei ...")
                .build();

        return create("mdl_" + this.id, "Neues Ticket")
                .addComponents(ActionRow.of(logInput))
                .build();
    }

    public void createTicketChannel(IReplyCallback event, @NotNull Member member, String topic) {
        String userName = member.getUser().getName();
        String memberId = member.getId();

//        Role supporterRole = discordBotProperties.getSupporterRole(); TODO
//        Role moderatorRole = discordBotProperties.getModeratorRole();
        discordBotProperties.getTicketCategory().createTextChannel("ticket-" + userName)
                .setTopic("Ticket von " + userName + " (" + memberId + ")")
                .addPermissionOverride(discordBotProperties.getGuild().getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL), null)
//                .addPermissionOverride(discordBotProperties.getSeniorModeratorRole(), EnumSet.of(VIEW_CHANNEL), null)
//                .addPermissionOverride(moderatorRole, EnumSet.of(VIEW_CHANNEL), null)
//                .addPermissionOverride(supporterRole, EnumSet.of(VIEW_CHANNEL), null)
                .queue(textChannel -> textChannel
                        .sendMessage("Hey " + member.getAsMention() + "! Danke, dass du ein " + this.buttonLabel + " erstellt hast. Das Ticket wird schnellstmöglich bearbeitet.\n"
                                + "Anliegen: " + topic)
                        .addComponents(ActionRow.of(success("btn_ticket_close", "Ticket schließen").withEmoji(fromUnicode("U+1F512"))))
                        .queue(message -> event.reply("Du hast ein Ticket erstellt: " + message.getJumpUrl()).setEphemeral(true).queue()));
    }
}
