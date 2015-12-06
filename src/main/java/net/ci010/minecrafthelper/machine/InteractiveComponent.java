package net.ci010.minecrafthelper.machine;

import com.google.common.collect.Lists;
import net.ci010.minecrafthelper.HelperMod;
import net.ci010.minecrafthelper.gui.*;
import net.ci010.minecrafthelper.util.GuiUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

/**
 * @author ci010
 */
public abstract class InteractiveComponent implements ContainerProvider
{
	protected String name;
	protected Class<? extends Process>[] clz;
	protected InteractiveComponentInfo.ProcessInfo[] info;
	protected InteractiveComponentInfo.SlotInfo[] slotInfo;
	@SideOnly(Side.CLIENT)
	protected boolean adjusted;
	@SideOnly(Side.CLIENT)
	protected GuiComponent[] front, back;
	protected int id, numOfProcess, numOfSync, numOfInt, numOfStack;

	protected InteractiveComponent(InteractiveComponentInfo info)
	{
		this.id = GuiHandler.addContainerProvider(this);
		this.name = info.name;

		this.numOfProcess = info.processInfoMap.size();
		this.clz = new Class[numOfProcess];
		this.info = new InteractiveComponentInfo.ProcessInfo[numOfProcess];

		int idx = -1;
		numOfSync = 0;
		for (Map.Entry<Class<? extends Process>, InteractiveComponentInfo.ProcessInfo> entry : info.processInfoMap.entrySet())
		{
			InteractiveComponentInfo.ProcessInfo temp = entry.getValue();
			numOfSync += temp.sync.size();
			numOfInt += temp.integers.size();
			numOfStack += temp.stacks.size();
			numOfSync += temp.sync.size();
			this.clz[++idx] = entry.getKey();
			this.info[idx] = temp;
		}

		slotInfo = info.slotInfos.toArray(new InteractiveComponentInfo.SlotInfo[info.slotInfos.size()]);

		if (HelperMod.proxy.isClient())
		{
			List<GuiComponent> front = Lists.newArrayList(), back = Lists.newArrayList();
			for (GuiComponent component : info.gui)
				if (component.type() == GuiComponent.Type.back)
					back.add(component);
				else
					front.add(component);

			for (InteractiveComponentInfo.SlotInfo s : slotInfo)
				back.add(new TileTexture(GuiUtil.slot, s.x, s.y));
			int index, magic = 8;// 8 is default
			for (index = 0; index < 9; ++index)
				back.add(new TileTexture(GuiUtil.slot, magic + index * 18, 142));
			for (index = 0; index < 3; ++index)
				for (int offset = 0; offset < 9; ++offset)
					back.add(new TileTexture(GuiUtil.slot, magic + offset * 18, 84 + index * 18));
			this.front = front.toArray(new GuiComponent[front.size()]);
			this.back = back.toArray(new GuiComponent[back.size()]);
		}
	}

	public String getName()
	{
		return this.name;
	}

	public void interactWith(EntityPlayer player, BlockPos pos)
	{
		player.openGui(HelperMod.instance, this.id, player.worldObj, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGuiContainer(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new GuiContainerWrap(getContainer(ID, player, world, x, y, z))
		{
			@Override
			public void initGui()
			{
				super.initGui();
				if (!adjusted)
				{
					for (GuiComponent comp : back)
						comp.setPos(this.guiLeft + comp.getX() - 1, this.guiTop + comp.getY() - 1);
					for (GuiComponent comp : front)
						comp.setPos(this.guiLeft + comp.getX() - 1, this.guiTop + comp.getY() - 1);
					adjusted = true;
				}
			}

			@Override
			protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
			{
				for (Drawable guiComponent : front)
					guiComponent.draw();

			}

			@Override
			protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
			{
				for (Drawable guiComponent : back)
					guiComponent.draw();
			}

			@Override
			public void drawScreen(int mouseX, int mouseY, float partialTicks)
			{
				super.drawScreen(mouseX, mouseY, partialTicks);
				current = null;
				for (GuiComponent component : back)
					if (this.include(component, mouseX, mouseY))
					{
						this.current = component;
						break;
					}
				if (current == null)
					for (GuiComponent component : front)
						if (this.include(component, mouseX, mouseY))
						{
							this.current = component;
							return;
						}
				if (current != null)
					if (current.hasMouseListener())
						current.getMouseListener().onHovered();
			}
		};
	}
}