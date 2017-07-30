package com.pome.sidedbuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pome.sidedbuffer.blocks.BlockAutoCraftingTable;
import com.pome.sidedbuffer.blocks.BlockSidedBuffer;
import com.pome.sidedbuffer.creativetabs.CreativeTabSidedBuffer;
import com.pome.sidedbuffer.guis.GuiHandler;
import com.pome.sidedbuffer.tiles.TileEntityAutoCrafting;
import com.pome.sidedbuffer.tiles.TileEntitySidedBuffer;
import com.pome.sidedbuffer.util.ItemDummy;
import com.pome.sidedbuffer.util.Util;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;

@Mod(modid="SidedBuffer", name="SidedBuffer", version="0.3")
public class SidedBuffer
{
	public static SidedBuffer instance;
	public static Logger logger = LogManager.getLogger("SidedBuffer");

	// Gui IDs
	public static final int BUFFER_IGUI = 0;
	public static final int AUTOCRAFTING_IGUI = 1;

	public static CreativeTabs creativeTabSB = new CreativeTabSidedBuffer();

	public static Block buffer;
	public static Block autoCrafting;

	public static Item dummy;

	public static boolean debug;
	public static boolean ignoreSidedInventory;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e)
	{
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		debug = config.getBoolean("debug", Configuration.CATEGORY_GENERAL, false, "Debugging mode: it shouldn't be used by everyone.");
		ignoreSidedInventory = config.getBoolean("ignoreSidedInventory", Configuration.CATEGORY_GENERAL, false, "if true, Auto-crafting table can't use or extract to the sided Inventory's item.");
		config.save();
	}

	@EventHandler
	public void onInit(FMLInitializationEvent e)
	{
		instance = this;
		buffer = new BlockSidedBuffer();
		autoCrafting = new BlockAutoCraftingTable();
		dummy = new ItemDummy();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		GameRegistry.registerTileEntity(TileEntitySidedBuffer.class, "pome_SidedBudder");
		GameRegistry.registerTileEntity(TileEntityAutoCrafting.class, "pome_AutoCrafting");
		GameRegistry.addShapelessRecipe(new ItemStack(buffer,2), new ItemStack(Blocks.chest), new ItemStack(Blocks.chest));
		GameRegistry.addShapelessRecipe(new ItemStack(autoCrafting), new ItemStack(Blocks.crafting_table),new ItemStack(Items.redstone));
		logger.info("Dir:"+Util.getInitialOfDirection(ForgeDirection.DOWN));
	}
}
