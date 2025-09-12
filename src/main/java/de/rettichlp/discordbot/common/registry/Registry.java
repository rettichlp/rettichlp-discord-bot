package de.rettichlp.discordbot.common.registry;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;

import static de.rettichlp.discordbot.Application.discordBot;
import static org.atteo.classindex.ClassIndex.getAnnotated;

@Log4j2
public class Registry {

    public void registerListeners() {
        getAnnotated(EventListener.class).forEach(listenerClass -> {
            try {
                EventListener annotation = listenerClass.getAnnotation(EventListener.class);
                if (annotation.skipped()) {
                    skippedRegistrations.getAndIncrement();
                    return;
                }

                ListenerAdapter listenerInstance = (ListenerAdapter) listenerClass.getConstructor().newInstance();
                discordBot.addEventListener(listenerInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("Failed to register listener: {}", listenerClass.getName(), e);
            }
        });
    }
}
