package io.github.vampirestudios.storyterium.gui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.vampirestudios.storyterium.entity.FamiliarsEntity;
import io.github.vampirestudios.storyterium.main.Storyterium;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class FamiliarsScreen extends LightweightGuiDescription {

    private FamiliarsEntity target;

    public WPlainPanel panel = new WPlainPanel();

    private static final Identifier PORTAL1 = new Identifier("storyterium:portal.png");
    private static final Identifier PORTAL2 = new Identifier("storyterium:portal2.png");

    @Environment(EnvType.CLIENT)
    public static final BackgroundPainter PANEL = (x, y, panel)->{
        ScreenDrawing.drawBeveledPanel(x-1, y-1, panel.getWidth()+2, panel.getHeight()+2);
    };

    public FamiliarsScreen(FamiliarsEntity entity) {
        this.target = entity;

        this.setRootPanel(panel);
        panel.setSize(275, 166);

        WLabel questsTitle = new WLabel("Quests");
        questsTitle.setColor(0x000000, 0xFFFFFF);
        panel.add(questsTitle, 1 + questsTitle.getWidth(), 6);

        WLabel namePlusProfessionWidget = new WLabel(String.format("%s - %s", this.target.getCustomName().asString(), WordUtils.capitalizeFully(this.target.getFamiliarsProfession().getProfession().getPath())));
        namePlusProfessionWidget.setColor(0x000000, 0xFFFFFF);
        panel.add(namePlusProfessionWidget, 93 + namePlusProfessionWidget.getWidth(), 7);

        ArrayList<String> data = new ArrayList<>();
        data.add("Wolfram Alpha");
        data.add("Strange Home");
        data.add("Nether Base");
        data.add("Death");
        data.add("Cake");
        data.add("Mushroom Island");
        data.add("A List Item");
        data.add("Notes");
        data.add("Slime Island");

        BiConsumer<String, PortalDestination> configurator = (String s, PortalDestination destination) -> {
            destination.label.setText(new LiteralText(s));

            int hash = s.hashCode();
            Identifier sprite = ((hash & 0x01) == 0) ? PORTAL1 : PORTAL2;
            destination.sprite.setImage(sprite);

            int cost = (hash >> 1) & 0x2FF;
            destination.cost.setText(new LiteralText(""+cost+" XP"));
        };
        WListPanel<String, PortalDestination> list = new WListPanel<String, PortalDestination>(data, PortalDestination::new, configurator);
        list.setListItemHeight(2*18);
        list.setBackgroundPainter(PANEL);
        panel.add(list, 5, 18, 95, 140);

        panel.add(new WButton(new LiteralText("Teleport")), 3,8,4,1);

        /*Quest[] quests = QuestManager.getQuests().stream().filter(quest -> quest.profession.equals(target.getFamiliarsProfession().getProfession())).toArray(Quest[]::new);
        for (Quest quest : quests) {
            SocialVillagerQuestButton questButton = new SocialVillagerQuestButton(quest);
            panel.add(questButton, 10 + 2, 10);
        }*/

        /*WLabel tasksTitle = new WLabel("Tasks");
        tasksTitle.setColor(0x000000, 0xFFFFFF);
        panel.add(tasksTitle, 1 + tasksTitle.getWidth(), 6);*/
    }

    @Override
    public void addPainters() {
        panel.setBackgroundPainter((left, top, panel) -> {
            if (LibGuiClient.config.darkMode) {
                ScreenDrawing.rect(new Identifier(Storyterium.MOD_ID, "textures/gui/quest_villager_dark.png"), 102, 44, 275, 166,
                        0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);
            } else {
                ScreenDrawing.rect(new Identifier(Storyterium.MOD_ID, "textures/gui/quest_villager.png"), 102, 44, 275, 166,
                        0.0F, 0.0F, 0.537F, 0.651F, 0xFFFFFFFF);
            }
        });
    }

    public static class PortalDestination extends WPlainPanel {
        WSprite sprite;
        WLabel label;
        WLabel cost;

        public PortalDestination() {
            sprite = new WSprite(new Identifier("storyterium:portal"));
            this.add(sprite, 2, 2, 18, 18);
            label = new WLabel("Foo");
            this.add(label, 18+ 4, 2, 5*18, 18);
            cost = new WLabel("1000 Xp");
            this.add(cost, 2, 20, 6*18, 18);

            this.setSize(7*18, 2*18);

            this.setBackgroundPainter(PANEL); //Would fail on a serverside gui
        }
    }

}