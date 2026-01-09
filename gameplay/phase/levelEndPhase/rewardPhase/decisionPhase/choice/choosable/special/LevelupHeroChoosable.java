package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import java.util.Map;

public class LevelupHeroChoosable implements Choosable {
   final HeroType ht;
   public static final String lucs = "levelup choosable";

   public LevelupHeroChoosable(HeroType ht) {
      this.ht = ht;
   }

   @Override
   public boolean isPositive() {
      return true;
   }

   @Override
   public Color getColour() {
      return this.ht.getColour();
   }

   @Override
   public String getSaveString() {
      return this.ht.getSaveString();
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Levelup;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      dc.levelupFromLevelupPhaseChoice(this.ht, index);
      if (!this.ht.isGenerated()) {
         dc.getStatsManager().pickDelta(this, true);
      }
   }

   @Override
   public void onReject(DungeonContext dc) {
      if (!this.ht.isGenerated()) {
         dc.getStatsManager().pickDelta(this, false);
      }
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      DungeonScreen ds = DungeonScreen.get();
      if (ds == null) {
         return ChoosableUtils.brokenActor("luds");
      } else {
         final Hero target = ds.getDungeonContext().getParty().getHeroFor(this.ht, index);
         if (target == null) {
            return ChoosableUtils.brokenActor("lut");
         } else {
            final Hero choice = target.transformLevelup(this.ht).makeEnt();
            if (choice == null) {
               return ChoosableUtils.brokenActor("luc");
            } else {
               choice.setRealFightLog(ds.getFightLog());
               choice.setLevelupOption(target);
               final EntPanelInventory dp = new EntPanelInventory(choice, false);
               if (!com.tann.dice.Main.getSettings().hasAttemptedLevel()) {
                  dp.addPortraitFlasher();
               }

               if (PhaseManager.get().getPhase() instanceof RandomRevealPhase) {
                  return dp;
               } else {
                  Group g = new Group() {
                     public void draw(Batch batch, float parentAlpha) {
                        if (!com.tann.dice.Main.isPortrait()
                           && target != null
                           && !Tann.hasParent(this, ChoiceDialog.class)
                           && !Tann.hasParent(this, ScrollPane.class)
                           && Math.abs(com.tann.dice.Main.width / 2.0F - Tann.getAbsoluteCoordinates(dp).x - dp.getWidth() / 2.0F) < 5.0F
                           && TannStageUtils.getActorsWithName("levelup choosable", com.tann.dice.Main.getCurrentScreen()).size() <= 2) {
                           EntPanelCombat fromPanel = target.getEntPanel();
                           Vector2 fromVector = Tann.getAbsoluteCoordinates(fromPanel, Tann.TannPosition.Right).cpy();
                           Vector2 toVector = Tann.getAbsoluteCoordinates(dp, Tann.TannPosition.Left).cpy();
                           batch.setColor(choice.getColour());
                           float fx = fromVector.x;
                           float tx = toVector.x;
                           float fy = fromVector.y;
                           float ty = toVector.y;
                           Draw.drawDottedLine(batch, fx - 3.0F, fy, tx + 3.0F, ty, 5.0F, 10.0F, 5.0F, 1.0F);
                        }

                        super.draw(batch, parentAlpha);
                     }
                  };
                  g.setName("levelup choosable");
                  g.setSize(dp.getWidth(), dp.getHeight());
                  g.addActor(dp);
                  g.setTransform(false);
                  return g;
               }
            }
         }
      }
   }

   @Override
   public int getTier() {
      return this.ht.getTier();
   }

   @Override
   public float getModTier() {
      return TierUtils.levelupHeroChoosable(this.ht.getTier());
   }

   @Override
   public String describe() {
      return "level-up";
   }

   @Override
   public float chance() {
      return 1.0F;
   }

   @Override
   public String getTierString() {
      return this.ht.getTierString();
   }

   @Override
   public String getName() {
      return this.ht.getName();
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

   public HeroType getHeroType() {
      return this.ht;
   }

   @Override
   public String toString() {
      return "Levelup to " + this.ht.getName();
   }
}
