package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.List;

public abstract class HpGrid {
   public static Actor make(int hp, int incoming, int poison, int maxHp, TextureRegion[] specials, Color[] specialCols, List<Actor> startBits) {
      return make(hp, incoming, poison, maxHp, specials, specialCols, null, startBits);
   }

   public static Actor make(
      int hp, int incoming, int poison, int maxHp, TextureRegion[] specials, Color[] specialCols, List<Actor> startBits, List<Actor> endBits
   ) {
      if (incoming + poison > hp) {
         return new TextWriter("error hpgrid");
      } else {
         boolean border = false;
         Pixl p = new Pixl(0, border ? 2 : 0);
         int initGap = border ? 1 : 0;
         int intergap = 1;
         if (border) {
            p.border(Colours.grey);
         }

         if (startBits != null) {
            for (Actor startBit : startBits) {
               p.actor(startBit).gap(1);
            }
         }

         for (int i = 0; i < maxHp; i++) {
            boolean empty = i >= hp;
            Color c = Colours.red;
            if (i >= hp - incoming && i <= hp) {
               c = Colours.yellow;
            } else if (i >= hp - poison - incoming && i <= hp) {
               c = OptionUtils.poisonCol();
            }

            if (i % 5 == 0 && i != 0) {
               p.row(intergap).gap(initGap);
            }

            if (specials != null && specials.length > i && specials[i] != null) {
               p.image(specials[i], specialCols[i]);
            } else if (empty) {
               p.image(Images.hp_empty, Colours.purple);
            } else {
               p.image(Images.hp, c);
            }

            if (i < maxHp - 1) {
               p.gap(intergap);
            } else {
               p.gap(initGap);
            }
         }

         if (endBits != null && !endBits.isEmpty()) {
            boolean atEnd = maxHp % 5 == 0;
            if (atEnd) {
               p.row(1).gap(initGap);
            } else {
               p.gap(intergap);
            }

            for (int i = 0; i < endBits.size(); i++) {
               Actor a = endBits.get(i);
               p.actor(a);
               if (i < endBits.size() - 1) {
                  p.gap(1);
               } else {
                  p.gap(initGap);
               }
            }
         }

         p.row(initGap);
         return p.pix(12);
      }
   }

   public static Actor make(int hp, int incoming, int poison, int maxHp) {
      return make(hp, incoming, poison, maxHp, null, null, null);
   }

   public static Actor make(int hp, int maxHp) {
      return make(hp, 0, maxHp);
   }

   public static Actor make(int hp, int incoming, int maxHp) {
      return make(hp, incoming, 0, maxHp);
   }

   public static Actor makeTutorial(int totalWidth, int padding) {
      Actor example = make(8, 2, 10);
      int gap = 3;
      int row = 4;
      int textWidth = (int)(totalWidth - 6 - example.getWidth());
      Pixl p = new Pixl(2, padding);
      p.actor(make(8, 10)).gap(3).text("[notranslate][red]8[grey]/[purple]10[cu] " + com.tann.dice.Main.t("hp"), textWidth);
      p.row(4).actor(make(8, 2, 10)).gap(3).text("[yellow]2 [notranslate][grey]" + com.tann.dice.Main.t("incoming damage"), textWidth);
      p.row(4).actor(make(8, 0, 1, 10)).gap(3).text(OptionUtils.poisonTag() + "1 [notranslate][grey]" + com.tann.dice.Main.t("incoming poison"), textWidth);
      Actor a = p.pix(8);
      a.setName("hp display show thing");
      return a;
   }
}
