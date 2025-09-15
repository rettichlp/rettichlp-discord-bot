package de.rettichlp.discordbot.common.configuration;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static de.rettichlp.discordbot.Application.discordBot;
import static java.util.Objects.isNull;

@Getter
@Component
public class DiscordBotProperties {

    @Value("${application-version}")
    private String version;

    @Value("${discord.bot.token}")
    private String token;

    @Value("${discord.guild.id}")
    private String guildId;

    @Value("${discord.guild.category.ticket.id}")
    private String ticketCategoryId;

    public Guild getGuild() {
        Guild guild = discordBot.getGuildById(this.guildId);

        if (isNull(guild)) {
            throw new IllegalStateException("Guild with id " + this.guildId + " not found");
        }

        return guild;
    }

    public Category getTicketCategory() {
        Category category = getGuild().getCategoryById(this.ticketCategoryId);

        if (isNull(category)) {
            throw new IllegalStateException("Category with id " + this.ticketCategoryId + " not found");
        }

        return category;
    }
}
