package com.pome.sidedbuffer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemStackContainer
{
	private List<ItemStack> stacks = new ArrayList<ItemStack>();

	public void add(ItemStack stack)
	{
		if(stack == null)
		{
			return;
		}
		int id = Util.getMatchedStackId(stacks, stack);
		if(id == -1)
		{
			stacks.add(stack.copy());
		}
		else
		{
			ItemStack stack2 = stacks.get(id).copy();
			stack2.stackSize += stack.stackSize;
			stacks.set(id, stack2);
		}
	}

	public List<ItemStack> copy()
	{
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for(ItemStack stack : stacks)
		{
			if(stack != null)
			{
				ret.add(stack.copy());
			}
		}
		return ret;
	}
}
