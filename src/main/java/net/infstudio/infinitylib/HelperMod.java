package net.infstudio.infinitylib;

import net.infstudio.infinitylib.api.utils.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.infstudio.infinitylib.common.registry.RegistryBufferManager;
import net.infstudio.infinitylib.entity.IPropertiesManager;
import net.infstudio.infinitylib.login.restriction.ModRestriction;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;

@Mod(modid = HelperMod.MODID, name = HelperMod.NAME, version = HelperMod.VERSION, useMetadata = true)
public class HelperMod
{
	public static final String MODID = "helper", NAME = "Helper", VERSION = "beta 0.8";

	@Mod.Metadata(MODID)
	public static ModMetadata metadata;

	@Mod.Instance(MODID)
	public static HelperMod instance;

	public static Logger LOG;

	@SidedProxy(modId = MODID, serverSide = "CommonProxy", clientSide = "ClientProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void construct(FMLConstructionEvent event)
	{
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		List<String> inputArguments = bean.getInputArguments();
		System.out.println(inputArguments);
		RegistryHelper.INSTANCE.container = Loader.instance().activeModContainer();
		RegistryBufferManager.instance().load(event.getASMHarvestedData());
		RegistryBufferManager.instance().invoke(event);
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		LOG = event.getModLog();
		if (Environment.debug())
			LOG.info("Detected that this is a development environment. Debug mode on.");
		RegistryBufferManager.instance().invoke(event);
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		metadata.autogenerated = false;
		metadata.description = "A mod that can simplify the mod registrant.";
		metadata.url = "https://github.com/InfinityStudio/InfinityLib";
		metadata.authorList.add(0, "ci010");
		metadata.updateUrl = "https://github.com/InfinityStudio/InfinityLib/releases";

		//TODO remove this test code
//		RegistryHelper.INSTANCE.registerSittableBlock(Blocks.brick_stairs);
		RegistryBufferManager.instance().invoke(event);
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {RegistryBufferManager.instance().invoke(event);}

	@Mod.EventHandler
	public void complete(FMLLoadCompleteEvent event)
	{
		RegistryBufferManager.instance().invoke(event);
		RegistryHelper.INSTANCE.close();
	}

	@Mod.EventHandler
	void serverAboutStart(FMLServerAboutToStartEvent event) {RegistryBufferManager.instance().invoke(event);}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		RegistryBufferManager.instance().invoke(event);
	}

	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{
		if (IPropertiesManager.enable())
			MinecraftServer.getServer().registerTickable(IPropertiesManager.instance());
		RegistryBufferManager.instance().invoke(event);
	}

	@NetworkCheckHandler
	public boolean acceptModList(Map<String, String> modList, Side side)
	{
		return ModRestriction.acceptModList(modList, side);
	}
}
