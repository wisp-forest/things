package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import com.glisco.things.client.ThingsClient;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class EnderPouchItem extends TrinketItemWithOptionalTooltip {

    public EnderPouchItem() {
        super(new Item.Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText(String.format("§7Press §6%s §7to open Ender Chest Inventory", I18n.translate(KeyBindingHelper.getBoundKeyOf(ThingsClient.openEChest).getTranslationKey()))));
    }
}
