package de.rettichlp.discordbot.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;

import static net.dv8tion.jda.api.components.buttons.Button.secondary;
import static net.dv8tion.jda.api.components.buttons.Button.success;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;

@Getter
@AllArgsConstructor
public enum TicketCategory {

    GENERALLY("btn_ticket_create", "Allgemeines Ticket", fromUnicode("U+1F3AB")),
    PKUTILS("btn_ticket_create_pkutils", "Ticket (PKUtils)", fromUnicode("U+1F9E9")),
    RLPTP("btn_ticket_create_rlptp", "Ticket (RLPTP)", fromUnicode("U+1F5BC"));

    private final String buttonId;
    private final String buttonLabel;
    private final Emoji emoji;

    @NotNull
    public Button getTicketCreateButton() {
        return this == GENERALLY
                ? success(this.buttonId, this.buttonLabel).withEmoji(this.emoji)
                : secondary(this.buttonId, this.buttonLabel).withEmoji(this.emoji);
    }
}
