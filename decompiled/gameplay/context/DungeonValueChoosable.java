package com.tann.dice.gameplay.context;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.util.Colours;
import java.util.Map;

public class DungeonValueChoosable implements Choosable {
   final DungeonValue delta;

   public DungeonValueChoosable(DungeonValue delta) {
      this.delta = delta;
   }

   public DungeonValueChoosable(String data) {
      if (data != null && data.contains("V")) {
         String[] parts = data.split("V");
         if (parts.length != 2) {
            throw new RuntimeException("Invalid DVC: " + data);
         } else {
            this.delta = new DungeonValue(parts[0], Integer.parseInt(parts[1]));
         }
      } else {
         throw new RuntimeException("Invalid DVC: " + data);
      }
   }

   @Override
   public boolean isPositive() {
      return true;
   }

   @Override
   public Color getColour() {
      return Colours.orange;
   }

   @Override
   public String getSaveString() {
      return this.delta.toSaveString();
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Value;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      dc.addValue(this.delta.copy());
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      return this.delta.getActor(big, true);
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
      return this.delta.desc(true);
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
