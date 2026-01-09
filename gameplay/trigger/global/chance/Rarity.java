package com.tann.dice.gameplay.trigger.global.chance;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.TextWriter;

public enum Rarity {
   ONE(1, Colours.dark),
   TWO_THIRDS(0.66F, Colours.yellow),
   HALF(2, Colours.green),
   THIRD(0.33F, Colours.purple),
   FIFTH(5, Colours.grey),
   TENTH(10, Colours.blue),
   TWENTIETH(20, Colours.text),
   FIFTIETH(50, Colours.orange),
   HUNDREDTH(100, Colours.yellow),
   FIVE_HUNDREDTH(500, Colours.text),
   THOUSANDTH(1000, Colours.pink),
   TEN_THOUSANDTH(10000, Colours.brown),
   MILLIONTH(1000000, Colours.pink);

   final float value;
   final String desc;
   public final Color col;

   private Rarity(int div, Color col) {
      this(1.0F / div, col);
   }

   private Rarity(float chance, Color col) {
      this.value = chance;
      this.desc = makeFromChance(chance);
      this.col = col;
   }

   private static String makeFromChance(float chance) {
      return Tann.percentFormat(chance);
   }

   public static Rarity multiply(float chance, float chance1) {
      return fromChance(chance * chance1);
   }

   public static Rarity multiply(Rarity a, Rarity b) {
      return multiply(a.getValue(), b.getValue());
   }

   public static Rarity fromChance(float chance) {
      for (Rarity r : values()) {
         if (r.getValue() <= chance) {
            return r;
         }
      }

      TannLog.log("millionth rarity hmm");
      return MILLIONTH;
   }

   public float getValue() {
      return this.value;
   }

   public String toColourTaggedString() {
      return TextWriter.getTag(this.col) + this.desc + "[cu]";
   }
}
