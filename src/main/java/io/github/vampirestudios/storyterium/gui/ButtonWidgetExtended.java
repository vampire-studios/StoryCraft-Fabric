package io.github.vampirestudios.storyterium.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ButtonWidgetExtended extends ButtonWidget {

    public ButtonWidgetExtended(int int_1, int int_2, int int_3, int int_4, Text string_1, PressAction buttonWidget$PressAction_1) {
        super(int_1, int_2, int_3, int_4, string_1, buttonWidget$PressAction_1);
    }

    public int getHeight() {
        return this.height;
    }

}
