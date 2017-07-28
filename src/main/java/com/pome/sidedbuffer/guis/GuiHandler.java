package com.pome.sidedbuffer.guis;

import com.pome.sidedbuffer.SidedBuffer;
import com.pome.sidedbuffer.guis.container.ContainerAutoCrafting;
import com.pome.sidedbuffer.guis.container.ContainerSidedBuffer;
import com.pome.sidedbuffer.tiles.TileEntityAutoCrafting;
import com.pome.sidedbuffer.tiles.TileEntitySidedBuffer;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == SidedBuffer.BUFFER_IGUI)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntitySidedBuffer)
			{
				return new ContainerSidedBuffer(player.inventory, (TileEntitySidedBuffer)te);
			}
		}
		if(ID == SidedBuffer.AUTOCRAFTING_IGUI)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntity)
			{
				return new ContainerAutoCrafting(player.inventory, (TileEntityAutoCrafting)te);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == SidedBuffer.BUFFER_IGUI)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntitySidedBuffer)
			{
				return new GuiSidedBuffer(player.inventory, (TileEntitySidedBuffer)te);
			}
		}
		if(ID == SidedBuffer.AUTOCRAFTING_IGUI)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileEntity)
			{
				return new GuiAutoCrafting(player.inventory, (TileEntityAutoCrafting)te);
			}
		}
		return null;
	}

}
