package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemKeyword;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import java.util.Map;

public enum EnumChoosable implements Choosable {
   RandoKeywordT7Item(7),
   RandoKeywordT5Item(5),
   RandoKeywordT1Item(1);

   final int tier;

   private EnumChoosable(int tier) {
      this.tier = tier;
   }

   @Override
   public boolean isPositive() {
      return this.getTier() > 0;
   }

   @Override
   public Color getColour() {
      return Colours.blue;
   }

   @Override
   public String getSaveString() {
      return this.name();
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Enu;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      Item i = PipeItemKeyword.makeRandomForEvent(this.getSST());
      dc.getParty().addItem(i);
      PhaseManager.get().pushPhaseNext(new RandomRevealPhase(i));
   }

   private SpecificSidesType getSST() {
      switch (this) {
         case RandoKeywordT7Item:
            return SpecificSidesType.All;
         case RandoKeywordT5Item:
            return Tann.pick(SpecificSidesType.Left, SpecificSidesType.Wings, SpecificSidesType.RightThree);
         case RandoKeywordT1Item:
            return SpecificSidesType.RightMost;
         default:
            TannLog.error("invalid ec: " + this);
            return SpecificSidesType.MiddleFour;
      }
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      switch (this) {
         default:
            return new Pixl(2, 2).border(Colours.grey).text(this.describe()).pix();
      }
   }

   @Override
   public int getTier() {
      return this.tier;
   }

   @Override
   public float getModTier() {
      return TierUtils.toModTier(ChoosableType.Item, this.getTier());
   }

   @Override
   public String describe() {
      switch (this) {
         case RandoKeywordT7Item:
            return "A [b]random[b] keyword item[n][grey](all sides)";
         case RandoKeywordT5Item:
            return "A [b]random[b] keyword item[n][grey](left, topbot or right3)";
         case RandoKeywordT1Item:
            return "A [b]random[b] keyword item[n][grey](rightmost)";
         default:
            return this.name();
      }
   }

   @Override
   public float chance() {
      return 1.0F;
   }

   @Override
   public String getTierString() {
      return Words.getTierString(this.getTier(), false);
   }

   @Override
   public String getName() {
      return "enum";
   }

   @Override
   public boolean encountered(Map<String, Stat> allMergedStats) {
      return false;
   }

   @Override
   public int getPicks(Map<String, Stat> allMergedStats, boolean reject) {
      return 0;
   }

   @Override
   public long getCollisionBits() {
      return 0L;
   }
}
