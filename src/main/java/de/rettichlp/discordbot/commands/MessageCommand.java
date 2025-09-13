package de.rettichlp.discordbot.commands;

import de.rettichlp.discordbot.common.registry.Command;
import de.rettichlp.discordbot.common.registry.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static java.util.Objects.requireNonNull;
import static net.dv8tion.jda.api.components.buttons.Button.success;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;

@Command(label = "nachricht")
public class MessageCommand extends CommandBase {

    public MessageCommand(String label) {
        super(label);
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        TextChannel textChannel = event.getChannel().asTextChannel();
        switch (requireNonNull(event.getSubcommandName())) {
            case "ticket" -> {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(new Color(0x609fee))
                        .setTitle("Ticket")
                        .addField("🎫 **Hier kannst du ein Ticket erstellen um schnell Hilfe zu erhalten oder sonstige Fragen zu klären.**", "Bei der Erstellung eines Tickets wirst du nach deinem Minecraft Namen und Anliegen gefragt.", false);

                textChannel
                        .sendMessageEmbeds(embedBuilder.build())
                        .addComponents(ActionRow.of(success("btn_ticket_create", "Neues Ticket").withEmoji(fromUnicode("U+1F3AB"))))
                        .queue();
            }
        }

        event.getHook().deleteOriginal().queue();
    }
}
