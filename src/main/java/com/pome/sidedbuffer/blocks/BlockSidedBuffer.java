package com.pome.sidedbuffer.blocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pome.sidedbuffer.SidedBuffer;
import com.pome.sidedbuffer.tiles.TileEntitySidedBuffer;
import com.pome.sidedbuffer.util.Util;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSidedBuffer extends BlockContainer
{
	private IIcon front,back,left,right,up,down;

	Logger log = LogManager.getLogger("SidedBuffer");

	private static final int[] RIGHT_SIDES = {0,0,4,5,3,2};

	public BlockSidedBuffer()
	{
		super(Material.wood);
		this.setBlockName("SidedBuffer").setCreativeTab(SidedBuffer.creativeTabSB).setHardness(5);
		GameRegistry.registerBlock(this, "SidedBuffer");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntitySidedBuffer();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		IInventory tile = (IInventory)world.getTileEntity(x,y,z);
		for(int i = 0;i < tile.getSizeInventory();i++)
		{
			ItemStack stack = tile.getStackInSlotOnClosing(i);
			if(stack != null && stack.getItem() != SidedBuffer.dummy)
			{
				Util.spawnEntityItem(world, stack, x, y, z, 0.05f);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
    }

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
		{
			return true;
		}
		if(player.isSneaking())
		{
			int metadata = world.getBlockMetadata(x, y, z);
			world.setBlockMetadataWithNotify(x, y, z, (metadata + 1) % 6, 2);
			metadata = world.getBlockMetadata(x, y, z);
			((TileEntitySidedBuffer)world.getTileEntity(x, y, z)).markNeedsUpdate();
		}
		else
		{
			player.openGui(SidedBuffer.instance, SidedBuffer.BUFFER_IGUI, world, x, y, z);
		}
	    return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack)
	{
		world.setBlockMetadataWithNotify(x, y, z, BlockPistonBase.determineOrientation(world, x, y, z, entityLiving), 2);
		((TileEntitySidedBuffer)world.getTileEntity(x, y, z)).markNeedsUpdate();
	}

	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister reg)
    {
		front = reg.registerIcon("sidedbuffer:sb_front");
		back = reg.registerIcon("sidedbuffer:sb_back");
		up = reg.registerIcon("sidedbuffer:sb_up");
		down = reg.registerIcon("sidedbuffer:sb_down");
		left = reg.registerIcon("sidedbuffer:sb_left");
		right = reg.registerIcon("sidedbuffer:sb_right");
    }

	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int ori, int metadata)
    {
		if(ori == metadata)
		{
			return front;
		}
		else if (ori == Util.getOpSide(metadata))
		{
			return back;
		}
		else if(metadata == 0)
		{
			return down;
		}
		else if(metadata == 1)
		{
			return up;
		}
		else
		{
			if(ori == 0 || ori == 1)
			{

				switch(metadata)
				{
					case 2: return up;
					case 3: return down;
					case 4: return left;
					case 5: return right;
				}
			}
			else
			{
				return ori == RIGHT_SIDES[metadata] ? left : right;
			}
		}
		return null;
    }
}
