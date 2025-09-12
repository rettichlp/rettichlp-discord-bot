package de.rettichlp.discordbot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static java.util.Objects.nonNull;
import static net.dv8tion.jda.api.Permission.MANAGE_CHANNEL;
import static net.dv8tion.jda.api.Permission.MANAGE_PERMISSIONS;
import static net.dv8tion.jda.api.Permission.MODERATE_MEMBERS;
import static net.dv8tion.jda.api.Permission.VIEW_CHANNEL;
import static net.dv8tion.jda.api.Permission.VOICE_CONNECT;
import static net.dv8tion.jda.api.Permission.VOICE_MOVE_OTHERS;

public class GuildVoiceUpdateListener extends ListenerAdapter {

    private static final String TEMPORARY_VOICE_CHANNEL_CATEGORY_ID = "1415968694743597119";
    private static final String TEMPORARY_VOICE_CHANNEL_START_ID = "1415973808652947477";
    private static final String TEMPORARY_VOICE_CHANNEL_MOVE_ID = "1415973928131887114";
    private static final List<String> VOICE_CHANNEL_NAMES = List.of(
            "Salon Privé",
            "Gesellschaftszimmer",
            "Konferenzraum",
            "Herrenzimmer",
            "Damenzimmer",
            "Kaminzimmer",
            "Bibliothek",
            "Besprechungsraum",
            "Chambre Privée",
            "Gesprächszimmer",
            "Séparée",
            "Sitzungssaal",
            "Gesellschaftsraum",
            "Clubraum",
            "Stube",
            "Diskretionsraum",
            "Empfangsraum",
            "Gesprächssalon",
            "Refugium",
            "Konversationszimmer",
            "Lounge",
            "Boudoir"
    );

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
        Guild guild = e.getGuild();

        AudioChannelUnion channelJoined = e.getChannelJoined();
        if (nonNull(channelJoined) && channelJoined.getId().equals(TEMPORARY_VOICE_CHANNEL_START_ID)) {
            Category parentCategory = channelJoined.getParentCategory();
            assert parentCategory != null;

            Member entity = e.getEntity();
            parentCategory.createVoiceChannel(randomVoiceChannelName())
                    .addPermissionOverride(guild.getPublicRole(), EnumSet.of(VIEW_CHANNEL), EnumSet.of(VOICE_CONNECT))
                    .addPermissionOverride(entity, EnumSet.of(MANAGE_CHANNEL, MANAGE_PERMISSIONS, MODERATE_MEMBERS, VOICE_MOVE_OTHERS), null)
                    .queue(voiceChannel -> {
                        guild.moveVoiceMember(entity, voiceChannel).queue();
                        voiceChannel.modifyStatus("von " + entity.getEffectiveName()).queue();
                    });
        }

        Category temporaryVoiceChannelCategory = guild.getCategoryById(TEMPORARY_VOICE_CHANNEL_CATEGORY_ID);
        assert temporaryVoiceChannelCategory != null;
        temporaryVoiceChannelCategory.getVoiceChannels().stream()
                .filter(voiceChannel -> !voiceChannel.getId().equals(TEMPORARY_VOICE_CHANNEL_START_ID) && !voiceChannel.getId().equals(TEMPORARY_VOICE_CHANNEL_MOVE_ID))
                .filter(voiceChannel -> voiceChannel.getMembers().isEmpty())
                .forEach(voiceChannel -> voiceChannel.delete().queue());
    }

    private String randomVoiceChannelName() {
        return VOICE_CHANNEL_NAMES.get(new Random().nextInt(VOICE_CHANNEL_NAMES.size()));
    }
}
