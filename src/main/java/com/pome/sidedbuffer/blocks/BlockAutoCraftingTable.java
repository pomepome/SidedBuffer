package com.pome.sidedbuffer.blocks;

import com.pome.sidedbuffer.SidedBuffer;
import com.pome.sidedbuffer.tiles.TileEntityAutoCrafting;
import com.pome.sidedbuffer.util.Util;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockAutoCraftingTable extends BlockContainer
{

	private IIcon side,top,bottom;

	public BlockAutoCraftingTable()
	{
		super(Material.wood);
		this.setBlockName("AutoCraftingTable").setCreativeTab(SidedBuffer.creativeTabSB).setHardness(3);
		GameRegistry.registerBlock(this, "AutoCraftingTable");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityAutoCrafting();
	}
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		IInventory tile = (IInventory)world.getTileEntity(x,y,z);
		for(int i = 0;i < 9;i++)
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
		if(!player.isSneaking())
		{
			player.openGui(SidedBuffer.instance, SidedBuffer.AUTOCRAFTING_IGUI, world, x, y, z);
		}
	    return true;
	}
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister reg)
    {
		side = reg.registerIcon("sidedbuffer:autocraft_side");
		top = reg.registerIcon("sidedbuffer:autocraft_top");
		bottom = Blocks.crafting_table.getBlockTextureFromSide(0);
    }

	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int ori, int metadata)
    {
		switch(ori)
		{
			case 0 : return bottom;
			case 1 : return top;
			default : return side;
		}
    }

}
