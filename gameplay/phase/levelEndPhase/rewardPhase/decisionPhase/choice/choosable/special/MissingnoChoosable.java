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

public class MissingnoChoosable implements Choosable {
   static final String tag = "goof";

   @Override
   public boolean isPositive() {
      return false;
   }

   @Override
   public Color getColour() {
      return Colours.pink;
   }

   @Override
   public String getSaveString() {
      return "goof";
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.MISSINGNO;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      return new Pixl().text("goof").pix();
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
      return "goof";
   }

   @Override
   public float chance() {
      return 0.0F;
   }

   @Override
   public String getTierString() {
      return null;
   }

   @Override
   public String getName() {
      return "goof";
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
