package com.pome.sidedbuffer.guis.container;

import com.pome.sidedbuffer.SidedBuffer;
import com.pome.sidedbuffer.guis.InventoryAutoCrafting;
import com.pome.sidedbuffer.tiles.TileEntityAutoCrafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerAutoCrafting extends Container
{
    public InventoryCrafting craftMatrix;
    public IInventory craftResult = new InventoryCraftResult();

    private World worldObj;

    TileEntityAutoCrafting tile;

	public ContainerAutoCrafting(InventoryPlayer pInv, TileEntityAutoCrafting tile)
	{
		this.tile = tile;
		craftMatrix = new InventoryAutoCrafting(this, tile);
		this.addSlotToContainer(new SlotCrafting(pInv.player,this.craftMatrix, this.craftResult, 0, 124, 35));
        this.worldObj = pInv.player.worldObj;
		int l;
        int i1;

        for (l = 0; l < 3; ++l)
        {
            for (i1 = 0; i1 < 3; ++i1)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
            }
        }

        for (l = 0; l < 3; ++l)
        {
            for (i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(pInv, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(pInv, l, 8 + l * 18, 142));
        }

        //this.addSlotToContainer(new Slot(tile, 9 , 150, 35));

        this.onCraftMatrixChanged(this.craftMatrix);
	}

	public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
		tile.currentResult = craftResult.getStackInSlot(0);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer p_75134_1_)
    {
        super.onContainerClosed(p_75134_1_);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord) != SidedBuffer.autoCrafting ? false : p_75145_1_.getDistanceSq((double)tile.xCoord + 0.5D, (double)tile.yCoord + 0.5D, (double)tile.zCoord + 0.5D) <= 64.0D;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = this.getSlot(p_82846_2_);

        if (slot != null && slot.getHasStack() && p_82846_2_ != 46)
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (p_82846_2_ >= 10 && p_82846_2_ < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (p_82846_2_ >= 37 && p_82846_2_ < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }

        return itemstack;
    }

    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
    {
        return p_94530_2_.inventory != this.craftResult && super.func_94530_a(p_94530_1_, p_94530_2_);
    }

}
