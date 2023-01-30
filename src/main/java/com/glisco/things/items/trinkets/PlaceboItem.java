package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import io.wispforest.owo.itemgroup.OwoItemSettings;

public class PlaceboItem extends TrinketItemWithOptionalTooltip {

    public PlaceboItem() {
        super(new OwoItemSettings().maxCount(1).group(Things.THINGS_GROUP));
    }
}
