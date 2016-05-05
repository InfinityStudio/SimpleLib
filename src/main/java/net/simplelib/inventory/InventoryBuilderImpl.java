package net.simplelib.inventory;

import api.simplelib.minecraft.inventory.*;
import api.simplelib.utils.ArrayUtils;
import net.minecraft.util.EnumFacing;
import net.simplelib.common.Vector2i;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * @author ci010
 */
public class InventoryBuilderImpl implements InventoryBuilder
{
	private int currentIdx = 0;
	private InvImpl inv = new InvImpl();
	private EnumMap<EnumFacing, int[]> sideMap = new EnumMap<EnumFacing, int[]>(EnumFacing.class);
	private ArrayList<InventoryElement> elements = new ArrayList<InventoryElement>();

	public InvImpl buildInventory()
	{
		if (currentIdx == 0)
			return null;
		inv.build(sideMap, currentIdx, elements, this.layout);
		return inv;
	}

	public InventorySpace newSpace(int size, EnumFacing facing, InventoryRule rule)
	{
		if (facing != null)
		{
			int[] newArr = new int[size];
			for (int i = 0; i < size; i++)
				newArr[i] = currentIdx + size;
			if (sideMap.containsKey(facing))
				newArr = ArrayUtils.concat(sideMap.get(facing), newArr);
			sideMap.put(facing, newArr);
		}
		InvSpaceImpl space = new InvSpaceImpl(inv, currentIdx, size);
		space.setRule(rule);
		currentIdx += size;
		elements.add(space);
		for (int i = 0; i < size; i++)
			layout.list.add(new Vector2i(0, 18 * i));
		return space;
	}

	public InventorySpace newSpace(int size, EnumFacing facing)
	{
		return this.newSpace(size, facing, null);
	}

	public InventorySlot newSlot(EnumFacing facing)
	{
		return this.newSlot(facing, null);
	}

	public InventorySlot newSlot(EnumFacing facing, InventoryRule rule)
	{
		if (facing != null)
		{
			int[] newArr = new int[]{currentIdx};
			if (sideMap.containsKey(facing))
				newArr = ArrayUtils.concat(sideMap.get(facing), newArr);
			sideMap.put(facing, newArr);
		}
		SlotSpaceImpl slotSpace = new SlotSpaceImpl(inv, currentIdx);
		slotSpace.setRule(rule);
		++currentIdx;
		layout.list.add(Layout.NULL);
		elements.add(slotSpace);
		return slotSpace;
	}

	private class LayoutContainer implements Layout
	{
		private ArrayList<Vector2i> list = new ArrayList<Vector2i>();

		@Override
		public Vector2i getPos(int id)
		{
			return list.get(id);
		}
	}

	private LayoutContainer layout = new LayoutContainer();

	@Override
	public InventoryBuilder allocPos(InventoryElement element, int x, int y)
	{
		layout.list.set(element.id(), new Vector2i(x, y));
		return this;
	}

	@Override
	public InventoryBuilder allocName(InventoryElement element, String name)
	{
		if (element instanceof InvSpaceImpl)
			((InvSpaceImpl) element).setName(name);
		else if (element instanceof SlotSpaceImpl)
			((SlotSpaceImpl) element).setName(name);
		return this;
	}

	@Override
	public InventoryBuilder allocLength(InventorySpace space, int length)
	{
		int id = space.id();
		Vector2i std = layout.list.get(id);
		int currentX = std.getX(), currentY = std.getY(), currentCount = 0;
		for (int i = id; i < space.getSlots(); i++)
		{
			if (currentCount < length)
				currentX += 18;
			else
			{
				currentCount = 0;
				currentX = std.getX();
				currentY += 18;
			}
			++currentCount;
			layout.list.set(i, new Vector2i(currentX, currentY));
		}
		return this;
	}

	@Override
	public int currentSize()
	{
		return elements.size();
	}

	@Override
	public InventoryElement getElement(int i)
	{
		return elements.get(i);
	}
}