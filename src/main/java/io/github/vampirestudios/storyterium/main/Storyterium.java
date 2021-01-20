package io.github.vampirestudios.storyterium.main;

import io.github.vampirestudios.questing_api.QuestDataManager;
import io.github.vampirestudios.questing_api.QuestManager;
import io.github.vampirestudios.questing_api.api.Quest;
import io.github.vampirestudios.storyterium.entity.EntityUtils;
import io.github.vampirestudios.storyterium.entity.FamiliarsEntity;
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

public class Storyterium implements ModInitializer {

    public static final String MOD_ID = "storyterium";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final QuestDataManager QUEST_DATA_MANAGER = new QuestDataManager();
    public static EntityType<FamiliarsEntity> FAMILIARS;

    @Override
    public void onInitialize() {
        QUEST_DATA_MANAGER.registerReloadListener();
        FabricLoader.getInstance().getEntrypoints("storyterium:quests", Quest.class).forEach(QuestManager::registerQuest);
        FAMILIARS = EntityRegistryBuilder
                .<FamiliarsEntity>createBuilder(new Identifier(MOD_ID, "familiars"))
                .entity((entityType, world) -> new FamiliarsEntity(world))
                .group(SpawnGroup.CREATURE)
                .hasEgg(true)
                .egg(5651507, 12422002)
                .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                .tracker(32, 5, true)
                .build();
        FabricDefaultAttributeRegistry.register(FAMILIARS, EntityUtils.createGenericEntityAttributes());
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
