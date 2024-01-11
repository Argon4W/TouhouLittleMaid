package com.github.tartaricacid.touhoulittlemaid.event.maid;

import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class DismountMaidEvent {
    @SubscribeEvent
    public static void onInteract(InteractMaidEvent event) {
        PlayerEntity player = event.getPlayer();
        EntityMaid maid = event.getMaid();

        if (player.isShiftKeyDown() && player.getMainHandItem().getItem() != InitItems.KAPPA_COMPASS.get()) {
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
