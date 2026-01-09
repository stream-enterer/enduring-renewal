package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class ChallengeReward {
   private String data;

   public ChallengeReward(List<Choosable> ch) {
      this.data = ChoosableUtils.serialiseList(ch);
   }

   public ChallengeReward() {
   }

   public static ChallengeReward generate(
      ChallengePhase.ChallengeDifficulty challengeDifficulty, DungeonContext dc, int currentLevelNumber, ChallengeType challengeType
   ) {
      int rewardLevel = Math.round(challengeType.getPower() * 0.33F);
      List<Item> items;
      switch (challengeDifficulty) {
         case Easy:
            items = ItemLib.randomWithExactQuality(rewardLevel, 1, dc);
            break;
         case Standard:
         default:
            boolean multiple = Math.random() > 0.5;
            if (multiple) {
               int quality;
               if (rewardLevel == 6) {
                  quality = 3;
               } else if (rewardLevel >= 4) {
                  quality = 2;
               } else {
                  quality = 1;
               }

               items = ItemLib.randomWithExactQuality(Math.round((float)rewardLevel / quality), quality, dc);
            } else {
               items = ItemLib.randomWithExactQuality(1, rewardLevel, dc);
            }
      }

      return new ChallengeReward(new ArrayList<>(items));
   }

   public List<Choosable> getRewards() {
      return ChoosableUtils.deserialiseList(this.data);
   }

   public Actor makeActor(boolean big) {
      List<Choosable> chz = this.getRewards();
      boolean small = chz.size() > 1 && !big;
      List<Actor> actors = new ArrayList<>();

      for (Choosable e : chz) {
         Actor a;
         if (small) {
            a = e.makeChoosableActor(false, 0);
         } else {
            a = e.makeChoosableActor(big, 0);
         }

         actors.add(a);
      }

      return Tann.layoutMinArea(actors, 2, (int)(com.tann.dice.Main.width * 0.6F), (int)(com.tann.dice.Main.height * 0.8F));
   }

   public void activate(FightLog fightLog) {
      DungeonContext dc = fightLog.getContext();
      ChoosableUtils.checkedOnChoose(this.getRewards(), dc, "trying to fight reward");
      fightLog.getContext().setCheckedItems(false);
   }

   public void reject(FightLog fightLog) {
      DungeonContext dc = fightLog.getContext();

      for (Choosable ch : this.getRewards()) {
         ch.onReject(dc);
      }
   }

   public int getNumRewards() {
      return this.getRewards().size();
   }
}
