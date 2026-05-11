package icu.ytlsnb.ytls.client.gui;

import icu.ytlsnb.ytls.world.inventory.BaguaFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * GUI 纹理占位：默认使用原版熔炉界面贴图；正式贴图路径见 {@code doc/八卦炉_资源占位说明.md}。
 */
public class BaguaFurnaceScreen extends AbstractContainerScreen<BaguaFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    public BaguaFurnaceScreen(BaguaFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int lit = menu.getLitProgress();
        if (lit > 0) {
            graphics.blit(TEXTURE, x + 56, y + 36 + 12 - lit, 176, 12 - lit, 14, lit + 1);
        }
        int cook = menu.getCookProgress();
        graphics.blit(TEXTURE, x + 79, y + 34, 176, 14, cook + 1, 16);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
