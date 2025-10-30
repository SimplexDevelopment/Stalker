package app.simplexdev.stalker.audio;

import app.simplexdev.stalker.Singletons;
import app.simplexdev.stalker.Stalker;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import dev.omialien.voicechatrecording.configs.RecordingCommonConfig;
import dev.omialien.voicechatrecording.voicechat.audio.AudioPlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class AudioUtils {
    public static EntityAudioChannel createNewChannel(final Entity entity) {
        return Singletons.getServerApi().orElseThrow().createEntityAudioChannel(
                UUID.randomUUID(),
                Singletons.getServerApi().orElseThrow().fromEntity(entity)
        );
    }

    public static AudioPlayer createAudioPlayer(short[] audio, final Entity entity) {
        EntityAudioChannel channel = createNewChannel(entity);
        channel.setCategory(Stalker.MODID);
        channel.setDistance(50.0F);
        channel.setWhispering(false);
        return new AudioPlayer(audio, Singletons.getServerApi().orElseThrow(), channel);
    }

    public static double calculateRMS(short[] audio) {
        int start = 0;
        while (start < audio.length && Math.abs(audio[start]) < RecordingCommonConfig.SILENCE_THRESHOLD.get()) {
            ++start;
        }

        int end = audio.length - 1;
        while (end > start && Math.abs(audio[end]) < RecordingCommonConfig.SILENCE_THRESHOLD.get()) {
            --end;
        }

        int activeSamples = end - start + 1;
        long sumSquares = 0L;

        for (int i = start; i <= end; ++i) {
            int sample = audio[i];
            sumSquares += (long) (sample * sample);
        }

        return Math.sqrt((double) sumSquares / (double) activeSamples);
    }
}
