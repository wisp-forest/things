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

    @Comment("The momentum level the mining glove produces")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int miningGloveMomentumLevel = 2;

    @Comment("The regeneration level the moss necklace produces")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
    public int mossNecklaceRegenerationLevel = 2;

    @Comment("How many ender pearls the displacement tome uses per teleport")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 999)
    public int displacementTomeFuelConsumption = 1;

    @Comment("Whether Things should prevent beacons from giving someone haste when they already have momentum")
    public boolean nerfBeaconsWithMomentum = true;

    @Comment("Globally disables Things trinket rendering")
    public boolean renderTrinkets = true;

    @Comment("Only disables trinket rendering for apples")
    public boolean renderAppleTrinket = true;

    @Comment("Whether players should receive a Things guide when they first enter a world")
    public boolean giveGuideOnWorldEntry = true;
}
