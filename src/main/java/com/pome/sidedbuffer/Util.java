package com.pome.sidedbuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pome.sidedbuffer.tiles.TileEntityAutoCrafting;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Util
{
	private static Logger logger = LogManager.getLogger("SidedBuffer:Util");

	public static int getOpSide(int side)
	{
		return ForgeDirection.getOrientation(side).getOpposite().ordinal();
	}
	public static int[] getSlotsFromSide(ISidedInventory inv,int side)
	{
		return inv.getAccessibleSlotsFromSide(side);
	}
	public static ItemStack normalizeStack(ItemStack stack)
	{
		return changeAmount(stack, 1);
	}
	public static ItemStack changeAmount(ItemStack stack,int count)
	{
		ItemStack newStack = stack.copy();
		newStack.stackSize = count;
		return newStack;
	}
	public static int getRightSide(int side)
	{
		int ret = side - 1;
		return ret < 2 ? 5 : ret;
	}
	public static ItemStack[] copyStacks(List<ItemStack> list)
	{
		ItemStack[] arr = new ItemStack[list.size()];
		for(int i = 0;i < arr.length;i++)
		{
			arr[i] = list.get(i);
		}
		return arr;
	}
	public static int[] getAccecibleSlots(IInventory inv, int side)
	{
		if(inv instanceof ISidedInventory)
		{
			return ((ISidedInventory)inv).getAccessibleSlotsFromSide(side);
		}
		int[] slots = new int[inv.getSizeInventory()];
		for(int i = 0;i < slots.length;i++)
		{
			slots[i] = i;
		}
		return slots;
	}
	public static boolean compareStacks(ItemStack stack1, ItemStack stack2)
	{
		if(stack1 == null || stack2 == null || stack1.getItemDamage() != stack2.getItemDamage())
		{
			return false;
		}
		if(stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock)
		{
			return ((ItemBlock)stack1.getItem()).field_150939_a == ((ItemBlock)stack2.getItem()).field_150939_a;
		}
		return stack1.getItem() == stack2.getItem();
	}
	public static boolean decrStacksFromInventories(TileEntity tile,ItemStack stack,Map<IInventory,ItemStackContainer> foundContainers,boolean act,boolean includeItself)
	{
		World w = tile.getWorldObj();

		List<IInventory> checkedInventories = new ArrayList<IInventory>();
		List<Integer> checkedDirections = new ArrayList<Integer>();

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			int ox = tile.xCoord + dir.offsetX;
			int oy = tile.yCoord + dir.offsetY;
			int oz = tile.zCoord + dir.offsetZ;
			TileEntity te = w.getTileEntity(ox, oy, oz);
			if(te instanceof IInventory && !(te instanceof TileEntityHopper))
			{
				checkedInventories.add((IInventory)te);
				checkedDirections.add(dir.ordinal());
				if(te instanceof TileEntityChest)
				{
					TileEntityChest chest = (TileEntityChest)te;

					IInventory subChest = getSubChest(chest);
					if(subChest != null)
					{
						checkedInventories.add(subChest);
						checkedDirections.add(dir.ordinal());
					}
				}
			}
		}

		if(stack == null)
		{
			return false;
		}
		if(!act)
		{
			int count = 0;
			for(int i = 0;i < checkedInventories.size();i++)
			{
				IInventory inv = checkedInventories.get(i);
				if(SidedBuffer.ignoreSidedInventory && inv instanceof ISidedInventory)
				{
					continue;
				}
				int os = getOpSide(checkedDirections.get(i));
				for(int slot : getAccecibleSlots(inv, os))
				{
					if(inv.getStackInSlot(slot) == null)
					{
						continue;
					}
					ItemStack stack2 = inv.getStackInSlot(slot);
					if(compareStacks(stack, stack2))
					{
						count += stack2.stackSize;
					}
					if(foundContainers != null && stack2.getItem().hasContainerItem(stack2))
					{
						if(foundContainers.containsKey(inv))
						{
							ItemStackContainer container = foundContainers.get(inv);
							container.add(makeContainerStack(stack2));
						}
						else
						{
							ItemStackContainer container = new ItemStackContainer();
							container.add(makeContainerStack(stack2));
							foundContainers.put(inv, container);
						}
					}
				}
			}
			if(tile instanceof IInventory)
			{
				boolean req = true;
				for(int i = 0;i < 9;i++)
				{
					ItemStack content = ((IInventory)tile).getStackInSlot(i);
					if(content == null)
					{
						continue;
					}
					if(content.getMaxStackSize() == 1)
					{
						break;
					}
					if(content.stackSize == 1)
					{
						req = false;
						break;
					}
				}
				if(includeItself && req && count < stack.stackSize)
				{
					IInventory inv = (IInventory)tile;
					for(int i = 0;i < 9;i++)
					{
						ItemStack stack2 = inv.getStackInSlot(i);
						if(compareStacks(stack, stack2) && stack2.stackSize > 1)
						{
							count += inv.getStackInSlot(i).stackSize - 1;
						}
					}
				}
			}
			return count >= stack.stackSize;
		}
		else
		{
			ItemStack remain = stack.copy();
			if(!decrStacksFromInventories(tile, stack,null, false,includeItself))
			{
				return false;
			}

			for(int i = 0;i < checkedInventories.size();i++)
			{
				IInventory inv = checkedInventories.get(i);
				if(SidedBuffer.ignoreSidedInventory && inv instanceof ISidedInventory)
				{
					continue;
				}
				int os = getOpSide(checkedDirections.get(i));
				for(int slot : getAccecibleSlots(inv, os))
				{
					if(inv.getStackInSlot(slot) == null)
					{
						continue;
					}
					ItemStack content = inv.getStackInSlot(slot).copy();
					if(!compareStacks(stack, content))
					{
						continue;
					}
					if(foundContainers != null && content.getItem().hasContainerItem(content))
					{
						if(foundContainers.containsKey(inv))
						{
							ItemStackContainer container = foundContainers.get(inv);
							container.add(makeContainerStack(content));
						}
						else
						{
							ItemStackContainer container = new ItemStackContainer();
							container.add(makeContainerStack(content));
							foundContainers.put(inv, container);
						}
					}
					if(content.stackSize > remain.stackSize)
					{
						content.stackSize -= remain.stackSize;
						remain.stackSize = 0;
					}
					else
					{
						remain.stackSize -= content.stackSize;
						content = null;
					}
					if(remain.stackSize == 0)
					{
						remain = null;
					}
					inv.setInventorySlotContents(slot, content);
					inv.markDirty();
					if(remain == null)
					{
						break;
					}
				}
				if(remain == null)
				{
					break;
				}
			}
			if(includeItself && tile instanceof IInventory && remain != null)
			{
				IInventory inv = (IInventory)tile;
				for(int i = 0;i < 9;i++)
				{
					if(compareStacks(stack, inv.getStackInSlot(i)) && inv.getStackInSlot(i).stackSize > 1)
					{
						ItemStack content = inv.getStackInSlot(i).copy();
						content.stackSize--;
						remain.stackSize--;
						if(remain.stackSize == 0)
						{
							remain = null;
						}
						inv.setInventorySlotContents(i, content);
						inv.markDirty();
						if(remain == null)
						{
							break;
						}
					}
				}
			}
			return true;
		}
	}

	public static ItemStack makeContainerStack(ItemStack stack)
	{
		if(stack == null || !stack.getItem().hasContainerItem(stack))
		{
			return null;
		}
		return new ItemStack(stack.getItem().getContainerItem(),1,stack.getItemDamage());
	}

	public static void pushStacksIntoChests(TileEntity tile,Map<IInventory,ItemStackContainer> map)
	{
		for(Map.Entry<IInventory, ItemStackContainer> entry : map.entrySet())
		{
			IInventory inv = entry.getKey();
			ItemStackContainer container = entry.getValue();
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			{
				TileEntity tt = tile.getWorldObj().getTileEntity(tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ);

				if(tt == null || !(tt instanceof IInventory))
				{
					continue;
				}

				IInventory subChest = getSubChest((IInventory)tt);

				if(tt == inv)
				{
					for(ItemStack stack : container.copy())
					{
						pushStackInInv(inv, getOpSide(dir.ordinal()), stack);
					}
				}
				else if(subChest != null && subChest == inv)
				{
					for(ItemStack stack : container.copy())
					{
						pushStackInInv(subChest, getOpSide(dir.ordinal()), stack);
					}
				}
			}
		}
	}

	public static IInventory getSubChest(IInventory inv)
	{
		if(inv instanceof TileEntityChest)
		{
			TileEntityChest chest = (TileEntityChest)inv;
			if(chest.adjacentChestXNeg != null)
			{
				return chest.adjacentChestXNeg;
			}
			if(chest.adjacentChestXPos != null)
			{
				return chest.adjacentChestXPos;
			}
			if(chest.adjacentChestZNeg != null)
			{
				return chest.adjacentChestZNeg;
			}
			if(chest.adjacentChestZPos != null)
			{
				return chest.adjacentChestZPos;
			}
		}
		return null;
	}

	public static ItemStack pushStackInInv(IInventory inv,int side, ItemStack stack)
	{
		if(SidedBuffer.debug)
		{
			logger.info("Pushed ItemStack:"+stack.getDisplayName()+" * " + stack.stackSize);
			if(inv instanceof TileEntity)
			{
				TileEntity tile = (TileEntity)inv;
				logger.info(String.format("TileEntity at {%d,%d,%d} from side:{%s}", tile.xCoord,tile.yCoord,tile.zCoord,ForgeDirection.getOrientation(side).name()));
			}
		}
		if(stack == null)
		{
			return null;
		}
		int limit;

		if (inv instanceof InventoryPlayer)
		{
			limit = 36;
		}
		else
		{
			limit = inv.getSizeInventory();
		}

		for (int i : getAccecibleSlots(inv, side))
		{
			if(i > limit)
			{
				continue;
			}
			ItemStack invStack = inv.getStackInSlot(i);

			if (invStack == null && inv.isItemValidForSlot(i, stack))
			{
				inv.setInventorySlotContents(i, stack);
				return null;
			}

			if (compareStacks(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				int max = Math.min(inv.getInventoryStackLimit(), invStack.getMaxStackSize());
				int remaining = max - invStack.stackSize;

				if (remaining >= stack.stackSize)
				{
					invStack.stackSize += stack.stackSize;
					inv.setInventorySlotContents(i, invStack);
					return null;
				}

				invStack.stackSize += remaining;
				inv.setInventorySlotContents(i, invStack);
				stack.stackSize -= remaining;
			}
		}

		return stack.copy();
	}
	public static int getMatchedStackId(List<ItemStack> stacks,ItemStack stack)
	{
		for(int i = 0;i < stacks.size();i++)
		{
			if(compareStacks(stack, stacks.get(i)))
			{
				return i;
			}
		}
		return -1;
	}
	public static ItemStack getMatchedStack(List<ItemStack> stacks,ItemStack stack)
	{
		int id = getMatchedStackId(stacks, stack);
		return id == -1 ? null : stacks.get(id);
	}
	public static ItemStackContainer getIngredients(TileEntityAutoCrafting tile)
	{
		ItemStackContainer container = new ItemStackContainer();

		for(ItemStack stack : tile.getCopy())
		{
			container.add(normalizeStack(stack));
		}
		return container;
	}
	public static String getInitialOfDirection(ForgeDirection dir)
	{
		if(dir == null)
		{
			return "X";
		}
		return dir.name().substring(0, 1);
	}
}
