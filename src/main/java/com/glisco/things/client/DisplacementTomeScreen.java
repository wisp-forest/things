package com.glisco.things.client;

import com.glisco.things.DisplacementTomeScreenHandler;
import com.glisco.things.ThingsCommon;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.mixin.ScreenAccessor;
import com.glisco.things.network.RequestTomeActionC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class DisplacementTomeScreen extends HandledScreen<ScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(ThingsCommon.MOD_ID, "textures/gui/displacement_tome.png");
    public static final Identifier TOME_WIDGETS_LOCATION = new Identifier(ThingsCommon.MOD_ID, "textures/gui/tome_widgets.png");

    private TextFieldWidget nameField;
    private final List<ButtonWidget> buttons = new ArrayList<>();

    private final PlayerInventory playerInventory;
    private final List<Object> currentInputEventData;
    private Consumer<String> finishInputAction;

    public DisplacementTomeScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
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
        nameField = new TextFieldWidget(this.textRenderer, x + 152, y + 60, 138, 12, new LiteralText(""));
        nameField.setMaxLength(20);
        nameField.setDrawsBackground(true);
        nameField.setUneditableColor(0xFFFFFF);
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
        NbtCompound tag = ((DisplacementTomeScreenHandler) this.handler).getBook().getOrCreateTag();
        int fuelLevel = tag.contains("Fuel") ? tag.getInt("Fuel") : 0;
        this.textRenderer.draw(matrices, new LiteralText("Charges: " + fuelLevel), titleX, titleY, 0xFFFFFF);
    }

    public void onNameFieldChange(String text) {
        if (!StringUtils.isBlank(nameField.getText())) {
            if (this.currentInputEventData.isEmpty()) {
                buttons.get(0).active = !((DisplacementTomeScreenHandler) handler).getBook().getSubTag("Targets").getKeys().contains(text);
            } else {
                buttons.get(0).active = buttons.get((Integer) currentInputEventData.get(0)).getMessage().getString().equals(text) || !((DisplacementTomeScreenHandler) handler).getBook().getSubTag("Targets").getKeys().contains(text);
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
        this.buttons.clear();
        ((ScreenAccessor)this).getDrawables().removeIf(drawable -> drawable instanceof ButtonWidget);
        children().removeIf(element -> element instanceof ButtonWidget);
        addDefaultButtons();

        NbtCompound tag = ((DisplacementTomeScreenHandler) this.handler).getBook().getOrCreateTag();
        if (tag.contains("Targets")) {
            NbtCompound targets = tag.getCompound("Targets");

            int i = 0;
            for (String s : targets.getKeys()) {
                this.addButton(new WarpButtonWidget(x + 6, y + 21 + i * 17, 133, 13, new LiteralText(s), button -> {
                    client.getNetworkHandler().sendPacket(RequestTomeActionC2SPacket.create(RequestTomeActionC2SPacket.Action.TELEPORT, s));
                }));
                i++;
            }
        }
    }

    private void addButton(ButtonWidget widget){
        buttons.add(widget);
        addDrawable(widget);
    }

    public void addDefaultButtons() {
        ButtonWidget okButton = new ButtonWidget(x + 150, y + 80, 70, 20, new LiteralText(""), button -> {
            finishInputEvent();
        });
        okButton.active = false;
        okButton.visible = false;
        this.addButton(okButton);

        ButtonWidget deleteButton = new ButtonWidget(x + 223, y + 80, 70, 20, new LiteralText("Delete"), button -> {
            delete();
        });
        deleteButton.active = false;
        deleteButton.visible = false;
        this.addButton(deleteButton);

        ButtonWidget createButton = new SmallButtonWidget(x + 130, y + 4, 12, 12, new LiteralText("+"), button -> {
            startCreating();
        });
        createButton.active = playerInventory.containsAny(Collections.singleton(ThingsItems.DISPLACEMENT_PAGE)) && ((DisplacementTomeScreenHandler) handler).getBook().getOrCreateSubTag("Targets").getSize() <= 7;

        this.addButton(createButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
        nameField.render(matrices, mouseX, mouseY, delta);
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

    private static class SmallButtonWidget extends ButtonWidget {

        public SmallButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            TextRenderer textRenderer = minecraftClient.textRenderer;
            RenderSystem.setShaderTexture(0, TOME_WIDGETS_LOCATION);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
            int j = this.active ? 16777215 : 10526880;
            drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

    private static class WarpButtonWidget extends ButtonWidget {

        public WarpButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            TextRenderer textRenderer = minecraftClient.textRenderer;
            RenderSystem.setShaderTexture(0, TOME_WIDGETS_LOCATION);
            int i = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.drawTexture(matrices, this.x, this.y, 0, 126 + i * 20, this.width / 2, this.height);
            this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 126 + i * 20, this.width / 2, this.height);
            this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
            int j = this.active ? 0x001054 : 0x001054;
            textRenderer.draw(matrices, this.getMessage(), (this.x + this.width / 2 - textRenderer.getWidth(this.getMessage()) / 2), this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }

}
