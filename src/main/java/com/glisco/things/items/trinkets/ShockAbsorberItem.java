package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;

public class ShockAbsorberItem extends TrinketItemWithOptionalTooltip {
    public ShockAbsorberItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }
}
