package io.github.vampirestudios.questing_api.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class Quest {

    protected static Codec<Quest> QUEST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Identifier.CODEC.fieldOf("registry_name").forGetter((quest) -> quest.registry_name),
            Identifier.CODEC.fieldOf("profession").forGetter((quest) -> quest.profession),
            ItemStack.CODEC.fieldOf("icon").forGetter((quest) -> quest.icon),
            QuestTask.QUEST_TASK_CODEC.listOf().fieldOf("task").forGetter((quest) -> quest.task),
            QuestReward.QUEST_REWARD_CODEC.listOf().fieldOf("task").forGetter((quest) -> quest.reward)
    ).apply(instance, Quest::new));

    public Identifier registry_name, profession;
    private ItemStack icon;
    private List<QuestTask> task;
    private List<QuestReward> reward;

    public Quest(Identifier registry_name, Identifier profession, ItemStack icon, List<QuestTask> task, List<QuestReward> reward) {
        this.registry_name = registry_name;
        this.profession = profession;
        this.icon = icon;
        this.task = task;
        this.reward = reward;
    }

    public Identifier getRegistryName() {
        return registry_name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    //this gets the next task for the player to do
    private int last_Task = 0;
    public QuestTask getNextTask()
    {
        last_Task++;
        if(last_Task <= task.size()) last_Task = 0;
        return task.get(last_Task-1);
    }

    public List<QuestTask> getTasks() {
        return task;
    }

    //this will get the next reward on the list.
    private int last_Reward = 0;
    public QuestReward getNextReward()
    {
        last_Reward++;
        if(last_Reward <= reward.size()) last_Reward = 0;
        return reward.get(last_Reward-1);
    }

    public QuestTask getTask(int number) {
        return task.get(number);
    }

    public QuestReward getReward(int number) {
        return reward.get(number);
    }

    public Identifier getProfession() {
        return profession;
    }

}