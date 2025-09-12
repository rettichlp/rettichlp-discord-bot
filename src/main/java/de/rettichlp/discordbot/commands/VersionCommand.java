package de.rettichlp.discordbot.commands;

import de.rettichlp.discordbot.common.registry.Command;
import de.rettichlp.discordbot.common.registry.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.discordbot.Application.discordBot;
import static de.rettichlp.discordbot.Application.discordBotProperties;

@Command(label = "version")
public class VersionCommand extends CommandBase {

    public VersionCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(@NotNull SlashCommandInteractionEvent event) {
        User user = discordBot.retrieveUserById("278520516569071616").complete();
        SelfUser botUser = discordBot.getSelfUser();

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setTitle("Discord Bot by " + user.getEffectiveName(), "https://i.redd.it/gc2m1tdq22w81.jpg")
                .addField("Version", discordBotProperties.getVersion() + " [Changelog ↗](https://github.com/rettichlp/rettichlp-discord-bot/releases/latest)", false)
                .addField("GitHub", "https://github.com/rettichlp/rettichlp-discord-bot", false)
                .setAuthor(botUser.getName(), null, botUser.getAvatarUrl())
                .build();

        event.replyEmbeds(messageEmbed).setEphemeral(true).queue();
    }
}
