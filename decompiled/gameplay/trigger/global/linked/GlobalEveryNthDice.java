package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;

public class GlobalEveryNthDice extends GlobalLinked {
   final int num;
   final boolean every;
   final AffectSides linked;
   private final String desc;

   public GlobalEveryNthDice(int num, Keyword k) {
      this(num, true, new AffectSides(new AddKeyword(k)).buffPriority(), "gains " + k.getColourTaggedString());
   }

   private GlobalEveryNthDice(int num, boolean every, AffectSides linked, String desc) {
      super(linked);
      this.desc = desc;
      this.num = num;
      this.every = every;
      this.linked = linked;
      if (linked.getPriority() != 0.0F) {
         TannLog.error("Invalid priority: " + linked.getPriority() + linked);
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl(2, 2).border(Colours.green).actor(new TextWriter(this.describeFreqShort() + ":", 35)).gap(2).actor(this.linked.makePanelActor(big)).pix();
   }

   @Override
   public String describeForSelfBuff() {
      return this.describeFreq() + " " + this.desc + ModifierUtils.afterItems();
   }

   private String describeFreq() {
      return this.describeFreqShort() + " you use each turn";
   }

   private String describeFreqShort() {
      return this.every ? "Every " + Words.ordinal(this.num) + " dice" : "The " + Words.ordinal(this.num) + " dice";
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      if (!entState.isPlayer()) {
         return null;
      } else {
         Snapshot s = entState.getSnapshot();
         if (s == null) {
            return null;
         } else {
            int diceUsed = s.getNumDiceUsedThisTurn();
            return (this.every || this.num != diceUsed + 1) && (!this.every || (diceUsed + 1) % this.num != 0) ? null : this.linked;
         }
      }
   }

   public static Modifier makeNthKeyword(int n, Keyword k) {
      float allTier = KUtils.getModTierAllHero(k);
      float multiplier = (float)(1.0 / Math.pow(n, 1.05F));
      float tier = allTier * multiplier;
      if (k == Keyword.death) {
         tier *= 1.15F;
      }

      return new Modifier(tier, Words.ordinal(n) + " " + k.name(), new GlobalEveryNthDice(n, k)).rarity(Rarity.THIRD);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ignored(this.linked.getCollisionBits(true), Collision.GENERIC_ALL_SIDES_HERO);
   }
}
