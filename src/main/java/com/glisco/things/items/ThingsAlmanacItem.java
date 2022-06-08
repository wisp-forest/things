package com.glisco.things.items;

import com.glisco.things.Things;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThingsAlmanacItem extends Item {

    private static final Identifier THINGS_GUIDE = Things.id("things_guide");

    public ThingsAlmanacItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
//        Book book = BookRegistry.INSTANCE.books.get(THINGS_GUIDE);

        if (!world.isClient()) {
            player.sendMessage(Text.literal("Patchouli support is currently not implemented, click this link instead")
                    .styled(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://guides.wispforest.io/things/"))
                            .withFormatting(Formatting.UNDERLINE)));
//            PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, book.id);
//            player.playSound(PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN), 1, (float) (0.7 + Math.random() * 0.4));
        }

        return TypedActionResult.success(stack);
    }
}
