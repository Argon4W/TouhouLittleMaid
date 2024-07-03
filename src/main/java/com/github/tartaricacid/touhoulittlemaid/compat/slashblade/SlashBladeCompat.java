package com.github.tartaricacid.touhoulittlemaid.compat.slashblade;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskAttack;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

public class SlashBladeCompat {
    private static final String SLASH_BLADE = "slashblade";

    public static boolean isSlashBladeLoaded() {
        return ModList.get().isLoaded(SLASH_BLADE);
    }

    public static boolean isSlashBladeItem(ItemStack stack) {
        return isSlashBladeLoaded() && stack.getItem() instanceof ItemSlashBlade;
    }

    public static void swingSlashBlade(EntityMaid maid, ItemStack itemInHand) {
        if (SlashBladeCompat.isSlashBladeItem(itemInHand) && maid.getTask().getUid().equals(TaskAttack.UID)) {
            int roll = maid.getRandom().nextInt(60) - 30;
            AttackManager.doSlash(maid, roll, Vec3.ZERO, false, false, 1.0, KnockBacks.smash);
            itemInHand.getCapability(CapabilitySlashBlade.BLADESTATE).ifPresent(bladeState -> bladeState.setLastActionTime(maid.level.getGameTime()));
        }
    }
}
