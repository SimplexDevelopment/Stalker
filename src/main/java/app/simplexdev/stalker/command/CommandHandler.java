package app.simplexdev.stalker.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

public class CommandHandler {
    public CommandHandler() {}

    public <T extends CommandBase> void register(final @NotNull T commandClass, CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!commandClass.getClass().isAnnotationPresent(Commander.class)) {
            throw new IllegalArgumentException("Class " + commandClass.getClass().getName() + " is not annotated with @Commander");
        }

        Commander cmd = commandClass.getClass().getAnnotation(Commander.class);

        dispatcher.register(
                Commands.literal(cmd.literal())
                        .requires(src -> src.hasPermission(cmd.permissionLevel()))
                        .executes(commandClass::run));
    }
}
