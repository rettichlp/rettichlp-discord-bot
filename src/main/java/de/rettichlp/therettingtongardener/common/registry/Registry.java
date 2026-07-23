package de.rettichlp.therettingtongardener.common.registry;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static de.rettichlp.therettingtongardener.Application.discordBot;
import static java.lang.Class.forName;
import static java.util.regex.Pattern.compile;

@Log4j2
public class Registry {

    private final ApplicationContext applicationContext;
    private final ClassPathScanningCandidateComponentProvider scanner;

    public Registry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scanner = new ClassPathScanningCandidateComponentProvider(false);
        this.scanner.addIncludeFilter(new RegexPatternTypeFilter(compile(".*Command$")));
        this.scanner.addIncludeFilter(new RegexPatternTypeFilter(compile(".*Listener$")));
        this.scanner.addIncludeFilter(new RegexPatternTypeFilter(compile(".*Button$")));
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
                    if (commandClass.isAnnotationPresent(Ignore.class)) {
                        skippedRegistrations.getAndIncrement();
                        return;
                    }

                    CommandBase commandInstance = (CommandBase) applicationContext.getBean(commandClass);
                    discordBot.addEventListener(commandInstance);
                    successfulRegistrations.getAndIncrement();
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
                    if (listenerClass.isAnnotationPresent(Ignore.class)) {
                        skippedRegistrations.getAndIncrement();
                        return;
                    }

                    ListenerAdapter listenerInstance = (ListenerAdapter) applicationContext.getBean(listenerClass);
                    discordBot.addEventListener(listenerInstance);
                    successfulRegistrations.getAndIncrement();
                });

        log.info("Registered {}/{} event listeners ({} skipped)", successfulRegistrations.get(), listenerClassNames.size(), skippedRegistrations.get());
    }

    public void registerButtons() {
        AtomicInteger successfulRegistrations = new AtomicInteger();
        AtomicInteger skippedRegistrations = new AtomicInteger();

        List<String> buttonClassNames = this.scanner.findCandidateComponents("de.rettichlp.discordbot.buttons").stream()
                .map(BeanDefinition::getBeanClassName)
                .toList();

        buttonClassNames.stream()
                .map(className -> {
                    try {
                        return forName(className);
                    } catch (ClassNotFoundException e) {
                        log.error("Failed to load button class: {}", className, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(buttonClass -> {
                    if (buttonClass.isAnnotationPresent(Ignore.class)) {
                        skippedRegistrations.getAndIncrement();
                        return;
                    }

                    ButtonBase buttonInstance = (ButtonBase) applicationContext.getBean(buttonClass);
                    discordBot.addEventListener(buttonInstance);
                    successfulRegistrations.getAndIncrement();
                });

        log.info("Registered {}/{} button ({} skipped)", successfulRegistrations.get(), buttonClassNames.size(), skippedRegistrations.get());
    }
}
