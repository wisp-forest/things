package com.glisco.things.mixin;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.ContainerLock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LockableContainerBlockEntity.class)
public interface LockableContainerBlockEntityAccessor {

    @Accessor
    void setLock(ContainerLock lock);

    @Accessor
    ContainerLock getLock();

}
