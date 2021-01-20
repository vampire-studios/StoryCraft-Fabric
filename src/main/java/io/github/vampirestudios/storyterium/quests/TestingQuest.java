package io.github.vampirestudios.storyterium.quests;

import com.google.common.collect.ImmutableList;
import io.github.vampirestudios.questing_api.api.Quest;
import io.github.vampirestudios.questing_api.api.QuestReward;
import io.github.vampirestudios.questing_api.api.QuestTask;
import io.github.vampirestudios.storyterium.main.Storyterium;
import io.github.vampirestudios.storyterium.socialVillager.Professions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TestingQuest extends Quest {

    public TestingQuest() {
        super(
            new Identifier(Storyterium.MOD_ID, "testing_idk"),
            Professions.ARCHITECT.getName(),
            new ItemStack(Items.BEEF),
            ImmutableList.of(
                new QuestTask("Test 1", "Testing", 0, 100),
                new QuestTask("Test 2", "Testing", 0, 100),
                new QuestTask("Test 3", "Testing", 0, 100),
                new QuestTask("Test 4", "Testing", 0, 100),
                new QuestTask("Test 5", "Testing", 0, 100),
                new QuestTask("Test 6", "Testing", 0, 100)
            ),
            ImmutableList.of(
                new QuestReward(new ItemStack(Items.MUSIC_DISC_11, 10), 10)
            )
        );
    }

}
