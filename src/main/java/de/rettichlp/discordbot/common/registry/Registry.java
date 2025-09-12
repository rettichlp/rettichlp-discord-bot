package de.rettichlp.discordbot.common.registry;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import static de.rettichlp.discordbot.Application.discordBot;
import static java.util.stream.StreamSupport.stream;
import static org.atteo.classindex.ClassIndex.getAnnotated;

@Log4j2
public class Registry {

    public void registerListeners() {
        AtomicInteger successfulRegistrations = new AtomicInteger();
        AtomicInteger skippedRegistrations = new AtomicInteger();

        Iterable<Class<?>> listenerClasses = getAnnotated(EventListener.class);
        listenerClasses.forEach(listenerClass -> {
            try {
                EventListener annotation = listenerClass.getAnnotation(EventListener.class);
                if (annotation.skipped()) {
                    skippedRegistrations.getAndIncrement();
                    return;
                }

                ListenerAdapter listenerInstance = (ListenerAdapter) listenerClass.getConstructor().newInstance();
                discordBot.addEventListener(listenerInstance);
                successfulRegistrations.getAndIncrement();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("Failed to register listener: {}", listenerClass.getName(), e);
            }
        });

        log.info("Registered {}/{} event listeners ({} skipped)", successfulRegistrations.get(), stream(listenerClasses.spliterator(), false).count(), skippedRegistrations.get());
    }
}
