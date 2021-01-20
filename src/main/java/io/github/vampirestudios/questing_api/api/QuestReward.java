package io.github.vampirestudios.questing_api.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

public class QuestReward {

    protected static Codec<QuestReward> QUEST_REWARD_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ItemStack.CODEC.fieldOf("reward").forGetter(quest -> quest.reward),
            Codec.INT.fieldOf("xpReward").forGetter((quest) -> quest.xpReward)
    ).apply(instance, QuestReward::new));

    private ItemStack reward;
    private int xpReward;

    public QuestReward(ItemStack reward, int xpReward) {
        this.reward = reward;
        this.xpReward = xpReward;
    }

    public ItemStack getItemReward() {
        return reward;
    }

    public int getXPReward() {
        return xpReward;
    }

}