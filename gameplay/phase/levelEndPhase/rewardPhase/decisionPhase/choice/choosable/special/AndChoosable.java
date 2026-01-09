package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AndChoosable implements Choosable {
   List<Choosable> haveAll;

   public AndChoosable() {
   }

   public AndChoosable(Choosable... haveAll) {
      this.haveAll = Arrays.asList(haveAll);
   }

   private Choosable first() {
      return this.haveAll.get(0);
   }

   @Override
   public boolean isPositive() {
      return this.first().isPositive();
   }

   @Override
   public Color getColour() {
      return this.first().getColour();
   }

   @Override
   public String getSaveString() {
      return ChoosableUtils.serialiseList(this.haveAll);
   }

   public static Choosable byName(String n) {
      return (Choosable)com.tann.dice.Main.getJson(true).fromJson(AndChoosable.class, n);
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.And;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      for (Choosable option : this.haveAll) {
         option.onChoose(dc, index);
      }
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      List<Actor> actors = new ArrayList<>();

      for (Choosable option : this.haveAll) {
         actors.add(option.makeChoosableActor(big, index));
      }

      return OrChoosable.makeHackySeamless(actors, "take[n]all", Colours.orange);
   }

   @Override
   public int getTier() {
      return this.first().getTier();
   }

   @Override
   public float getModTier() {
      return TierUtils.totalModTier(this.haveAll);
   }

   @Override
   public String describe() {
      List<String> parts = new ArrayList<>();

      for (Choosable option : this.haveAll) {
         String s = option.getName();
         if (ChoosableUtils.shouldBracket(option)) {
            s = "(" + s + ")";
         }

         parts.add(s);
      }

      String sep = " and ";
      return Tann.commaList(parts, sep, sep);
   }

   @Override
   public float chance() {
      return 0.0F;
   }

   @Override
   public String getTierString() {
      return "?";
   }

   @Override
   public String getName() {
      return this.describe();
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
