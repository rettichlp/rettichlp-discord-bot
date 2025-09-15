package de.rettichlp.discordbot.buttons;

import de.rettichlp.discordbot.common.registry.Button;
import de.rettichlp.discordbot.common.registry.ButtonBase;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.discordbot.Application.discordBotProperties;

@Button(label = "btn_ticket_close_confirm")
public class TicketCloseConfirmButton extends ButtonBase {

    public TicketCloseConfirmButton(String label) {
        super(label);
    }

    @Override
    public void onButtonClick(@NotNull ButtonInteractionEvent event) {
        String userName = event.getChannel().getName().split("-")[1];

        discordBotProperties.getTicketCategory().getChannels().stream()
                .filter(guildChannel -> guildChannel.getName().startsWith("ticket-") && guildChannel.getName().contains(userName))
                .forEach(guildChannel -> guildChannel.delete().queue());
    }
}
