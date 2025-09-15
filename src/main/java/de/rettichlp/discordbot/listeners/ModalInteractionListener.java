package de.rettichlp.discordbot.listeners;

import de.rettichlp.discordbot.common.models.TicketCategory;
import de.rettichlp.discordbot.common.registry.EventListener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

@EventListener
public class ModalInteractionListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;
        String modalId = event.getModalId();

        // get ticket category by modal id
        Optional<TicketCategory> optionalTicketCategory = stream(TicketCategory.values())
                .filter(ticketCategory -> modalId.equals("mdl_" + ticketCategory.getId()))
                .findFirst();

        optionalTicketCategory.ifPresent(ticketCategory -> {
            String topic = requireNonNull(event.getValue("tip_topic")).getAsString();
            ticketCategory.createTicketChannel(event, member, topic);
        });
    }
}
