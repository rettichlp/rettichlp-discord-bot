package de.rettichlp.discordbot.buttons;

import de.rettichlp.discordbot.common.registry.Button;
import de.rettichlp.discordbot.common.registry.ButtonBase;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;

import static de.rettichlp.discordbot.Application.discordBotProperties;
import static java.util.Objects.requireNonNull;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.components.buttons.Button.secondary;
import static net.dv8tion.jda.api.components.buttons.Button.success;
import static net.dv8tion.jda.api.components.textinput.TextInputStyle.PARAGRAPH;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;
import static net.dv8tion.jda.api.interactions.modals.Modal.create;

@Button(label = "btn_ticket_create")
public class TicketCreateButton extends ButtonBase {

    public TicketCreateButton(String label) {
        super(label);
    }

    @Override
    public void onButtonClick(@NotNull ButtonInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        String memberId = member.getId();

        boolean hasTicketChannel = discordBotProperties.getTicketCategory().getChannels().stream()
                .map(guildChannel -> discordBotProperties.getGuild().getTextChannelById(guildChannel.getId()))
                .filter(Objects::nonNull)
                .map(StandardGuildMessageChannel::getTopic)
                .filter(Objects::nonNull)
                .anyMatch(s -> s.contains(memberId));

        if (hasTicketChannel) {
            event.reply("Du hast bereits einen Ticket Channel!").setEphemeral(true).queue();
            return;
        }

        event.replyModal(getTicketModal()).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equalsIgnoreCase("mdl_ticket")) {
            return;
        }

        Member member = event.getMember();
        assert member != null;
        String memberId = member.getId();
        String userName = member.getUser().getName();

        String minecraftName = requireNonNull(event.getValue("tip_minecraft_name")).getAsString();
        String log = requireNonNull(event.getValue("tip_topic")).getAsString();

//        Role supporterRole = discordBotProperties.getSupporterRole();
//        Role moderatorRole = discordBotProperties.getModeratorRole();
        discordBotProperties.getTicketCategory().createTextChannel("ticket-" + userName)
                .setTopic("Ticket von " + userName + " (" + memberId + ")")
                .addPermissionOverride(discordBotProperties.getGuild().getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL), null)
//                .addPermissionOverride(discordBotProperties.getSeniorModeratorRole(), EnumSet.of(VIEW_CHANNEL), null)
//                .addPermissionOverride(moderatorRole, EnumSet.of(VIEW_CHANNEL), null)
//                .addPermissionOverride(supporterRole, EnumSet.of(VIEW_CHANNEL), null)
                .queue(textChannel -> textChannel
                        .sendMessage("Hey " + member.getAsMention() + "! Danke dass du ein Ticket erstellt hast. Die " + /*supporterRole.getAsMention() + " und " + moderatorRole.getAsMention() +*/ " werden Dir schnellstmöglich deine Frage beantworten oder Dir helfen.\n"
                                + "Spieler:  " + minecraftName + "\n"
                                + "Anliegen: " + log)
                        .addComponents(ActionRow.of(success("btn_ticket_close", "Ticket schließen").withEmoji(fromUnicode("U+1F512")), secondary("btn_ticket_voice", "Voice-Channel erstellen").withEmoji(fromUnicode("U+1F50A"))))
                        .queue(message -> event.reply("Du hast ein Ticket erstellt: " + message.getJumpUrl()).setEphemeral(true).queue()));
    }

    @NotNull
    private Modal getTicketModal() {
        TextInput minecraftNameTextInput = TextInput.create("tip_minecraft_name", "Minecraft Name", TextInputStyle.SHORT)
                .setMinLength(3)
                .setMaxLength(16)
                .setRequired(true)
                .build();

        TextInput logInput = TextInput.create("tip_topic", "Anliegen", PARAGRAPH)
                .setRequired(true)
                .setPlaceholder("Hey, wie kann ich mich im Bauteam bewerben?")
                .build();

        return create("mdl_ticket", "Neues Ticket")
                .addComponents(ActionRow.of(minecraftNameTextInput), ActionRow.of(logInput))
                .build();
    }
}
