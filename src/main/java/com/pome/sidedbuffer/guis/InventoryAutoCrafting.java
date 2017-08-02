package com.pome.sidedbuffer.guis;

import com.pome.sidedbuffer.tiles.TileEntityAutoCrafting;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryAutoCrafting extends InventoryCrafting
{
	Container container;
	TileEntityAutoCrafting workbench;

	public InventoryAutoCrafting(Container p_i1807_1_,TileEntityAutoCrafting tile)
	{
		super(p_i1807_1_, 3,3);
		workbench = tile;
		container = p_i1807_1_;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if(slot> 8 || slot < 0)
		{
			return null;
		}
		return workbench.getStackInSlot(slot);
	}
	public ItemStack getStackInRowAndColumn(int p_70463_1_, int p_70463_2_)
    {
        if (p_70463_1_ >= 0 && p_70463_1_ < 3)
        {
            int k = p_70463_1_ + p_70463_2_ * 3;
            return this.getStackInSlot(k);
        }
        else
        {
            return null;
        }
    }
	@Override
	public ItemStack decrStackSize(int var1, int var2)
	{
		if(workbench == null)
		{
			return null;
		}
		if(getStackInSlot(var1) != null)
        {
            ItemStack var3;
            if(getStackInSlot(var1).stackSize <= var2)
            {
            	var3 = getStackInSlot(var1);
            	setInventorySlotContents(var1, null);
            	this.markDirty();
            	container.onCraftMatrixChanged(this);
            	return var3;
            }
            else
            {
            	var3 = getStackInSlot(var1).splitStack(var2);

            	if (getStackInSlot(var1).stackSize == 0)
                {
            		setInventorySlotContents(var1, null);
                }

                this.markDirty();
                container.onCraftMatrixChanged(this);
                return var3;
            }
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if(workbench != null)
		{
			ItemStack stack = getStackInSlot(slot);
			setInventorySlotContents(slot, null);
			return stack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if(slot < 0 || slot > 8)
		{
			return;
		}
		if(workbench != null)
		{
			workbench.setInventorySlotContents(slot, stack);
		}
		container.onCraftMatrixChanged(this);
	}
}