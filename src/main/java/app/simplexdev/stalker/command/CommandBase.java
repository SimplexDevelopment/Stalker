package app.simplexdev.stalker.command;

import com.mojang.brigadier.context.CommandContext;
import dev.omialien.voicechatrecording.voicechat.audio.AudioPlayer;
import net.minecraft.commands.CommandSourceStack;

public abstract class CommandBase {
    protected abstract int run(CommandContext<CommandSourceStack> context);
}
