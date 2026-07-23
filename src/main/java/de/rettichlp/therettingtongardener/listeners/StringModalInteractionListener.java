package de.rettichlp.therettingtongardener.listeners;

import de.rettichlp.therettingtongardener.common.services.TicketService;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.rettichlp.therettingtongardener.common.models.TicketCategory.fromValue;

@Component
public class StringModalInteractionListener extends ListenerAdapter {

    private final TicketService ticketService;

    @Autowired
    public StringModalInteractionListener(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        switch (event.getComponentId()) {
            case "ticket_category" -> {
                String selectedValue = event.getValues().getFirst();
                this.ticketService.createTicket(event, fromValue(selectedValue));
            }
        }
    }
}
