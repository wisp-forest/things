package com.glisco.things.misc;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluids;

@SuppressWarnings("UnstableApiUsage")
public class BaterWucketStorage implements ExtractionOnlyStorage<FluidVariant>, SingleSlotStorage<FluidVariant> {
    private static final FluidVariant WATER = FluidVariant.of(Fluids.WATER);

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        if (resource.equals(WATER)) {
            return Math.min(maxAmount, FluidConstants.BUCKET);
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return false;
    }

    @Override
    public FluidVariant getResource() {
        return WATER;
    }

    @Override
    public long getAmount() {
        return FluidConstants.BUCKET;
    }

    @Override
    public long getCapacity() {
        return FluidConstants.BUCKET;
    }
}
