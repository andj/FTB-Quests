package dev.ftb.mods.ftbquests.integration.gamestages;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ChapterGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import dev.ftb.mods.ftbquests.quest.reward.RewardTypes;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration {
	public static final TaskType GAMESTAGE_TASK = TaskTypes.register(new ResourceLocation(FTBQuests.MOD_ID, "gamestage"), GameStageTask::new, () -> Icons.CONTROLLER);
	public static final RewardType GAMESTAGE_REWARD = RewardTypes.register(new ResourceLocation(FTBQuests.MOD_ID, "gamestage"), GameStageReward::new, () -> Icons.CONTROLLER);
	public static GameStageHelperCommon proxy;

	public void init() {
		proxy = DistExecutor.safeRunForDist(() -> GameStageHelperClient::new, () -> GameStageHelperCommon::new);
		MinecraftForge.EVENT_BUS.register(GameStagesIntegration.class);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayer) {
			checkStages((ServerPlayer) event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onGameStageAdded(GameStageEvent.Added event) {
		if (event.getPlayer() instanceof ServerPlayer) {
			checkStages((ServerPlayer) event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onGameStageRemoved(GameStageEvent.Removed event) {
		if (event.getPlayer() instanceof ServerPlayer) {
			checkStages((ServerPlayer) event.getPlayer());
		}
	}

	public static void checkStages(ServerPlayer player) {
		TeamData data = ServerQuestFile.INSTANCE == null || (player instanceof FakePlayer) ? null : ServerQuestFile.INSTANCE.getData(player);

		if (data == null || data.isLocked()) {
			return;
		}

		TeamData.currentPlayer = player;

		for (ChapterGroup group : ServerQuestFile.INSTANCE.chapterGroups) {
			for (Chapter chapter : group.chapters) {
				for (Quest quest : chapter.quests) {
					if (data.canStartTasks(quest)) {
						for (Task task : quest.tasks) {
							if (task instanceof GameStageTask) {
								task.submitTask(data, player);
							}
						}
					}
				}
			}
		}

		TeamData.currentPlayer = null;
	}

	public static boolean hasStage(Player player, String stage) {
		return proxy.hasStage(player, stage);
	}
}