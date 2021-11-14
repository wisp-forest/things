package com.glisco.things.client;

import com.glisco.things.DisplacementTomeScreenHandler;
import com.glisco.things.ThingsCommon;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.network.RequestTomeActionC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class DisplacementTomeScreen extends HandledScreen<DisplacementTomeScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(ThingsCommon.MOD_ID, "textures/gui/displacement_tome.png");

    private final List<ButtonWidget> buttons = new ArrayList<>();
    private TextFieldWidget nameField;

    private final PlayerInventory playerInventory;
    private final List<Object> currentInputEventData;
    private Consumer<String> finishInputAction;

    public DisplacementTomeScreen(DisplacementTomeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 147;
        this.currentInputEventData = new ArrayList<>();
        this.playerInventory = inventory;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void init() {
        super.init();
        client.keyboard.setRepeatEvents(true);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        nameField = new TextFieldWidget(this.textRenderer, x + 152, y + 60, 138, 12, Text.of(""));
        nameField.setMaxLength(20);
        nameField.setDrawsBackground(true);
        nameField.setUneditableColor(0xFF0000);
        nameField.visible = false;
        nameField.active = false;
        nameField.setChangedListener(this::onNameFieldChange);

        addDrawableChild(nameField);
        update();
    }

    @Override
    public void onClose() {
        super.onClose();
        client.keyboard.setRepeatEvents(false);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        NbtCompound tag = this.handler.getBook().getOrCreateNbt();
        int fuelLevel = tag.contains("Fuel") ? tag.getInt("Fuel") : 0;
        this.textRenderer.draw(matrices, new LiteralText("Charges: " + fuelLevel), titleX, titleY, 0xFFFFFF);
    }

    public void onNameFieldChange(String text) {
        if (!StringUtils.isBlank(nameField.getText())) {
            if (this.currentInputEventData.isEmpty()) {
                buttons.get(0).active = !handler.getBook().getSubNbt("Targets").getKeys().contains(text);
            } else {
                buttons.get(0).active = buttons.get((Integer) currentInputEventData.get(0)).getMessage().getString().equals(text)
                        || !handler.getBook().getSubNbt("Targets").getKeys().contains(text);
            }
        } else {
            buttons.get(0).active = false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.player.closeHandledScreen();
        }

        return this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void update() {
        this.buttons.forEach(this::remove);
        this.buttons.clear();

        addDefaultButtons();

        NbtCompound tag = this.handler.getBook().getOrCreateNbt();
        if (tag.contains("Targets")) {
            NbtCompound targets = tag.getCompound("Targets");

            int idx = 0;
            for (String s : targets.getKeys()) {
                final var widget = new ButtonWithMessageWidget(x + 6, y + 21 + idx * 17, 133, 13, 0, 179, TEXTURE, button -> {
                    client.getNetworkHandler().sendPacket(RequestTomeActionC2SPacket.create(RequestTomeActionC2SPacket.Action.TELEPORT, s));
                });
                widget.setTextColor(0x001054);
                widget.setMessage(Text.of(s));
                this.addButton(widget);
                idx++;
            }
        }
    }

    private void addButton(ButtonWidget widget) {
        buttons.add(widget);
        addDrawable(widget);
    }

    public void addDefaultButtons() {
        ButtonWidget okButton = new ButtonWidget(x + 150, y + 80, 70, 20, new LiteralText(""), button -> finishInputEvent());
        okButton.active = false;
        okButton.visible = false;
        this.addButton(okButton);

        ButtonWidget deleteButton = new ButtonWidget(x + 223, y + 80, 70, 20, new LiteralText("Delete"), button -> delete());
        deleteButton.active = false;
        deleteButton.visible = false;
        this.addButton(deleteButton);

        ButtonWidget createButton = new ButtonWithMessageWidget(x + 130, y + 4, 12, 12, 147, 12, TEXTURE, button -> startCreating());
        createButton.setMessage(Text.of("+"));
        createButton.active = playerInventory.containsAny(Collections.singleton(ThingsItems.DISPLACEMENT_PAGE)) &&
                handler.getBook().getOrCreateSubNbt("Targets").getSize() <= 7;
        this.addButton(createButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            for (ButtonWidget b : buttons) {
                if (!b.isHovered()) continue;
                startRenaming(buttons.indexOf(b));
            }
        }
        for (ButtonWidget b : buttons) {
            if (b.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void startRenaming(int buttonIndex) {
        this.currentInputEventData.clear();
        this.currentInputEventData.add(buttonIndex);

        ButtonWidget button = buttons.get(buttonIndex);

        this.nameField.visible = true;
        this.nameField.active = true;
        this.nameField.setText(button.getMessage().getString());

        this.buttons.get(0).active = true;
        this.buttons.get(0).visible = true;
        this.buttons.get(0).setMessage(Text.of("Rename"));

        this.buttons.get(1).active = true;
        this.buttons.get(1).visible = true;

        this.setFocused(this.nameField);
        finishInputAction = this::finishRenaming;
    }

    private void startCreating() {
        this.nameField.visible = true;
        this.nameField.active = true;
        this.nameField.setText("");

        this.buttons.get(0).visible = true;
        this.buttons.get(0).setMessage(Text.of("Create"));

        this.setFocused(this.nameField);
        finishInputAction = this::finishCreating;
    }

    private void finishInputEvent() {
        this.finishInputAction.accept(nameField.getText());
    }

    private void finishRenaming(String text) {
        client.getNetworkHandler().sendPacket(RequestTomeActionC2SPacket.create(RequestTomeActionC2SPacket.Action.RENAME_POINT, buttons.get((Integer) currentInputEventData.get(0)).getMessage().getString() + ":" + text));
        this.nameField.visible = false;
        this.nameField.active = false;

        this.buttons.get(0).visible = false;
        this.buttons.get(0).active = false;
        this.buttons.get(1).visible = false;
        this.buttons.get(1).active = false;
    }

    private void delete() {
        client.getNetworkHandler().sendPacket(RequestTomeActionC2SPacket.create(RequestTomeActionC2SPacket.Action.DELETE_POINT, buttons.get((Integer) currentInputEventData.get(0)).getMessage().getString()));
        this.nameField.visible = false;
        this.nameField.active = false;

        this.buttons.get(0).visible = false;
        this.buttons.get(0).active = false;
        this.buttons.get(1).visible = false;
        this.buttons.get(1).active = false;
    }

    private void finishCreating(String text) {
        client.getNetworkHandler().sendPacket(RequestTomeActionC2SPacket.create(RequestTomeActionC2SPacket.Action.CREATE_POINT, text));
        playerInventory.getStack(playerInventory.getSlotWithStack(new ItemStack(ThingsItems.DISPLACEMENT_PAGE))).decrement(1);
        this.nameField.visible = false;
        this.nameField.active = false;

        this.buttons.get(0).visible = false;
        this.buttons.get(0).active = false;
    }

    public boolean isNameFieldVisible() {
        return this.nameField.isVisible();
    }

    public int getRootX() {
        return (width - backgroundWidth) / 2;
    }

    public int getRootY() {
        return (height - backgroundHeight) / 2;
    }

}
