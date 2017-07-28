package com.pome.sidedbuffer.tiles;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySidedBuffer extends TileEntity implements ISidedInventory
{

	ItemStack[] inventory = new ItemStack[45];

	List<int[]> accessibleSlots = null;
	ForgeDirection[] dirs = new ForgeDirection[5];

	private boolean needUpdate = true;

	private void setAccessibleSlots()
	{
		if(worldObj == null)
		{
			return;
		}
		int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		List<int[]> array = new ArrayList<int[]>();
		List<ForgeDirection> directions = new ArrayList<ForgeDirection>();
		int slotNum = 0;
		for(int i = 0;i < 6;i++)
		{
			int[] temp = null;
			if(i == metadata)
			{
				temp = new int[45];
				for(int j = 0;j < 45;j++)
				{
					temp[j] = j;
				}
			}
			else
			{
				directions.add(ForgeDirection.getOrientation(i));
				temp = new int[9];
				for(int j = 0;j < 9;j++)
				{
					temp[j] = slotNum;
					slotNum++;
				}
			}
			array.add(temp);
		}
		accessibleSlots = array;
		for(int i = 0;i < 5;i++)
		{
			dirs[i] = directions.get(i);
		}
		needUpdate = false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[45];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");

			if (slot >= 0 && slot < 45)
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
			}
		}
		updateRotation();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 45; i++)
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
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2)
	{
		ItemStack stack = inventory[var1];
		if (stack != null)
		{
			if (stack.stackSize <= var2)
			{
				inventory[var1] = null;
				this.markDirty();
			}
			else
			{
				stack = stack.splitStack(var2);
				if (stack.stackSize == 0)
				{
					inventory[var1] = null;
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
		return "SidedBuffer";
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
	public void openInventory()
	{

	}

	@Override
	public void closeInventory()
	{

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
	{
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if(accessibleSlots == null)
		{
			setAccessibleSlots();
		}
		return accessibleSlots.get(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		return true;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(needUpdate)
		{
			updateRotation();
		}
	}

	private void updateRotation()
	{
		setAccessibleSlots();
	}

	public void markNeedsUpdate()
	{
		needUpdate = true;
	}

	public ForgeDirection[] getDirections()
	{
		return dirs;
	}

}
