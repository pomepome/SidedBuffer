package com.pome.sidedbuffer.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pome.sidedbuffer.ItemDummy;
import com.pome.sidedbuffer.ItemStackContainer;
import com.pome.sidedbuffer.SidedBuffer;
import com.pome.sidedbuffer.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAutoCrafting extends TileEntity implements ISidedInventory
{
	private static final Logger logger = LogManager.getLogger("SidedBuffer:TileEntityAutoCrafting");

	private ItemStack[] inventory = new ItemStack[10];
    public ItemStack currentResult;

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if(slot < 0 || slot >= inventory.length)
		{
			return null;
		}
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if(slot == 9)
		{
			if(inventory[9] == null || inventory[9].getItem() == SidedBuffer.dummy)
			{
				if(!executeCrafting())
				{
					return null;
				}
				return decrStackSize(slot, inventory[9].stackSize);
			}
		}
		ItemStack stack = inventory[slot];
		if (stack != null)
		{
			if (stack.stackSize <= amount)
			{
				inventory[slot] = null;
				this.markDirty();
			}
			else
			{
				stack = stack.splitStack(amount);
				if (stack.stackSize == 0)
				{
					inventory[slot] = null;
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stack = inventory[slot];
		if(stack!= null)
		{
			inventory[slot] = null;
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
			inventory[slot] = stack;
			this.markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return "AutoCrafting";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
	{
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		if(slot == 9)
		{
			return false;
		}
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return new int[]{0,1,2,3,4,5,6,7,8,9};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		if(slot < 0 || slot > 8 || inventory[slot] == null)
		{
			return false;
		}
		return slot == getLowestStackSizeSlot(stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		return slot == 9;
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[10];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");

			if (slot >= 0 && slot < 10)
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
			}
		}
		if(nbt.hasKey("CurrentResult"))
		{
			NBTTagCompound tag = nbt.getCompoundTag("CurrentResult");
			currentResult = ItemStack.loadItemStackFromNBT(tag);
			if(SidedBuffer.debug && currentResult != null)
			{
				logger.info(String.format("Current Location:{%d,%d,%d}", xCoord,yCoord,zCoord));
				logger.info("Current output:"+currentResult.getDisplayName());
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 10; i++)
		{
			if (inventory[i] == null)
			{
				continue;
			}

			NBTTagCompound subNBT = new NBTTagCompound();
			subNBT.setByte("Slot", (byte) i);
			inventory[i].writeToNBT(subNBT);
			list.appendTag(subNBT);
		}

		nbt.setTag("Items", list);

		if(currentResult != null)
		{
			NBTTagCompound subNBT = new NBTTagCompound();
			currentResult.writeToNBT(subNBT);
			nbt.setTag("CurrentResult", subNBT);
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(inventory[9] == null)
		{
			inventory[9] = new ItemStack(SidedBuffer.dummy,1);
		}
	}

	public List<ItemStack> getCopy()
	{
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for(int i = 0;i < 9;i++)
		{
			ItemStack stack = inventory[i];
			if(stack != null && !(stack.getItem() instanceof ItemDummy))
			{
				ret.add(stack.copy());
			}
		}
		return ret;
	}

	private boolean executeCrafting()
	{
		if(currentResult == null)
		{
			return false;
		}

		ItemStackContainer ingrs = Util.getIngredients(this);

		boolean flag = true;

		for(ItemStack ingr : ingrs.copy())
		{
			if(!Util.decrStacksFromInventories(this, ingr, null, false,true))
			{
				flag = false;
			}
		}

		if(flag)
		{
			Map<IInventory,ItemStackContainer> found = new HashMap<IInventory, ItemStackContainer>();
			for(ItemStack ingr : ingrs.copy())
			{
				Util.decrStacksFromInventories(this, ingr, found, true,true);
			}
			Util.pushStacksIntoChests(this, found);
			inventory[9] = currentResult.copy();
			markDirty();
		}
		return flag;
	}

	private int getLowestStackSizeSlot(ItemStack stack)
	{
		int min = stack.getMaxStackSize();
		int slot = -1;
		for(int i = 0;i < 9;i++)
		{
			if(inventory[i] == null)
			{
				continue;
			}
			else if(Util.compareStacks(stack, inventory[i]) && inventory[i].stackSize < min)
			{
				min = inventory[i].stackSize;
				slot = i;
			}
		}
		return slot;
	}
}