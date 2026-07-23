package de.rettichlp.therettingtongardener.buttons;

import de.rettichlp.therettingtongardener.common.registry.ButtonBase;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static de.rettichlp.therettingtongardener.Application.discordBotProperties;

@Component
public class TicketCloseConfirmButton extends ButtonBase {

    public TicketCloseConfirmButton() {
        super("btn_ticket_close_confirm");
    }

    @Override
    public void onButtonClick(@NotNull ButtonInteractionEvent event) {
        String userName = event.getChannel().getName().split("-")[1];

        discordBotProperties.getTicketCategory().getChannels().stream()
                .filter(guildChannel -> guildChannel.getName().startsWith("ticket-") && guildChannel.getName().contains(userName))
                .forEach(guildChannel -> guildChannel.delete().queue());
    }
}
