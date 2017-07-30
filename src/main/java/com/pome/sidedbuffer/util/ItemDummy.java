package com.pome.sidedbuffer.util;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemDummy extends Item
{
	public ItemDummy()
	{
		GameRegistry.registerItem(this, "Dummy_item");
		this.setUnlocalizedName("sb_dummy").setTextureName("sidedbuffer:null");
	}
}
