package app.simplexdev.stalker.command;

import app.simplexdev.stalker.Singletons;
import app.simplexdev.stalker.audio.AudioUtils;
import com.mojang.brigadier.context.CommandContext;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.voicechat.audio.AudioPlayer;
import net.minecraft.commands.CommandSourceStack;

@Commander(literal = "prvf", permissionLevel = 4)
public class playRandomVoiceFile extends CommandBase {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        IRecordedAudio audio = Singletons.getAudioStorage().orElseThrow().getRandomAudio(true);

        source.getServer().getPlayerList().getPlayers().forEach(player -> {
            AudioPlayer ap = AudioUtils.createAudioPlayer(audio.getAudio(), player);
            Singletons.getScheduler().ifPresent(s -> s.scheduleAt(ap, 20L));
        });

        return 1;
    }
}
