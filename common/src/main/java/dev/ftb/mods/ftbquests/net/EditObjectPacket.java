package dev.ftb.mods.ftbquests.net;

import dev.ftb.mods.ftblibrary.net.snm.BaseC2SPacket;
import dev.ftb.mods.ftblibrary.net.snm.PacketID;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.integration.jei.FTBQuestsJEIHelper;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.util.NetUtils;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class EditObjectPacket extends BaseC2SPacket {
	private final long id;
	private final CompoundTag nbt;

	EditObjectPacket(FriendlyByteBuf buffer) {
		id = buffer.readLong();
		nbt = buffer.readNbt();
	}

	public EditObjectPacket(QuestObjectBase o) {
		id = o.id;
		nbt = new CompoundTag();
		o.writeData(nbt);
		FTBQuestsJEIHelper.refresh(o);
		ClientQuestFile.INSTANCE.clearCachedData();
		o.editedFromGUI();
	}

	@Override
	public PacketID getId() {
		return FTBQuestsNetHandler.EDIT_OBJECT;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeLong(id);
		buffer.writeNbt(nbt);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		if (NetUtils.canEdit(context)) {
			QuestObjectBase object = ServerQuestFile.INSTANCE.getBase(id);

			if (object != null) {
				object.readData(nbt);
				ServerQuestFile.INSTANCE.clearCachedData();
				ServerQuestFile.INSTANCE.save();
				new EditObjectResponsePacket(object).sendToAll(context.getPlayer().getServer());
			}
		}
	}
}