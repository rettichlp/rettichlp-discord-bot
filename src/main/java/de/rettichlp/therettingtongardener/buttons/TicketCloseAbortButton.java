package de.rettichlp.therettingtongardener.buttons;

import de.rettichlp.therettingtongardener.common.registry.ButtonBase;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TicketCloseAbortButton extends ButtonBase {

    public TicketCloseAbortButton() {
        super("btn_ticket_close_abort");
    }

    @Override
    public void onButtonClick(@NotNull ButtonInteractionEvent event) {
        String messageId = event.getMessage().getId();
        event.getChannel().deleteMessageById(messageId).queue();
    }
}
