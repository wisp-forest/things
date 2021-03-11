package com.glisco.things.mixin;

import net.minecraft.inventory.ContainerLock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerLock.class)
public interface ContainerLockAccessor {

    @Accessor
    String getKey();
}
