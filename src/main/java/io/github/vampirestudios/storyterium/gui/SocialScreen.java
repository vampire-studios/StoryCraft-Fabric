package io.github.vampirestudios.storyterium.gui;

import io.github.vampirestudios.questing_api.QuestManager;
import io.github.vampirestudios.questing_api.api.Quest;
import io.github.vampirestudios.storyterium.entity.FamiliarsEntity;
import io.github.vampirestudios.storyterium.gui.widget.QuestButtonList;
import io.github.vampirestudios.storyterium.main.Storyterium;
import io.github.vampirestudios.vampirelib.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SocialScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(Storyterium.MOD_ID, "textures/gui/quest_villager.png");
    private final FamiliarsEntity target;
    private QuestButtonList questButtonList;

    public SocialScreen(FamiliarsEntity entity) {
        super(new TranslatableText("storyterium.quest_screen"));
        this.target = entity;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        super.init(minecraftClient_1, int_1, int_2);
        questButtonList = new QuestButtonList(client, this.width, this.height, 32, this.height - 32, 25);
        questButtonList.method_31323(false);
        QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(target.getFamiliarsProfession().getProfession())).forEach(quest ->
                questButtonList.addSingleQuestEntry(new QuestButtonList.QuestEntry(quest)));
        this.children.add(questButtonList);
        /*for (SocialVillagerQuestButton questButton : questButtons) {
            this.addButton(questButton);

            getRewardButton = new ButtonWidget(300, 130, 70, 20, new LiteralText("Get Reward"), var1 -> {
                System.out.println(Registry.ITEM.getId(questButton.quest.getReward(1).getItemReward().getItem()));
                talker.dropItem(questButton.quest.getReward(1).getItemReward().getItem(), 10);
                talker.sendMessage(new LiteralText("Testing"), false);
            });
        }*/
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        ScreenDrawing.rect(TEXTURE, 102, 44, 275, 166, 0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);

        String namePlusProfession = String.format("%s - %s", this.target.getCustomName().asString(), this.target.getFamiliarsProfession().getProfession());
        this.textRenderer.draw(stack, namePlusProfession, 211, 51, 4210752);

        String questTitle = "Quests";
        this.textRenderer.draw(stack, questTitle, 100 + this.textRenderer.getWidth(questTitle), 51, 4210752);

        questButtonList.render(stack, mouseX, mouseY, delta);
    }

    public FamiliarsEntity getTarget() {
        return this.target;
    }

    public static class SocialVillagerQuestButton extends ButtonWidget {
        private Quest quest;

        SocialVillagerQuestButton(int x, int y, Quest quest) {
            super(x, y, 89, 20, new LiteralText( quest != null ? quest.getTask(1).getName() : ""), ButtonWidget::onPress);
            setQuest(quest);
        }

        @Override
        public void onPress() {
            System.out.println(quest.getTask(1).getName());
        }

        public Quest getQuest() {
            return quest;
        }

        void setQuest(Quest quest) {
            this.quest = quest;
            visible = quest != null;
            setMessage(new LiteralText(quest != null ? quest.getTask(1).getName() : ""));
        }

    }

}