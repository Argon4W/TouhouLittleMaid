package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.compat.patchouli.MultiblockRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CompatRegistry {
    private static final String TOP = "theoneprobe";
    private static final String PATCHOULI = "patchouli";

    @SubscribeEvent
    public static void onEnqueue(final InterModEnqueueEvent event) {
        // event.enqueueWork(() -> InterModComms.sendTo(TOP, "getTheOneProbe", TheOneProbeInfo::new));
        event.enqueueWork(() -> checkModLoad(PATCHOULI, MultiblockRegistry::init));
    }

    private static void checkModLoad(String modid, Runnable runnable) {
        if (ModList.get().isLoaded(modid)) {
            runnable.run();
        }
    }
}
