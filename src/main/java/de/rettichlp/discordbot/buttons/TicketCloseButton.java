package de.rettichlp.discordbot.buttons;

import de.rettichlp.discordbot.common.registry.ButtonBase;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static de.rettichlp.discordbot.Application.discordBotProperties;
import static net.dv8tion.jda.api.components.buttons.Button.danger;
import static net.dv8tion.jda.api.components.buttons.Button.secondary;

@Component
public class TicketCloseButton extends ButtonBase {

    public TicketCloseButton() {
        super("btn_ticket_close");
    }

    @Override
    public void onButtonClick(@NotNull ButtonInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();

        if (Objects.equals(textChannel.getParentCategory(), discordBotProperties.getTicketCategory()) && textChannel.getName().startsWith("ticket-")) {
            event.reply("Möchtest Du das Ticket wirklich schließen?")
                    .addComponents(ActionRow.of(danger("btn_ticket_close_confirm", "Bestätigen"), secondary("btn_ticket_close_abort", "Abbrechen")))
                    .queue();
        }
    }
}
