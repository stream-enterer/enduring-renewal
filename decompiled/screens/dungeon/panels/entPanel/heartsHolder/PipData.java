package com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.tp.TP;
import java.util.HashMap;
import java.util.Map;

public class PipData {
   int reds;
   int greens;
   int yellows;
   int purples;
   int max;
   final Map<Integer, TP<TextureRegion, Color>> specials = new HashMap<>();

   public void reset() {
      int def = -1;
      this.reds = def;
      this.greens = def;
      this.yellows = def;
      this.purples = def;
      this.max = def;
      this.specials.clear();
   }

   public void set(int reds, int greens, int yellows, int purples) {
      this.reds = reds;
      this.greens = greens;
      this.yellows = yellows;
      this.purples = purples;
      this.max = reds + greens + yellows + purples;
   }

   public void addSpecialPip(int hpIndex, TP<TextureRegion, Color> pipTannple) {
      if (this.specials.get(hpIndex) == null) {
         this.specials.put(hpIndex, pipTannple);
      } else {
         this.specials.put(hpIndex, new TP<>(Images.hp_glider, Colours.pink));
      }
   }

   public Color getCol(int i) {
      TP<TextureRegion, Color> spec = this.specials.get(i);
      i -= this.reds;
      if (i < 0) {
         return spec == null ? Colours.red : spec.b;
      } else {
         i -= this.greens;
         if (i < 0) {
            return OptionUtils.poisonCol();
         } else {
            i -= this.yellows;
            if (i < 0) {
               return Colours.yellow;
            } else {
               i -= this.purples;
               return i < 0 ? Colours.purple : Colours.pink;
            }
         }
      }
   }

   public TextureRegion getImage(int i, HPHolder.PipSize pipSize) {
      if (pipSize == HPHolder.PipSize.normal && this.specials.get(i) != null) {
         return (TextureRegion)this.specials.get(i).a;
      } else {
         if (this.getCol(i) == Colours.purple) {
            switch (pipSize) {
               case normal:
                  return Images.hp_empty;
               case little:
                  return Images.hp_empty_small;
               case pixel:
                  return pipSize.img;
            }
         }

         return pipSize.img;
      }
   }
}
