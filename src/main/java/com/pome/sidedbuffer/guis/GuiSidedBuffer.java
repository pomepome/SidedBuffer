package com.pome.sidedbuffer.guis;

import com.pome.sidedbuffer.guis.container.ContainerSidedBuffer;
import com.pome.sidedbuffer.tiles.TileEntitySidedBuffer;
import com.pome.sidedbuffer.util.Util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiSidedBuffer extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("sidedbuffer", "textures/gui/buffer.png");

	private final TileEntitySidedBuffer tile;

	public GuiSidedBuffer(InventoryPlayer pInv,TileEntitySidedBuffer te)
	{
		super(new ContainerSidedBuffer(pInv, te));
		this.xSize = 175;
		this.ySize = 221;
		tile = te;
	}

	@Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ) {
		this.fontRendererObj.drawString(I18n.format("container.sidedbuffer", new Object[0]), 8, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8 + 108, this.ySize - 96 + 2, 4210752);
		for(int i = 0;i < 5;i++)
		{
			ForgeDirection dir = tile.getDirections()[i];
			String initial = Util.getInitialOfDirection(dir);
			this.fontRendererObj.drawString(initial, 14 + 15*i, 118, 4210752);
		}
    }

	@Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ) {
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
    }

}
