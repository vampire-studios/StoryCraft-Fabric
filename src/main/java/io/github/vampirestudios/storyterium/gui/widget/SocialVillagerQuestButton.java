package io.github.vampirestudios.storyterium.gui.widget;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.vampirestudios.questing_api.api.Quest;
import net.minecraft.text.LiteralText;

public class SocialVillagerQuestButton extends WButton {

    private Quest quest;

    public SocialVillagerQuestButton(Quest quest) {
        super(new LiteralText(quest != null ? quest.getTask(1).getName() : ""));
        this.quest = quest;
        setOnClick(() -> System.out.println(quest.getTask(1).getName()));
    }

    @Override
    public void onClick(int x, int y, int button) {
        System.out.println(quest.getTask(1).getName());
    }

    public Quest getQuest() {
        return quest;
    }

}