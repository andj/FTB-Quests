package dev.ftb.mods.ftbquests.integration.gamestages;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardAutoClaim;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author LatvianModder
 */
public class GameStageReward extends Reward {
	public String stage = "";
	public boolean remove = false;

	public GameStageReward(Quest quest) {
		super(quest);
		autoclaim = RewardAutoClaim.INVISIBLE;
	}

	@Override
	public RewardType getType() {
		return GameStagesIntegration.GAMESTAGE_REWARD;
	}

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);
		nbt.putString("stage", stage);

		if (remove) {
			nbt.putBoolean("remove", true);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);
		stage = nbt.getString("stage");
		remove = nbt.getBoolean("remove");
	}

	@Override
	public void writeNetData(FriendlyByteBuf buffer) {
		super.writeNetData(buffer);
		buffer.writeUtf(stage, Short.MAX_VALUE);
		buffer.writeBoolean(remove);
	}

	@Override
	public void readNetData(FriendlyByteBuf buffer) {
		super.readNetData(buffer);
		stage = buffer.readUtf(Short.MAX_VALUE);
		remove = buffer.readBoolean();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getConfig(ConfigGroup config) {
		super.getConfig(config);
		config.addString("stage", stage, v -> stage = v, "").setNameKey("ftbquests.reward.ftbquests.gamestage");
		config.addBool("remove", remove, v -> remove = v, false);
	}

	@Override
	public void claim(ServerPlayer player, boolean notify) {
		if (remove) {
			GameStageHelper.removeStage(player, stage);
		} else {
			GameStageHelper.addStage(player, stage);
		}

		GameStageHelper.syncPlayer(player);

		if (notify) {
			if (remove) {
				player.sendMessage(new TranslatableComponent("commands.gamestage.remove.target", stage), Util.NIL_UUID);
			} else {
				player.sendMessage(new TranslatableComponent("commands.gamestage.add.target", stage), Util.NIL_UUID);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public MutableComponent getAltTitle() {
		return new TranslatableComponent("ftbquests.reward.ftbquests.gamestage").append(": ").append(new TextComponent(stage).withStyle(ChatFormatting.YELLOW));
	}
}