package app.simplexdev.stalker.audio;

import app.simplexdev.stalker.Singletons;
import app.simplexdev.stalker.Stalker;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import dev.omialien.voicechatrecording.api.IRecordedPlayer;
import dev.omialien.voicechatrecording.voicechat.VoiceChatRecordingPlugin;

import java.util.UUID;

public class StalkerVoicePlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return Stalker.MODID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    @Override
    public void initialize(VoicechatApi api) {
        Stalker.logger().info("Stalker Voice Chat Plugin initialized");
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        // This fires every time a player sends audio (PTT or voice activation)
        UUID playerUUID = event.getSenderConnection().getPlayer().getUuid();

        // Get or create the IRecordedPlayer for this player
        Singletons.getRecordingApi()
                .map(api -> (VoiceChatRecordingPlugin) api)
                .ifPresent(api -> {
                    IRecordedPlayer recordedPlayer =
                            api.getRecordedPlayer(playerUUID);

                    if (recordedPlayer != null) {
                        // Start recording if not already recording
                        if (!recordedPlayer.isRecording()) {
                            api.startRecording(playerUUID);
                            Stalker.logger().debug("Started recording for player: {}", playerUUID);
                        }
                    }
                });
    }
}