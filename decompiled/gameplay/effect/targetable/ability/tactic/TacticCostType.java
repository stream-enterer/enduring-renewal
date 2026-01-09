package com.tann.dice.gameplay.effect.targetable.ability.tactic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.List;

public enum TacticCostType {
   basicSword("sword", true),
   basicShield("shield", true),
   basicHeal("heal", true),
   basicMana("mana", true),
   wild("wild", true),
   blank("blank"),
   pips1("pips1"),
   pips2("pips2"),
   pips3("pips3"),
   pips4("pips4"),
   keyword("keyword"),
   twoKeywords("twoKeywords"),
   fourKeywords("fourKeywords");

   private final TextureRegion img;
   public final boolean pippy;
   private static List<TacticCostType> cache = new ArrayList<>();

   private TacticCostType(String name) {
      this(name, false);
   }

   private TacticCostType(String name, boolean pippy) {
      this.img = ImageUtils.loadExt("ability/tactic/cost/" + name);
      this.pippy = pippy;
   }

   public static TacticCostType getValidTypeTextmod(Eff calcEff) {
      switch (calcEff.getType()) {
         case Damage:
            return basicSword;
         case Heal:
            return basicHeal;
         case Mana:
            return basicMana;
         case Shield:
            return basicShield;
         case Blank:
            return calcEff.isBasic() ? null : blank;
         case Resurrect:
            return wild;
         default:
            switch (calcEff.getKeywords().size()) {
               case 1:
                  return keyword;
               case 2:
                  return twoKeywords;
               case 3:
               default:
                  switch (calcEff.getValue()) {
                     case 1:
                        return pips1;
                     case 2:
                        return pips2;
                     case 3:
                        return pips3;
                     case 4:
                        return pips4;
                     default:
                        return null;
                  }
               case 4:
                  return fourKeywords;
            }
      }
   }

   private boolean isValid(Eff e) {
      EffType type = e.getType();
      switch (this) {
         case pips4:
            return e.getValue() == 4;
         case pips3:
            return e.getValue() == 3;
         case pips2:
            return e.getValue() == 2;
         case pips1:
            return e.getValue() == 1;
         case wild:
            return e.hasValue();
         case blank:
            return type == EffType.Blank;
         case basicSword:
            return type == EffType.Damage || e.hasKeyword(Keyword.damage);
         case basicShield:
            return type == EffType.Shield || type == EffType.HealAndShield || e.hasKeyword(Keyword.selfShield) || e.hasKeyword(Keyword.shield);
         case basicHeal:
            return type == EffType.Heal || type == EffType.HealAndShield || e.hasKeyword(Keyword.selfHeal) || e.hasKeyword(Keyword.heal);
         case basicMana:
            return type == EffType.Mana || e.hasKeyword(Keyword.manaGain);
         case keyword:
            return e.getKeywords().size() == 1;
         case twoKeywords:
            return e.getKeywords().size() == 2;
         case fourKeywords:
            return e.getKeywords().size() == 4;
         default:
            return false;
      }
   }

   public static List<TacticCostType> getValidTypes(Eff e) {
      cache.clear();
      TacticCostType[] vals = values();

      for (int i = 0; i < vals.length; i++) {
         TacticCostType tct = vals[i];
         if (tct.isValid(e)) {
            cache.add(tct);
         }
      }

      return cache;
   }

   public Actor getActor(boolean highlit) {
      return new ImageActor(this.img, highlit ? Colours.light : Colours.grey);
   }

   private String desc() {
      switch (this) {
         case pips4:
            return this.pipSide(4);
         case pips3:
            return this.pipSide(3);
         case pips2:
            return this.pipSide(2);
         case pips1:
            return this.pipSide(1);
         case wild:
            return "any pip";
         case blank:
            return "blank side";
         case basicSword:
            return "damage pip";
         case basicShield:
            return "shield pip";
         case basicHeal:
            return "heal pip";
         case basicMana:
            return "mana pip";
         case keyword:
            return this.keywordSide(1);
         case twoKeywords:
            return this.keywordSide(2);
         case fourKeywords:
            return this.keywordSide(4);
         default:
            return "unk: " + this.name();
      }
   }

   private String keywordSide(int i) {
      return i + "-keyword side";
   }

   private String pipSide(int i) {
      return i + "-pip side";
   }

   public Actor makeExpl() {
      return new Pixl(3).image(this.img, Colours.grey).text("[notranslate]: " + com.tann.dice.Main.t(this.desc())).pix();
   }

   public String describe(int thisAmt) {
      return "[notranslate]" + thisAmt + "x " + com.tann.dice.Main.t(Words.plural(this.desc(), thisAmt));
   }
}
