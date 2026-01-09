package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.Map;

public class SkipChoosable implements Choosable {
   public static boolean validData(String data) {
      return data == null || data.isEmpty() || data.equals("s");
   }

   @Override
   public boolean isPositive() {
      return false;
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   public String getSaveString() {
      return "";
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Skip;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      return new Pixl(0, 3).border(Colours.text).text("skip").pix();
   }

   @Override
   public int getTier() {
      return 0;
   }

   @Override
   public float getModTier() {
      return 0.0F;
   }

   @Override
   public String describe() {
      return "skip";
   }

   @Override
   public float chance() {
      return 0.0F;
   }

   @Override
   public String getTierString() {
      return "";
   }

   @Override
   public String getName() {
      return "skip";
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
