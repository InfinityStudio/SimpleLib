package net.ci010.minecrafthelper.abstracts;

import net.minecraft.creativetab.CreativeTabs;

/**
 * @author CI010
 */
public abstract class MinecraftComponent<T>
{
	private T component;

	public MinecraftComponent(T wrap)
	{
		this.component = wrap;
	}

	public T getComponent()
	{
		return component;
	}

	public abstract T setUnlocalizedName(String name);

	public abstract T setCreativeTab(CreativeTabs tab);
}