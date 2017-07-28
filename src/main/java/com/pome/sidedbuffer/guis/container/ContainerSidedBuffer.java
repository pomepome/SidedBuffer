package com.pome.sidedbuffer.guis.container;

import com.pome.sidedbuffer.tiles.TileEntitySidedBuffer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSidedBuffer extends Container
{
	TileEntitySidedBuffer tile;
	int numRows = 5;

	public ContainerSidedBuffer(InventoryPlayer pInv,TileEntitySidedBuffer te)
	{
		tile = te;
		tile.openInventory();
		int i = (this.numRows - 3) * 18 + 1;
        int j;
        int k;

        for (j = 0; j < 5; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(te, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(pInv, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(pInv, j, 8 + j * 18, 161 + i));
        }
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack())
		{
			return null;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex < 45)
		{
			if (!this.mergeItemStack(stack, 73, this.inventorySlots.size(), false))
			{
				if(!this.mergeItemStack(stack, 46, 72, false))
				{
					return null;
				}
			}
		}
		else if (!this.mergeItemStack(stack, 0, 45, false))
		{
			return null;
		}

		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}

		return newStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_)
	{
		return true;
	}

}
