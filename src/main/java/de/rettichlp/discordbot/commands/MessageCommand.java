package de.rettichlp.discordbot.commands;

import de.rettichlp.discordbot.common.registry.CommandBase;
import de.rettichlp.discordbot.common.services.TicketService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class MessageCommand extends CommandBase {

    private final TicketService ticketService;

    @Autowired
    public MessageCommand(TicketService ticketService) {
        super("nachricht");
        this.ticketService = ticketService;
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        TextChannel textChannel = event.getChannel().asTextChannel();
        switch (requireNonNull(event.getSubcommandName())) {
            case "ticket" -> textChannel.sendMessageComponents(this.ticketService.getTicketCreateMessage())
                    .useComponentsV2()
                    .queue();
        }

        event.getHook().deleteOriginal().queue();
    }
}
