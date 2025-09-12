package de.rettichlp.discordbot.common.registry;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static de.rettichlp.discordbot.Application.discordBot;
import static java.lang.Class.forName;

@Log4j2
public class Registry {

    private final ClassPathScanningCandidateComponentProvider scanner;

    public Registry() {
        this.scanner = new ClassPathScanningCandidateComponentProvider(false);
        this.scanner.addIncludeFilter(new AnnotationTypeFilter(Command.class));
        this.scanner.addIncludeFilter(new AnnotationTypeFilter(EventListener.class));
    }

    public void registerCommands() {
        AtomicInteger successfulRegistrations = new AtomicInteger();
        AtomicInteger skippedRegistrations = new AtomicInteger();

        List<String> commandClassNames = this.scanner.findCandidateComponents("de.rettichlp.discordbot.commands").stream()
                .map(BeanDefinition::getBeanClassName)
                .toList();

        commandClassNames.stream()
                .map(className -> {
                    try {
                        return forName(className);
                    } catch (ClassNotFoundException e) {
                        log.error("Failed to load command class: {}", className, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(commandClass -> {
                    Command annotation = commandClass.getAnnotation(Command.class);
                    if (annotation.skipped()) {
                        skippedRegistrations.getAndIncrement();
                        return;
                    }

                    try {
                        CommandBase commandInstance = (CommandBase) commandClass.getConstructor(String.class).newInstance(annotation.label());
                        discordBot.addEventListener(commandInstance);
                        successfulRegistrations.getAndIncrement();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("Failed to register command: {}", commandClass.getName(), e);
                    }
                });

        log.info("Registered {}/{} commands ({} skipped)", successfulRegistrations.get(), commandClassNames.size(), skippedRegistrations.get());
    }

    public void registerListeners() {
        AtomicInteger successfulRegistrations = new AtomicInteger();
        AtomicInteger skippedRegistrations = new AtomicInteger();

        List<String> listenerClassNames = this.scanner.findCandidateComponents("de.rettichlp.discordbot.listeners").stream()
                .map(BeanDefinition::getBeanClassName)
                .toList();

        listenerClassNames.stream()
                .map(className -> {
                    try {
                        return forName(className);
                    } catch (ClassNotFoundException e) {
                        log.error("Failed to load listener class: {}", className, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(listenerClass -> {
                    EventListener annotation = listenerClass.getAnnotation(EventListener.class);
                    if (annotation.skipped()) {
                        skippedRegistrations.getAndIncrement();
                        return;
                    }

                    try {
                        ListenerAdapter listenerInstance = (ListenerAdapter) listenerClass.getConstructor().newInstance();
                        discordBot.addEventListener(listenerInstance);
                        successfulRegistrations.getAndIncrement();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("Failed to register listener: {}", listenerClass.getName(), e);
                    }
                });

        log.info("Registered {}/{} event listeners ({} skipped)", successfulRegistrations.get(), listenerClassNames.size(), skippedRegistrations.get());
    }
}
