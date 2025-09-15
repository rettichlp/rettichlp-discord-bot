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
}
