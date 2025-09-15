package de.rettichlp.discordbot.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static de.rettichlp.discordbot.Application.discordBotProperties;
import static java.util.Objects.nonNull;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.components.buttons.Button.secondary;
import static net.dv8tion.jda.api.components.buttons.Button.success;
import static net.dv8tion.jda.api.components.textinput.TextInputStyle.PARAGRAPH;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.modals.Modal.create;

@Getter
@AllArgsConstructor
public enum TicketCategory {

    GENERALLY("ticket_create", "Allgemeines Ticket", fromUnicode("U+1F3AB"), null),
    PKUTILS("ticket_create_pkutils", "Ticket (PKUtils)", fromUnicode("U+1F9E9"), discordBotProperties.getGuild().getRoleById("1413069992123170877")),
    RLPTP("ticket_create_rlptp", "Ticket (RLPTP)", fromUnicode("U+1F5BC"), discordBotProperties.getGuild().getRoleById("1417089576706641920"));

    private final String id;
    private final String buttonLabel;
    private final Emoji emoji;
    private final Role secretaryRole;

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

        ChannelAction<TextChannel> textChannelChannelAction = discordBotProperties.getTicketCategory().createTextChannel("ticket-" + userName)
                .setTopic("Ticket von " + userName + " (" + memberId + ")")
                .addPermissionOverride(discordBotProperties.getGuild().getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL), null);

        if (nonNull(this.secretaryRole)) {
            textChannelChannelAction = textChannelChannelAction.addPermissionOverride(this.secretaryRole, EnumSet.of(VIEW_CHANNEL), null);
        }

        textChannelChannelAction.queue(textChannel -> textChannel
                    .sendMessage("Hey " + member.getAsMention() + "! Danke, dass du ein " + this.buttonLabel + " erstellt hast. Das Ticket wird schnellstmöglich bearbeitet.\n" +
                            "Anliegen: " + topic + (nonNull(this.secretaryRole) ? "\n" + this.secretaryRole.getAsMention() : ""))
                    .addComponents(ActionRow.of(success("btn_ticket_close", "Ticket schließen").withEmoji(fromUnicode("U+1F512"))))
                    .queue(message -> event.reply("Du hast ein Ticket erstellt: " + message.getJumpUrl()).setEphemeral(true).queue()));
    }
}
