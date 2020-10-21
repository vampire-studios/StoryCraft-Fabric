package io.github.paradoxicalblock.storycraft.main;

import io.github.paradoxicalblock.questing_api.QuestDataManager;
import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.storycraft.entity.EntityUtils;
import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import io.github.vampirestudios.vampirelib.utils.registry.EntityRegistryBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StoryCraft implements ModInitializer {

    public static final String MOD_ID = "storycraft";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final QuestDataManager QUEST_DATA_MANAGER = new QuestDataManager();
    public static EntityType<FamiliarsEntity> FAMILIARS;

    @Override
    public void onInitialize() {
        QUEST_DATA_MANAGER.registerReloadListener();
        FabricLoader.getInstance().getEntrypoints("storycraft:quests", Quest.class).forEach(QuestManager::registerQuest);
        FAMILIARS = EntityRegistryBuilder
                .<FamiliarsEntity>createBuilder(new Identifier(MOD_ID, "familiars"))
                .entity((var1, var2) -> new FamiliarsEntity(var2))
                .group(SpawnGroup.CREATURE)
                .egg(5651507, 12422002)
                .dimensions(EntityDimensions.fixed(0.5F, 1.95F))
                .tracker(64, 3, false)
                .build();
        FabricDefaultAttributeRegistry.register(FAMILIARS, EntityUtils.createGenericEntityAttributes());
    }
}
