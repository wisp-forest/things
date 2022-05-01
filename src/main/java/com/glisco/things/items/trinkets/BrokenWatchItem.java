package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;

public class BrokenWatchItem extends TrinketItemWithOptionalTooltip {
    public BrokenWatchItem() {
        super(new Settings().group(Things.THINGS_GROUP).maxCount(1));
    }
}
