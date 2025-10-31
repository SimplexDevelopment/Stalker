package app.simplexdev.stalker;

import app.simplexdev.stalker.audio.AudioStorage;
import app.simplexdev.stalker.command.CommandHandler;
import app.simplexdev.stalker.command.playRandomVoiceFile;
import de.maxhenkel.voicechat.plugins.impl.VoicechatServerApiImpl;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.events.AudioLoadedEvent;
import dev.omialien.voicechatrecording.api.events.AudioRecordedEvent;
import dev.omialien.voicechatrecording.api.events.RecordingSetupEvent;
import dev.omialien.voicechatrecording.taskscheduler.TaskScheduler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Stalker.MODID)
public class CommonEventBus {

    @SubscribeEvent
    public static void onRecordingApiInitialized(RecordingSetupEvent event) {
        Singletons.API = event.getApi();
        Singletons.SRV_API = VoicechatServerApiImpl.instance();
        Singletons.AUDIOS = new AudioStorage();
        Singletons.SCHEDULER = new TaskScheduler();
        event.addCategory(Stalker.MODID, "Stalker", "Stalker Related Audio Stuff", null);

        Singletons.getRecordingApi().ifPresent(api ->
                api.loadNamespaceAudios(Stalker.MODID, (audio) ->
                        Singletons.getAudioStorage().ifPresent(storage -> {
            storage.addAudio(audio);
            Stalker.logger().info("Loaded audio from storage: {}", audio.getId());
        })));
    }

    @SubscribeEvent
    public static void onAudioRecordedEvent(AudioRecordedEvent event){
        if(event.getAudio().getFilterResult() == IRecordedAudio.FilterResult.PASSED){
            if (Singletons.getAudioStorage().orElseThrow().getTotalAudioCount() >= 256){
                for (int i = 0; i <= Singletons.getAudioStorage().orElseThrow().getTotalAudioCount() - 255; i++) {
                    Singletons.getAudioStorage().orElseThrow().removeRandomAudio();
                }
            }
            Singletons.getAudioStorage().orElseThrow().addAudio(event.getAudio());
        }
    }

    @SubscribeEvent
    public static void onAudioLoadedEvent(AudioLoadedEvent event){
        if(Singletons.getAudioStorage().orElseThrow().getTotalAudioCount() < 256 &&
                event.getLoadReason() == AudioLoadedEvent.LoadType.NAMESPACE &&
                event.getNamespace().equals(Stalker.MODID)) {
            Singletons.getAudioStorage().orElseThrow().addAudio(event.getAudio());
        }
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
        Singletons.getAudioStorage().orElseThrow().savePlayerAudios(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event){
        Singletons.getAudioStorage().orElseThrow().loadPlayerAudios(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandHandler handler = new CommandHandler();
        handler.register(new playRandomVoiceFile(), event.getDispatcher());
    }
}
