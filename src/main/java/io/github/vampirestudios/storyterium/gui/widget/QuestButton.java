package io.github.vampirestudios.storyterium.gui.widget;

import io.github.vampirestudios.questing_api.api.Quest;
import me.shedaniel.clothconfig2.api.Expandable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class QuestButton extends ButtonWidget implements Expandable {

	private boolean expanded = false;
	private Quest quest = null;

	public QuestButton(int x, int y, int width, int height, Text message, Quest in_Quest) {
		super(x, y, width, height, message, ButtonWidget::onPress);
		this.quest = in_Quest;
	}

	@Override
	public void onPress() {
		super.onPress();
		setExpanded(expanded);
	}

	@Override
	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void setExpanded(boolean b) {
		expanded = b;
	}

	public Quest getQuest() {
		return quest;
	}

}
