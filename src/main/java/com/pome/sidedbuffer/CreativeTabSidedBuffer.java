package com.pome.sidedbuffer;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabSidedBuffer extends CreativeTabs {

	public CreativeTabSidedBuffer()
	{
		super("SidedBuffer");
	}

	@Override
	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(SidedBuffer.buffer);
	}

}
