package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;

public class PlaceboItem extends TrinketItemWithOptionalTooltip {

    public PlaceboItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }
}
