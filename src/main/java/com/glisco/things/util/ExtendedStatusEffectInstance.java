package com.glisco.things.util;

import net.minecraft.entity.LivingEntity;

public interface ExtendedStatusEffectInstance {
    void things$setAttachedEntity(LivingEntity entity);

    LivingEntity things$getAttachedEntity();
}
