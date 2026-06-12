package icu.ytlsnb.ytls.client.screen;

import icu.ytlsnb.ytls.menu.MilkAltarMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MilkAltarScreen extends AbstractContainerScreen<MilkAltarMenu> {

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
        int right = left + this.imageWidth;
        int bottom = top + this.imageHeight;
        // 主体卡片
        graphics.fill(left, top, right, bottom, 0xCC151515);
        graphics.fill(left + 1, top + 1, right - 1, bottom - 1, 0xCC262626);
        graphics.fill(left + 2, top + 2, right - 2, bottom - 2, 0xCC303030);

        // 标题条
        graphics.fill(left + 4, top + 4, right - 4, top + 18, 0xAA8F7A4A);
        graphics.fill(left + 5, top + 5, right - 5, top + 17, 0xAA6E5E3A);

        // 祭坛槽位边框和内核
        int slotX = left + 79;
        int slotY = top + 20;
        graphics.fill(slotX - 3, slotY - 3, slotX + 19, slotY + 19, 0xFF7C6A40);
        graphics.fill(slotX - 2, slotY - 2, slotX + 18, slotY + 18, 0xFFB7A26A);
        graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF2A2A2A);
        graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF101010);

        // 玩家背包面板
        graphics.fill(left + 7, top + 49, left + 169, top + 127, 0x99202020);
        graphics.fill(left + 8, top + 50, left + 168, top + 126, 0xAA2C2C2C);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFF3D3, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xE0E0E0, false);
        graphics.drawString(this.font, "放入奶桶以塑造奶雨", 48, 23, 0xCFCFCF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
