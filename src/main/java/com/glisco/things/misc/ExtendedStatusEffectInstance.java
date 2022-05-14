package com.glisco.things.misc;

import net.minecraft.entity.LivingEntity;

public interface ExtendedStatusEffectInstance {
    void things$setAttachedEntity(LivingEntity entity);

    LivingEntity things$getAttachedEntity();
}
