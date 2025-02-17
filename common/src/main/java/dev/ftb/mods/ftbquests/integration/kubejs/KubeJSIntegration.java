package dev.ftb.mods.ftbquests.integration.kubejs;

import dev.ftb.mods.ftbquests.events.CustomRewardEvent;
import dev.ftb.mods.ftbquests.events.CustomTaskEvent;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.events.ObjectStartedEvent;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.world.InteractionResult;

/**
 * @author LatvianModder
 */
public class KubeJSIntegration {
	public static void init() {
		BindingsEvent.EVENT.register(KubeJSIntegration::registerBindings);
		AttachPlayerDataEvent.EVENT.register(KubeJSIntegration::attachPlayerData);
		CustomTaskEvent.EVENT.register(KubeJSIntegration::onCustomTask);
		CustomRewardEvent.EVENT.register(KubeJSIntegration::onCustomReward);
		ObjectCompletedEvent.GENERIC.register(KubeJSIntegration::onCompleted);
		ObjectStartedEvent.GENERIC.register(KubeJSIntegration::onStarted);
	}

	//@SubscribeEvent
	//public static void registerDocumentation(DocumentationEvent event)
	//{
	//	event.registerAttachedData(DataType.PLAYER, "ftbquests", FTBQuestsKubeJSPlayerData.class);
	//
	//	event.registerEvent("ftbquests.custom_task", CustomTaskEventJS.class).doubleParam("id").canCancel();
	//	event.registerEvent("ftbquests.custom_reward", CustomRewardEventJS.class).doubleParam("id").canCancel();
	//	event.registerEvent("ftbquests.completed", QuestObjectCompletedEventJS.class).doubleParam("id|tag");
	//	event.registerEvent("ftbquests.started", TaskStartedEventJS.class).doubleParam("id|tag");
	//}

	public static void registerBindings(BindingsEvent event) {
		event.add("FTBQuests", FTBQuestsKubeJSWrapper.INSTANCE);
	}

	public static void attachPlayerData(AttachPlayerDataEvent event) {
		event.add("ftbquests", new FTBQuestsKubeJSPlayerData(event.getParent()));
	}

	public static InteractionResult onCustomTask(CustomTaskEvent event) {
		if (new CustomTaskEventJS(event).post(ScriptType.SERVER, "ftbquests.custom_task", event.getTask().toString())) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public static InteractionResult onCustomReward(CustomRewardEvent event) {
		if (new CustomRewardEventJS(event).post(ScriptType.SERVER, "ftbquests.custom_reward", event.getReward().toString())) {
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public static InteractionResult onCompleted(ObjectCompletedEvent<?> event) {
		if (event.getData().file.isServerSide()) {
			QuestObjectCompletedEventJS e = new QuestObjectCompletedEventJS(event);
			e.post(ScriptType.SERVER, "ftbquests.completed", event.getObject().getCodeString());

			for (String tag : event.getObject().getTags()) {
				e.post(ScriptType.SERVER, "ftbquests.completed." + tag);
			}
		}

		return InteractionResult.PASS;
	}

	public static InteractionResult onStarted(ObjectStartedEvent<?> event) {
		if (event.getData().file.isServerSide()) {
			QuestObjectStartedEventJS e = new QuestObjectStartedEventJS(event);
			e.post(ScriptType.SERVER, "ftbquests.started", event.getObject().getCodeString());

			for (String tag : event.getObject().getTags()) {
				e.post(ScriptType.SERVER, "ftbquests.started." + tag);
			}
		}

		return InteractionResult.PASS;
	}
}