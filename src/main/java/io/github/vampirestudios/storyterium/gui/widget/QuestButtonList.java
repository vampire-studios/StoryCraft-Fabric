package io.github.vampirestudios.storyterium.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.vampirestudios.questing_api.api.Quest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class QuestButtonList extends ElementListWidget<QuestButtonList.ButtonEntry> {
   public QuestButtonList(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
      super(minecraftClient, i, j, k, l, m);
      this.centerListVertically = false;
   }

   public int addSingleQuestEntry(QuestEntry entry) {
      return this.addEntry(QuestButtonList.ButtonEntry.create(this.width, this.height, entry));
   }

   public void addQuestEntry(QuestEntry firstOption, @Nullable QuestEntry secondOption) {
      this.addEntry(QuestButtonList.ButtonEntry.create(this.width, this.height, firstOption, secondOption));
   }

   public void addAll(QuestEntry[] options) {
      for(int i = 0; i < options.length; i += 2) {
         this.addQuestEntry(options[i], i < options.length - 1 ? options[i + 1] : null);
      }
   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPositionX() {
      return super.getScrollbarPositionX() + 32;
   }

   @Nullable
   public AbstractButtonWidget getButtonFor(Quest quest) {

      for (ButtonEntry buttonEntry : this.children()) {
         for (AbstractButtonWidget abstractButtonWidget : buttonEntry.buttons) {
            return abstractButtonWidget;
         }
      }

      return null;
   }

   public Optional<AbstractButtonWidget> getHoveredButton(double mouseX, double mouseY) {
      for (ButtonEntry buttonEntry : this.children()) {
         for (AbstractButtonWidget abstractButtonWidget : buttonEntry.buttons) {
            if (abstractButtonWidget.isMouseOver(mouseX, mouseY)) {
               return Optional.of(abstractButtonWidget);
            }
         }
      }

      return Optional.empty();
   }

   @Override
   public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices);

      int k = this.getRowLeft();
      int l = this.top + 4 - (int)this.getScrollAmount();

      this.renderList(matrices, k, l, mouseX, mouseY, delta);

      RenderSystem.enableTexture();
      RenderSystem.shadeModel(7424);
      RenderSystem.enableAlphaTest();
      RenderSystem.disableBlend();
   }

   @Environment(EnvType.CLIENT)
   public static class ButtonEntry extends ElementListWidget.Entry<QuestButtonList.ButtonEntry> {
      private final List<AbstractButtonWidget> buttons;

      private ButtonEntry(List<AbstractButtonWidget> buttons) {
         this.buttons = buttons;
      }

      public static QuestButtonList.ButtonEntry create(int width, int height, QuestEntry entry) {
         return new QuestButtonList.ButtonEntry(ImmutableList.of(entry.createButton(0, 150, width / 2 - 155, height / 2)));
      }

      public static QuestButtonList.ButtonEntry create(int width, int height, QuestEntry firstEntry, QuestEntry secondEntry) {
         AbstractButtonWidget abstractButtonWidget = firstEntry.createButton(0, 150, width / 2 - 155, height);
         return secondEntry == null ? new QuestButtonList.ButtonEntry(ImmutableList.of(abstractButtonWidget)) : new QuestButtonList.ButtonEntry(ImmutableList.of(abstractButtonWidget, secondEntry.createButton(0, 150, width / 2 - 155 + 160, height / 2)));
      }

      public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         this.buttons.forEach((button) -> {
            button.y = y;
            button.render(matrices, mouseX, mouseY, tickDelta);
         });
      }

      public List<? extends Element> children() {
         return this.buttons;
      }
   }

   public static class QuestEntry {

      public Quest quest;

      public QuestEntry(Quest quest) {
         this.quest = quest;
      }

      public Quest getQuest() {
         return quest;
      }

      public AbstractButtonWidget createButton(int x, int y, int width, int height) {
         return new ButtonWidget(x, y, width, height, new LiteralText(quest.registry_name.toString()), button -> {
            System.out.println(quest.registry_name.toString());
         });
      }

   }
}
