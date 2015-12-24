package net.simplelib.interactive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.simplelib.ModNetwork;
import net.simplelib.data.VarInteger;
import net.simplelib.data.VarSync;
import net.simplelib.network.NBTWindowsMessage;

import java.util.List;

/**
 * @author ci010
 */
public class ContainerCommon extends Container implements Inventory.Listener, VarSync.Listener
{
	private ImmutableList<VarInteger> varIntegers;
	private ImmutableList<VarSync> syncs;

	public VarSync getVar(int idx)
	{
		return syncs.get(idx);
	}

	public ContainerCommon load(List<VarSync> syncs)
	{
		this.syncs = ImmutableList.copyOf(syncs);
		return this;
	}

	public ContainerCommon load(ImmutableList<VarInteger> varIntegers)
	{
		this.varIntegers = varIntegers;
		return this;
	}

	public ContainerCommon loadPlayerSlot(InventoryPlayer player)
	{
		int index;
		for (index = 0; index < 9; ++index)
			this.addSlotToContainer(new Slot(player, index, 8 + index * 18, 142));
		for (index = 0; index < 3; ++index)
			for (int offset = 0; offset < 9; ++offset)
				this.addSlotToContainer(new Slot(player, offset + index * 9 + 9, 8 + offset * 18, 84 + index * 18));
		return this;
	}

	public ContainerCommon loadSlots(List<Slot> slots)
	{
		for (Slot slot : slots)
			this.addSlotToContainer(slot);
		return this;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

	public List<EntityPlayerMP> getPlayers()
	{
		//TODO maybe cache?
		List<EntityPlayerMP> players = Lists.newArrayList();
		for (Object o : this.crafters)
			if (o instanceof EntityPlayerMP)
				players.add((EntityPlayerMP) o);
		return players;
	}

	@Override
	public void onCraftGuiOpened(ICrafting iCrafting)
	{
		for (int i = 0; i < varIntegers.size(); ++i)
			iCrafting.sendProgressBarUpdate(this, i, varIntegers.get(i).getData());
		if (iCrafting instanceof EntityPlayerMP)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP) iCrafting;
			for (int num = 0; num < syncs.size(); ++num)
				ModNetwork.instance().sendTo(new NBTWindowsMessage(this.windowId, num, syncs.get(num).getData()),
						playerMP);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value)
	{
		varIntegers.get(id).setData(value);
	}

	@SideOnly(Side.CLIENT)
	public void updateSync(int id, NBTTagCompound tag)
	{
		syncs.get(id).getData().readFromNBT(tag);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		int num;
		for (EntityPlayerMP player : getPlayers())
			if (varIntegers != null)
				for (num = 0; num < varIntegers.size(); ++num)
				{
					VarInteger i = varIntegers.get(num);
					if (i.isDirty())
						player.sendProgressBarUpdate(this, num, i.getData());
				}
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		super.onCraftMatrixChanged(inventoryIn);
		//TODO think about this...
		//this basically is a callback function...
		//called when an inventory changed and this function is expected to update and sync the data to other players.
		//The inventory seems like always be the temporally inventory.
//		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
	}

	@Override
	public void onInventoryChange(IInventory inventoryIn)
	{
		onCraftMatrixChanged(inventoryIn);
	}

	@Override
	public void onChanged(VarSync var)
	{
		for (EntityPlayerMP player : getPlayers())
			ModNetwork.instance().sendTo(new NBTWindowsMessage(this.windowId, syncs.indexOf(var), var.getData()),
					player);
	}
}
