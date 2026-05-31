package icu.ytlsnb.ytls.client.screen;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.menu.MilkAltarMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MilkAltarScreen extends AbstractContainerScreen<MilkAltarMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/milk_altar.png");

    public MilkAltarScreen(MilkAltarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        graphics.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
