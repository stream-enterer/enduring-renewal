package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.TextWriter;
import java.util.Map;

public class ReplaceChoosable implements Choosable {
   public final Choosable lose;
   public final Choosable gain;

   public ReplaceChoosable(Choosable lose, Choosable gain) {
      this.lose = lose;
      this.gain = gain;
   }

   public static Choosable make(String data) {
      String[] parts = data.split("~");
      return new ReplaceChoosable(ChoosableUtils.deserialise(parts[0]), ChoosableUtils.deserialise(parts[1]));
   }

   @Override
   public boolean isPositive() {
      return this.gain.isPositive();
   }

   @Override
   public Color getColour() {
      return Colours.purple;
   }

   @Override
   public String getSaveString() {
      return ChoosableUtils.fullSerialise(this.lose) + "~" + ChoosableUtils.fullSerialise(this.gain);
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Replace;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      dc.onLose(this.lose);
      this.gain.onChoose(dc, index);
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      if (!big) {
         Actor pl = new TextWriter("+");
         pl.setTouchable(Touchable.disabled);
         Group base = Tann.makeGroup(TannStageUtils.noListener(this.gain.makeChoosableActor(big, index)));
         base.addActor(pl);
         pl.setPosition(2.0F, base.getHeight() - pl.getHeight() - 2.0F);
         return base;
      } else {
         return new Pixl(3)
            .text(this.describe() + "?")
            .row()
            .actor(this.lose.makeChoosableActor(false, index))
            .image(Images.arrowRight, this.getColour())
            .actor(this.gain.makeChoosableActor(false, index))
            .pix();
      }
   }

   @Override
   public int getTier() {
      return this.gain.getTier() - this.lose.getTier();
   }

   @Override
   public float getModTier() {
      return this.getTier();
   }

   @Override
   public String describe() {
      return "Replace " + this.lose.getName();
   }

   @Override
   public float chance() {
      return 0.0F;
   }

   @Override
   public String getTierString() {
      return "?ts?";
   }

   @Override
   public String getName() {
      return this.describe() + "?name";
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
