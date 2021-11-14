package com.glisco.things.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ButtonWithMessageWidget extends ButtonWidget {

    private final Identifier texture;
    private final int u;
    private final int v;
    private int textColor = 0xffffff;

    public ButtonWithMessageWidget(int x, int y, int width, int height, int u, int v, Identifier texture, PressAction pressAction) {
        super(x, y, width, height, Text.of(""), pressAction);
        this.texture = texture;
        this.u = u;
        this.v = v;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        int i = this.v;
        if (!this.active) {
            i -= this.height;
        } else if (this.isHovered()) {
            i += this.height;
        }

        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, (float) this.u, (float) i, this.width, this.height, 256, 256);
        if (this.isHovered()) {
            this.renderTooltip(matrices, mouseX, mouseY);
        }

        final var text = this.getMessage().asOrderedText();
        final var textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(matrices, text, (float) (this.x + this.width / 2 - textRenderer.getWidth(text) / 2), this.y + (this.height - 8) / 2f,
                this.active ? textColor : 0xa0a0a0 | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
