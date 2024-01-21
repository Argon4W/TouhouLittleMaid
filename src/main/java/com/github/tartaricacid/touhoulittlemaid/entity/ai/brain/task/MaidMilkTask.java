package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

public class MaidMilkTask extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = 40;
    private final float speedModifier;
    private LivingEntity milkTarget = null;

    public MaidMilkTask(float speedModifier) {
        super(ImmutableMap.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.speedModifier = speedModifier;
        this.setMaxCheckRate(MAX_DELAY_TIME);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld worldIn, EntityMaid owner) {
        if (super.checkExtraStartConditions(worldIn, owner)) {
            CombinedInvWrapper availableInv = owner.getAvailableInv(true);
            return ItemsUtil.isStackIn(availableInv, stack -> stack.getItem() == Items.BUCKET) &&
                    ItemsUtil.isStackIn(availableInv, stack -> stack == ItemStack.EMPTY);
        }
        return false;
    }

    @Override
    protected void start(ServerWorld worldIn, EntityMaid maid, long gameTimeIn) {
        milkTarget = null;
        this.getEntities(maid).stream()
                .filter(e -> maid.isWithinRestriction(e.blockPosition()))
                .filter(Entity::isAlive)
                .filter(e -> e instanceof CowEntity)
                .filter(e -> !e.isBaby())
                .filter(maid::canPathReach)
                .findFirst()
                .ifPresent(e -> {
                    milkTarget = e;
                    BrainUtil.setWalkAndLookTargetMemories(maid, e, this.speedModifier, 0);
                });

        if (milkTarget != null && milkTarget.closerThan(maid, 2)) {
            CombinedInvWrapper availableInv = maid.getAvailableInv(false);
            ItemStack bucket = ItemsUtil.getStack(availableInv, stack -> stack.getItem() == Items.BUCKET);
            if (bucket != ItemStack.EMPTY) {
                bucket.shrink(1);
                ItemHandlerHelper.insertItemStacked(availableInv, new ItemStack(Items.MILK_BUCKET), false);
            }
            maid.swing(Hand.MAIN_HAND);
            maid.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            milkTarget = null;
        }
    }

    private List<LivingEntity> getEntities(EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(Lists.newArrayList());
    }
}
