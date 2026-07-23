package de.rettichlp.therettingtongardener.common.services;

import de.rettichlp.therettingtongardener.common.models.TicketCategory;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static de.rettichlp.therettingtongardener.Application.discordBotProperties;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.components.buttons.Button.primary;
import static net.dv8tion.jda.api.components.buttons.Button.success;
import static net.dv8tion.jda.api.components.separator.Separator.Spacing.SMALL;
import static net.dv8tion.jda.api.components.separator.Separator.createDivider;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;

@Log4j2
@Service
public class TicketService {

    public static final String SSM_TICKET_CATEGORY_CUSTOM_ID = "ticket_category";

    private static final String TICKET_CHANNEL_ID = "1416563386832912404";
    private static final String TICKET_MESSAGE_ID = "1501614155180937287";

    public TicketService() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updateTicketCreateMessage();
            }
        }, 5000);
    }

    public void createTicket(@NonNull GenericComponentInteractionCreateEvent event, @Nullable TicketCategory ticketCategory) {
        Member member = event.getMember();
        assert member != null;

        boolean hasTicketChannel = discordBotProperties.getTicketCategory().getChannels().stream()
                .map(guildChannel -> discordBotProperties.getGuild().getTextChannelById(guildChannel.getId()))
                .filter(Objects::nonNull)
                .map(StandardGuildMessageChannel::getTopic)
                .filter(Objects::nonNull)
                .anyMatch(s -> s.contains(member.getId()));

        if (hasTicketChannel) {
            event.reply("Du hast bereits ein Ticket!").setEphemeral(true).queue();
            return;
        }

        String userName = member.getUser().getName();
        String memberId = member.getId();

        ChannelAction<TextChannel> textChannelChannelAction = discordBotProperties.getTicketCategory().createTextChannel("ticket-" + userName)
                .setTopic("Ticket von " + userName + " (" + memberId + ")")
                .addPermissionOverride(discordBotProperties.getGuild().getPublicRole(), null, EnumSet.of(VIEW_CHANNEL))
                .addPermissionOverride(member, EnumSet.of(VIEW_CHANNEL), null);

        textChannelChannelAction.queue(textChannel -> textChannel
                .sendMessage("Hey " + member.getAsMention() + "! Danke, dass du ein Ticket erstellt hast. Das Ticket wird schnellstmöglich bearbeitet.\n" +
                        "Kategorie: " + ofNullable(ticketCategory).map(TicketCategory::getLabel).orElse("Ohne"))
                .addComponents(ActionRow.of(success("btn_ticket_close", "Ticket schließen").withEmoji(fromUnicode("U+1F512"))))
                .queue(message -> event.reply("Du hast ein Ticket erstellt: " + message.getJumpUrl()).setEphemeral(true).queue()));
    }

    public @NonNull MessageTopLevelComponent getTicketCreateMessage() {
        return Container.of(
                TextDisplay.of("## 🎫 Neues Ticket"),
                TextDisplay.of("Hier kannst du ein Ticket erstellen um Hilfe zu erhalten oder sonstige Fragen zu klären."),
                createDivider(SMALL),
                TextDisplay.of("**Mit Kategorie**"),
                TextDisplay.of("Sollte es bei deinem Anliegen um eine bestimmte Anwendung gehen, erstelle bitte ein Ticket mit dieser Kategorie."),
                ActionRow.of(getStringSelectMenu()),
                createDivider(SMALL),
                TextDisplay.of("**Ohne Kategorie**"),
                TextDisplay.of("Wenn die Kategorie nicht aufgeführt ist, kannst du auch ohne Kategorie fortfahren."),
                ActionRow.of(primary("ticket_create_no_category", "Ticket erstellen"))
        );
    }

    private @NonNull StringSelectMenu getStringSelectMenu() {
        return StringSelectMenu.create(SSM_TICKET_CATEGORY_CUSTOM_ID)
                .setPlaceholder("Wähle eine Kategorie...")
                .addOptions(stream(TicketCategory.values())
                        .map(TicketCategory::getSelectOption)
                        .toList())
                .build();
    }

    private void updateTicketCreateMessage() {
        TextChannel ticketChannel = discordBotProperties.getGuild().getTextChannelById(TICKET_CHANNEL_ID);

        if (ticketChannel == null) {
            log.warn("Ticket channel not found! Skipping update of ticket create message.");
            return;
        }

        ticketChannel.retrieveMessageById(TICKET_MESSAGE_ID).queue(message -> {
            MessageTopLevelComponent ticketCreateMessage = getTicketCreateMessage();
            message.editMessageComponents(ticketCreateMessage)
                    .useComponentsV2()
                    .queue();
        });
    }
}
