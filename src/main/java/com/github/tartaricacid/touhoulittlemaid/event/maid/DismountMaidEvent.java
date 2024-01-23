package com.github.tartaricacid.touhoulittlemaid.event.maid;

import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class DismountMaidEvent {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onInteract(InteractMaidEvent event) {
        Player player = event.getPlayer();
        EntityMaid maid = event.getMaid();

        if (player.isShiftKeyDown() && !player.getMainHandItem().is(InitItems.KAPPA_COMPASS.get())) {
            if (maid.getVehicle() == null && maid.getPassengers().isEmpty()) {
                return;
            }
            if (maid.getVehicle() != null) {
                maid.stopRiding();
            }
            if (!maid.getPassengers().isEmpty()) {
                maid.ejectPassengers();
            }
            event.setCanceled(true);
        }
    }
}
