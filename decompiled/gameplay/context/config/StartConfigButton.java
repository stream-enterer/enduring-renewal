package com.tann.dice.gameplay.context.config;

import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class StartConfigButton {
   public static StandardButton make(String text) {
      return make(text, null);
   }

   public static StandardButton make(String text, String extraText) {
      String finalText = text + (extraText == null ? "" : " " + extraText);
      return new StandardButton(finalText, Colours.grey, 50, 20);
   }
}
