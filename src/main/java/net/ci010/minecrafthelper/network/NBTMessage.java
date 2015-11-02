package net.ci010.minecrafthelper.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NBTMessage implements IMessage
{
	public NBTTagCompound data;
	
	public NBTMessage()
	{}
	
	public NBTMessage(NBTTagCompound data)
	{
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, data);
	}
}