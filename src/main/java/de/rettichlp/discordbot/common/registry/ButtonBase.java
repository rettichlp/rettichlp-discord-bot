package de.rettichlp.discordbot.common.registry;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class ButtonBase extends ListenerAdapter {

    private final String name;

    public ButtonBase(String name) {
        this.name = name;
    }

    public abstract void onButtonClick(ButtonInteractionEvent event);

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if (componentId.equalsIgnoreCase(this.name)) {
            onButtonClick(event);
        }
    }
}
