package de.rettichlp.discordbot.buttons;

import de.rettichlp.discordbot.common.models.TicketCategory;
import de.rettichlp.discordbot.common.registry.Button;
import de.rettichlp.discordbot.common.registry.ButtonBase;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static de.rettichlp.discordbot.Application.discordBotProperties;
import static de.rettichlp.discordbot.common.models.TicketCategory.RLPTP;
import static java.util.Objects.requireNonNull;

@Button(label = "btn_ticket_create_rlptp")
public class TicketCreateRLPTPButton extends ButtonBase {

    private static final TicketCategory TICKET_CATEGORY = RLPTP;

    public TicketCreateRLPTPButton(String label) {
        super(label);
    }

    @Override
    public void onButtonClick(@NotNull ButtonInteractionEvent event) {
        boolean hasTicketChannel = discordBotProperties.getTicketCategory().getChannels().stream()
                .map(guildChannel -> discordBotProperties.getGuild().getTextChannelById(guildChannel.getId()))
                .filter(Objects::nonNull)
                .map(StandardGuildMessageChannel::getTopic)
                .filter(Objects::nonNull)
                .anyMatch(s -> s.contains(TICKET_CATEGORY.getButtonLabel()) && s.contains(requireNonNull(event.getMember()).getId()));

        if (hasTicketChannel) {
            event.reply("Du hast bereits einen " + TICKET_CATEGORY.getButtonLabel() + " Channel!").setEphemeral(true).queue();
            return;
        }

        event.replyModal(TICKET_CATEGORY.getTicketModal()).queue();
    }
}
