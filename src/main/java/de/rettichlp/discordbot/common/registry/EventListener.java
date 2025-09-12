package de.rettichlp.discordbot.common.registry;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@IndexAnnotated
public @interface EventListener {

    boolean skipped() default false;
}
