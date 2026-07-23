package de.rettichlp.therettingtongardener.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jspecify.annotations.NonNull;

@Getter
@AllArgsConstructor
public enum TicketCategory {

    THE_RETTINGTON_COMPANION("The Rettington Companion", "the-rettington-companion", "Eine Mod mit nützlichen Hilfsmitteln für einen besseren Minecraft Alltag.", Emoji.fromFormatted("<:therettingtonicon:1501507423888736426>")),
    THE_RETTINGTON_AESTHETICS("The Rettington Aesthetics", "the-rettington-aesthetics", "Ein Minecraft-Ressourcenpaket, das Elemente von BetterVanillaBuilding und Excalibur kombiniert.", Emoji.fromFormatted("<:therettingtonicon:1501507423888736426>")),
    THE_RETTINGTON_CONCIERGE("The Rettington Concierge", "the-rettington-concierge", "Eine Java Bibliothek für Minecraft Plugins.", Emoji.fromFormatted("<:java:1501512844137599086>")),
    UCUTILS("UCUtils", "ucutils", "Eine Mod für den UnicaCity-Rollenspielserver mit nützliche Befehlen und Funktionen.", Emoji.fromFormatted("<:unicacity:1501509954555744276>"));

    private final String label;
    private final String value;
    private final String description;
    private final Emoji emoji;
    //private final Role secretaryRole;

    public @NonNull SelectOption getSelectOption() {
        return SelectOption.of(this.label, this.value)
                .withDescription(this.description)
                .withEmoji(this.emoji);
    }

    public static TicketCategory fromValue(String value) {
        for (TicketCategory category : TicketCategory.values()) {
            if (category.getValue().equals(value)) {
                return category;
            }
        }

        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
