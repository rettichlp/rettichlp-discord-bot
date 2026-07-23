package de.rettichlp.therettingtongardener.buttons;

import de.rettichlp.therettingtongardener.common.registry.ButtonBase;
import de.rettichlp.therettingtongardener.common.services.TicketService;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketCreateButton extends ButtonBase {

    private final TicketService ticketService;

    @Autowired
    public TicketCreateButton(TicketService ticketService) {
        super("btn_ticket_create");
        this.ticketService = ticketService;
    }

    @Override
    public void onButtonClick(ButtonInteractionEvent event) {
        ticketService.createTicket(event, null);
    }
}
