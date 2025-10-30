package app.simplexdev.stalker;

import app.simplexdev.stalker.audio.AudioStorage;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import dev.omialien.voicechatrecording.api.VoiceChatRecordingApi;
import dev.omialien.voicechatrecording.taskscheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Singletons {
    public static @Nullable VoiceChatRecordingApi API;
    public static @Nullable VoicechatServerApi SRV_API;
    public static @Nullable AudioStorage AUDIOS;
    public static @Nullable TaskScheduler SCHEDULER;

    public static @NotNull Optional<VoicechatServerApi> getServerApi() {
        return Optional.ofNullable(SRV_API);
    }

    public static @NotNull Optional<VoiceChatRecordingApi> getRecordingApi() {
        return Optional.ofNullable(API);
    }

    public static @NotNull Optional<AudioStorage> getAudioStorage() {
        return Optional.ofNullable(AUDIOS);
    }

    public static @NotNull Optional<TaskScheduler> getScheduler() {
        return Optional.ofNullable(SCHEDULER);
    }
}
