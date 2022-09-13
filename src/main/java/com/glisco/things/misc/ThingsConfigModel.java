package com.glisco.things.misc;

import io.wispforest.owo.config.annotation.*;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Modmenu(modId = "things")
@Config(name = "things", wrapperName = "ThingsConfig")
public class ThingsConfigModel {

    @ConfigEntry.Gui.RequiresRestart
    @Comment("Whether gleaming ore should generate. Unless you plan on making custom recipes, turning this off is a bad idea")
    public boolean generateGleamingOre = true;

    @ConfigEntry.Gui.RequiresRestart
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

    @Comment("Only disables trinket rendering for agglomerations")
    public boolean renderAgglomerationTrinket = true;
  
    @Comment("Whether container blocks locked by a Container Key should be unbreakable")
    public boolean makeLockedContainersUnbreakable = false;

    public boolean enableAgglomeration = true;

    public boolean enableAgglomerationInvScrollSelection = true;

    @Comment("How many ender pearls the displacement tome uses per teleport")
    @RangeConstraint(min = 1, max = 128)
    public int displacementTomeFuelConsumption = 1;

    @Comment("How much walking speed the socks should add per level, base is 0.1, default is 0.02")
    public float sockPerLevelSpeedAmplifier = .02f;

    @Nest
    @Expanded
    public EffectLevels effectLevels = new EffectLevels();

    public static class EffectLevels {
        @RangeConstraint(min = 1, max = 16)
        public int mossNecklaceRegen = 2;

        @RangeConstraint(min = 1, max = 16)
        public int miningGloveMomentum = 2;

        @RangeConstraint(min = 1, max = 16)
        public int riotGauntletStrength = 1;
    }
}
