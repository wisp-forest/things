package com.glisco.things.compat.rei;

import com.glisco.things.client.DisplacementTomeScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;

import java.util.Collections;

public class ThingsPlugin implements REIClientPlugin {

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(DisplacementTomeScreen.class, screen -> {
            if (!screen.isNameFieldVisible()) return Collections.emptyList();

            int x = screen.getRootX();
            int y = screen.getRootY();

            return Collections.singletonList(new Rectangle(x + 160, y + 50, 130, 40));
        });
    }
}
