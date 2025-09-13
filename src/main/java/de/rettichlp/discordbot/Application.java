package de.rettichlp.discordbot;

import de.rettichlp.discordbot.common.configuration.DiscordBotProperties;
import de.rettichlp.discordbot.common.registry.Registry;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.utils.Compression.NONE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.MEMBER_OVERRIDES;
import static org.springframework.boot.SpringApplication.run;

@Log4j2
@SpringBootApplication
public class Application {

    public static JDA discordBot;
    public static DiscordBotProperties discordBotProperties;

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = run(Application.class, args);

        discordBotProperties = context.getBean(DiscordBotProperties.class);

        long discordBotStartTime = currentTimeMillis();
        log.info("Discord bot starting");
        startDiscordBot();

        getRuntime().addShutdownHook(new Thread(() -> ofNullable(discordBot).ifPresent(JDA::shutdown)));

        log.info("Discord bot started in {}ms", currentTimeMillis() - discordBotStartTime);
    }

    private static void startDiscordBot() throws InterruptedException {
        discordBot = JDABuilder
                .createDefault(discordBotProperties.getToken())
                .disableCache(MEMBER_OVERRIDES) // Disable parts of the cache
                .setBulkDeleteSplittingEnabled(false) // Enable the bulk delete event
                .setCompression(NONE) // Disable compression (not recommended)
                .enableIntents(MESSAGE_CONTENT)
                .enableIntents(GUILD_MEMBERS)
                .enableIntents(GUILD_MESSAGES)
                .enableIntents(GUILD_VOICE_STATES)
                .build().awaitReady();

        Registry registry = new Registry();
        registry.registerCommands();
        registry.registerListeners();
        registry.registerButtons();

        discordBot.getGuilds().forEach(guild -> guild.updateCommands().addCommands(
                slash("version", "Zeigt die aktuelle Version des Discord Bots an")
        ).queue());
    }
}
