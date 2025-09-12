package de.rettichlp.discordbot.common.registry;

import org.atteo.classindex.IndexAnnotated;

@IndexAnnotated
public @interface EventListener {

    boolean skipped() default false;
}
