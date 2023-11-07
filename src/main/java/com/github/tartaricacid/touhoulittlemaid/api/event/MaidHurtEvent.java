package com.github.tartaricacid.touhoulittlemaid.api.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class MaidHurtEvent extends LivingEvent {
    private final EntityMaid maid;
    private final DamageSource source;
    private float amount;

    public MaidHurtEvent(EntityMaid maid, DamageSource source, float amount) {
        super(maid);
        this.maid = maid;
        this.source = source;
        this.amount = amount;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
