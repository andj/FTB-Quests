package dev.ftb.mods.ftbquests.events;

import dev.ftb.mods.ftbquests.quest.task.CustomTask;
import me.shedaniel.architectury.ForgeEvent;
import me.shedaniel.architectury.event.Actor;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

/**
 * @author LatvianModder
 */
@ForgeEvent
public class CustomTaskEvent {
	public static final Event<Actor<CustomTaskEvent>> EVENT = EventFactory.createActorLoop();
	private final CustomTask task;

	public CustomTaskEvent(CustomTask t) {
		task = t;
	}

	public boolean isCancelable() {
		return true;
	}

	public CustomTask getTask() {
		return task;
	}
}