package com.tann.dice.screens.dungeon.panels.book.page.helpPage;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;

public enum HelpType {
   Basics(Colours.green),
   Dice(Colours.yellow),
   Rolling(Colours.orange),
   Combat(Colours.red),
   Abilities(Colours.blue),
   Tips(Colours.pink),
   Advanced(Colours.purple),
   Glossary(Colours.grey);

   public final Color background;
   public final Color text;

   private HelpType(Color text) {
      this(Colours.dark, text);
   }

   private HelpType(Color background, Color text) {
      this.background = background;
      this.text = text;
   }

   public String getName() {
      String name = this.name();
      if (this.name().equals("Dice") && com.tann.dice.Main.self().translator.shouldTranslate()) {
         name = "[notranslate]" + com.tann.dice.Main.t("Dice (% menu item %)");
      }

      return name;
   }

   public String getColouredString() {
      String colTag = TextWriter.getTag(this.text);
      String n = this.getName();
      return colTag + n;
   }
}
