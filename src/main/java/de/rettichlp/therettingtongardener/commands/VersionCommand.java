package de.rettichlp.therettingtongardener.commands;

import de.rettichlp.therettingtongardener.common.registry.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static de.rettichlp.therettingtongardener.Application.discordBot;
import static de.rettichlp.therettingtongardener.Application.discordBotProperties;

@Component
public class VersionCommand extends CommandBase {

    public VersionCommand() {
        super("version");
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        User user = discordBot.retrieveUserById("278520516569071616").complete();
        SelfUser botUser = discordBot.getSelfUser();

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setTitle("Discord Bot by " + user.getEffectiveName(), "https://i.redd.it/gc2m1tdq22w81.jpg")
                .addField("Version", discordBotProperties.getVersion() + " [Changelog ↗](https://github.com/rettichlp/the-rettington-gardener/releases/latest)", false)
                .addField("GitHub", "https://github.com/rettichlp/the-rettington-gardener", false)
                .setAuthor(botUser.getName(), null, botUser.getAvatarUrl())
                .build();

        event.replyEmbeds(messageEmbed).setEphemeral(true).queue();
    }
}
