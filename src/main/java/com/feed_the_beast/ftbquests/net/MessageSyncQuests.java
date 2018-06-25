package com.feed_the_beast.ftbquests.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.client.FTBQuestsClient;
import com.feed_the_beast.ftbquests.quest.tasks.QuestTaskKey;
import com.google.gson.JsonElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class MessageSyncQuests extends MessageToClient
{
	private JsonElement json;
	private Map<QuestTaskKey, Integer> progress;

	public MessageSyncQuests()
	{
	}

	public MessageSyncQuests(JsonElement e, Map<QuestTaskKey, Integer> p)
	{
		json = e;
		progress = p;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBQuestsNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeJson(json);
		data.writeMap(progress, QuestTaskKey.SERIALIZER, DataOut.INT);
	}

	@Override
	public void readData(DataIn data)
	{
		json = data.readJson();
		progress = data.readMap(QuestTaskKey.DESERIALIZER, DataIn.INT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBQuestsClient.loadQuests(json.getAsJsonObject(), progress);
	}
}