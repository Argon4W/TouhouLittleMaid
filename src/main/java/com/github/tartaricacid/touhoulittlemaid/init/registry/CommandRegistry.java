package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.command.RootCommand;
import com.github.tartaricacid.touhoulittlemaid.command.arguments.HandleTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class CommandRegistry {
    @SubscribeEvent
    public static void onServerStaring(RegisterCommandsEvent event) {
        RootCommand.register(event.getDispatcher());
    }

    public static void registerArgumentTypes() {
        ArgumentTypes.register("touhou_little_maid:handle_types", HandleTypeArgument.class, new EmptyArgumentSerializer<>(HandleTypeArgument::type));
    }
}
