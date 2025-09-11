package de.rettichlp.discordbot.common.configuration;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static de.rettichlp.discordbot.Application.discordBot;

@Getter
@Component
public class DiscordBotProperties {

    @Value("${application-version}")
    private String version;

    @Value("${discord.bot.token}")
    private String token;

    @Value("${discord.guild.id}")
    private String guildId;

    @Nullable
    public Guild getGuild() {
        return discordBot.getGuildById(this.guildId);
    }
}
