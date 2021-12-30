package com.glisco.things;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "things")
public class ThingsConfig implements ConfigData {

    @Comment("Disables trinket support for apples")
    public boolean appleTrinket = true;

    @Comment("How much faster the wax gland should make you")
    public float waxGlandMultiplier = 10f;

    @Comment("The base durability of the infernal scepter")
    public int infernalScepterDurability = 64;

    @Comment("Whether Things should prevent beacons from giving someone haste when they already have momentum")
    public boolean nerfBeaconsWithMomentum = true;

    @Comment("Globally disables Things trinket rendering")
    public boolean renderTrinkets = true;

    @Comment("Only disables trinket rendering for apples")
    public boolean renderAppleTrinket = true;

    @Comment("How many ender pearls the displacement tome uses per teleport")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 128)
    public int displacementTomeFuelConsumption = 1;

    @ConfigEntry.Gui.CollapsibleObject
    public EffectLevels effectLevels = new EffectLevels();

    public static class EffectLevels {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int mossNecklaceRegen = 2;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int miningGloveMomentum = 2;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int riotGauntletStrength = 1;
    }
}
