package io.github.vampirestudios.questing_api.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class QuestTask {

    protected static Codec<QuestTask> QUEST_TASK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.STRING.fieldOf("name").forGetter((quest) -> quest.name),
                Codec.STRING.fieldOf("description").forGetter((quest) -> quest.description),
                Codec.INT.fieldOf("current_progress").forGetter((quest) -> quest.current_progress),
                Codec.INT.fieldOf("max_progress").forGetter((quest) -> quest.max_progress)
        ).apply(instance, QuestTask::new);
    });

    private String name, description;
    private int current_progress, max_progress;

    public QuestTask(String name, String description, int current_progress, int max_progress) {
        this.name = name;
        this.description = description;
        this.current_progress = current_progress;
        this.max_progress = max_progress;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCurrentProgress() {
        return current_progress;
    }

    public int getMaxProgress() {
        return max_progress;
    }

}