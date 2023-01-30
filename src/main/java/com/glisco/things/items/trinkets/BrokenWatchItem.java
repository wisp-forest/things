package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import io.wispforest.owo.itemgroup.OwoItemSettings;

public class BrokenWatchItem extends TrinketItemWithOptionalTooltip {
    public BrokenWatchItem() {
        super(new OwoItemSettings().group(Things.THINGS_GROUP).maxCount(1));
    }
}
