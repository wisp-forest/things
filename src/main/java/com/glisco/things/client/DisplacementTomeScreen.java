package com.glisco.things.client;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.generic.DisplacementTomeItem;
import com.glisco.things.misc.DisplacementTomeScreenHandler;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.base.BaseUIModelHandledScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TexturedButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DisplacementTomeScreen extends BaseUIModelHandledScreen<FlowLayout, DisplacementTomeScreenHandler> {

    private final PlayerInventory inventory;

    protected DisplacementTomeScreen(DisplacementTomeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, FlowLayout.class, BaseUIModelScreen.DataSource.file("../src/main/resources/assets/things/owo_ui/displacement_tome.xml"));
        this.inventory = inventory;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void build(FlowLayout rootComponent) {
        var bookNbt = this.handler.getBook().getOrCreateNbt();
        var buttonContainer = rootComponent.childById(FlowLayout.class, "button-container");
        var floatingAnchor = rootComponent.childById(FlowLayout.class, "floating-anchor");

        rootComponent.childById(LabelComponent.class, "charge-label").text(Text.translatable("gui.things.displacement_tome.charges", bookNbt.get(DisplacementTomeItem.FUEL)));

        var targets = new HashSet<String>();

        var newButton = rootComponent.childById(TexturedButtonComponent.class, "new-button");
        newButton.active = inventory.containsAny(Set.of(ThingsItems.DISPLACEMENT_PAGE)) && bookNbt.get(DisplacementTomeItem.TARGETS).getSize() < 8;
        if (!newButton.active) newButton.tooltip(Text.translatable("gui.things.displacement_tome.no_pages"));

        newButton.onPress(button -> {
            var floating = this.model.expandTemplate(FlowLayout.class, "create-box", Map.of("text", ""));
            var createButton = floating.childById(ButtonWidget.class, "create-button");
            var createBox = floating.childById(TextFieldWidget.class, "text-field");

            createBox.setChangedListener(s -> createButton.active = !s.isBlank() && !targets.contains(s));
            createButton.onPress(b -> this.handler.addPoint(createBox.getText()));

            this.clearAnchor(floatingAnchor);

            if (buttonContainer.children().isEmpty()) {
                var background = rootComponent.childById(Component.class, "background");
                floatingAnchor.positioning(Positioning.absolute(background.x() + 149, background.y() + 18));
            } else {
                var lastButton = buttonContainer.children().get(buttonContainer.children().size() - 1);
                floatingAnchor.positioning(Positioning.absolute(lastButton.x() + lastButton.width() + 10, lastButton.y() + lastButton.height()));
            }
            floatingAnchor.child(floating);
        });

        if (bookNbt.has(DisplacementTomeItem.TARGETS)) {
            for (var target : bookNbt.get(DisplacementTomeItem.TARGETS).getKeys()) {
                targets.add(target);

                var teleportComponent = this.model.expandTemplate(FlowLayout.class, "teleport-button", Map.of());
                var teleportButton = teleportComponent.childById(TexturedButtonComponent.class, "teleport-button");
                var editLabel = teleportComponent.childById(LabelComponent.class, "edit-label");

                teleportButton.setMessage(TextOps.withColor(target, 0x0096FF));
                buttonContainer.child(teleportComponent);

                teleportComponent.mouseEnter().subscribe(() -> editLabel.positioning(editLabel.positioning().get().withX(98)));
                teleportComponent.mouseLeave().subscribe(() -> editLabel.positioning(editLabel.positioning().get().withX(110)));

                editLabel.mouseEnter().subscribe(() -> editLabel.color(Color.ofArgb(0x00D7FF)));
                editLabel.mouseLeave().subscribe(() -> editLabel.color(Color.ofArgb(0x0096FF)));
                editLabel.cursorStyle(CursorStyle.HAND);

                editLabel.mouseDown().subscribe((mouseX, mouseY, button) -> {
                    UISounds.playInteractionSound();

                    if (floatingAnchor.children().size() != 0 && floatingAnchor.positioning().get().y == teleportButton.y) {
                        this.clearAnchor(floatingAnchor);
                        return true;
                    }

                    var floating = this.model.expandTemplate(FlowLayout.class, "edit-box", Map.of("text", target));
                    var editBox = floating.childById(TextFieldWidget.class, "text-field");

                    var renameButton = floating.childById(ButtonWidget.class, "rename-button");
                    renameButton.onPress(b -> this.handler.renamePoint(target + ":" + editBox.getText()));
                    floating.childById(ButtonWidget.class, "delete-button").onPress(b -> this.handler.deletePoint(target));

                    editBox.setChangedListener(s -> renameButton.active = !s.isBlank() && !targets.contains(s));

                    this.clearAnchor(floatingAnchor);
                    floatingAnchor.positioning(Positioning.absolute(teleportButton.x + teleportButton.width() + 10, teleportButton.y));
                    floatingAnchor.child(floating);

                    return true;
                });

                teleportButton.onPress(button -> this.handler.requestTeleport(target));
            }
        }
    }

    private void clearAnchor(FlowLayout layout) {
        if (layout.children().size() < 1) return;
        layout.removeChild(layout.children().get(0));
    }

    @Override
    public void clearAndInit() {
        super.clearAndInit();
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}
}
