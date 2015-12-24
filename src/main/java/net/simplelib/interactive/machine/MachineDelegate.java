package net.simplelib.interactive.machine;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.simplelib.HelperMod;
import net.simplelib.abstracts.RegistryDelegate;
import net.simplelib.annotation.type.ASMDelegate;
import net.simplelib.annotation.type.ModMachine;

/**
 * @author ci010
 */
@ASMDelegate
public class MachineDelegate extends RegistryDelegate<ModMachine>
{
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if (!MachineInfo.class.isAssignableFrom(this.getAnnotatedClass()))
			throw new IllegalArgumentException("ModMachine annotation should annotate a class extended MachineInfo");
		try
		{
			MachineInfo info = (MachineInfo) this.getAnnotatedClass().newInstance();
			new MachineMetadata(info, this.getModid());
		}
		catch (InstantiationException e)
		{
			HelperMod.LOG.fatal("The class {} should have a constructor without any argument. This machine will not " +
					"be registered.", this.getAnnotatedClass());
		}
		catch (IllegalAccessException e)
		{
			HelperMod.LOG.fatal("The class {} should be assessable. This machine will not be registered.", this.getAnnotatedClass());
		}
	}
}