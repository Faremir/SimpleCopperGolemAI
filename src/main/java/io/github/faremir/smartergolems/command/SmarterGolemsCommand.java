package io.github.faremir.smartergolems.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.faremir.smartergolems.config.SmarterGolemsConfigManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public final class SmarterGolemsCommand {

    private SmarterGolemsCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("smartergolems")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                    .then(Commands.literal("reload").executes(SmarterGolemsCommand::reload));
            dispatcher.register(command);
        });
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        SmarterGolemsConfigManager.IOResult result = SmarterGolemsConfigManager.reload();

        if (result == SmarterGolemsConfigManager.IOResult.SUCCESS) {
            context.getSource().sendSuccess(() -> Component.translatable("smarter-golems.commands.reload.success"), false);
            return 1;
        }

        context.getSource().sendFailure(Component.translatable("smarter-golems.commands.reload.failure", result.name()));

        return 0;
    }
}