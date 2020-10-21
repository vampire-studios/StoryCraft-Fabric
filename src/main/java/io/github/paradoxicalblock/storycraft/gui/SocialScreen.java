package io.github.paradoxicalblock.storycraft.gui;

import io.github.paradoxicalblock.questing_api.QuestManager;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.questing_api.api.QuestTask;
import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import io.github.vampirestudios.vampirelib.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(StoryCraft.MOD_ID, "textures/gui/quest_villager.png");
    private static PlayerEntity talker;
    private FamiliarsEntity target;
    private final SocialVillagerQuestButton[] questButtons;
    private ButtonWidget getRewardButton;

    private Map<HoverChecker, String> hoverChecks = new HashMap<>();

    public SocialScreen(FamiliarsEntity entity, PlayerEntity player) {
        super(new TranslatableText("narrator.screen.title"));
        this.target = entity;
        talker = player;

        Quest[] quests = QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(target.getFamiliarsProfession().getProfession())).toArray(Quest[]::new);
        questButtons = new SocialVillagerQuestButton[7];

        for (int i = 0; i < 7; i++) {
            questButtons[i] = new SocialVillagerQuestButton(107, 62 + i * 20, i < quests.length ? quests[i] : null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        super.init(minecraftClient_1, int_1, int_2);
        for (SocialVillagerQuestButton questButton : questButtons) {
            this.addButton(questButton);

            getRewardButton = new ButtonWidget(300, 130, 70, 20, new LiteralText("Get Reward"), var1 -> {
                System.out.println(Registry.ITEM.getId(questButton.quest.getReward().getItemReward().getItem()));
                talker.dropItem(questButton.quest.getReward().getItemReward().getItem(), 10);
                talker.sendMessage(new LiteralText("Testing"), false);
            });
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        ScreenDrawing.rect(TEXTURE, 102, 44, 275, 166, 0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);

        //blit(x, y, z, u, v, width, height, texHeight, texWidth)
        this.client.getTextureManager().bindTexture(TEXTURE);
		drawTexture(stack, 107, 62, this.getZOffset(), 275.0F, 0.0F, 88, 80, 256, 512);

        String name = String.format("%s %s", this.target.firstName, this.target.lastName);
        String namePlusProfession = String.format("%s - %s", name, this.target.getFamiliarsProfession().getProfession());
        this.textRenderer.draw(stack, namePlusProfession, 211, 51, 4210752);

        String questTitle = "Quests";
        this.textRenderer.draw(stack, questTitle, 100 + this.textRenderer.getWidth(questTitle), 51, 4210752);

        String taskTitle = "Tasks";
        this.textRenderer.draw(stack, taskTitle, 100 + this.textRenderer.getWidth(questTitle), 51, 4210752);

        for (SocialVillagerQuestButton questButton : questButtons) {
            questButton.render(stack, mouseX, mouseY, delta);

            if (questButton.getQuest() != null) {
                for(QuestTask task : questButton.getQuest().getTasks()) {
                    for (int i = 0; i < questButton.getQuest().getTasks().length; i++) {
                        String questName = String.format("Quest: %s", task.getName());
                        this.textRenderer.draw(stack, questName, 140 + this.textRenderer.getWidth(questName), 65 + i, 4210752);

                        drawWrappedString(stack, StringVisitable.plain(task.getDescription()), 210, 80 + i, 153, 4210752);

                        i += 50;
                    }
                }
                getRewardButton.render(stack, mouseX, mouseY, delta);
            } else {
//                String noQuests = "This villager has no quests";
//                this.font.draw(noQuests, 205, 101, 4210752);
            }
        }

    }

    public void drawWrappedString(MatrixStack stack, StringVisitable text, int x, int y, int entryWidth, int color) {
        List<OrderedText> strings = textRenderer.wrapLines(text, entryWidth);
        for (OrderedText string : strings) {
            textRenderer.draw(stack, string.toString(), x, y, color);
            y += textRenderer.fontHeight + 3;
        }
    }

    public FamiliarsEntity getTarget() {
        return this.target;
    }

    public static class SocialVillagerQuestButton extends ButtonWidget {
        private Quest quest;

        SocialVillagerQuestButton(int x, int y, Quest quest) {
            super(x, y, 89, 20,new LiteralText( quest != null ? quest.getTask().getName() : ""), ButtonWidget::onPress);
            setQuest(quest);
        }

        @Override
        public void onPress() {
            System.out.println(quest.getTask().getName());
        }

        public Quest getQuest() {
            return quest;
        }

        void setQuest(Quest quest) {
            this.quest = quest;
            visible = quest != null;
            setMessage(new LiteralText(quest != null ? quest.getTask().getName() : ""));
        }

    }

}