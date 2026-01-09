package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.afterRoll;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import java.util.Arrays;
import java.util.List;

public class NumSidesRolledAchievement extends AfterRollAchievement {
   final EffType type;
   final EntSide side;
   final int min;

   public NumSidesRolledAchievement(String name, EffType type, int min, Unlockable... unlockables) {
      super(name, DESC(type, min), unlockables);
      this.type = type;
      this.side = null;
      this.min = min;
      this.diff(15.0F);
   }

   public NumSidesRolledAchievement(String name, EntSide side, int min, Unlockable... unlockables) {
      super(name, DESC(side, min), unlockables);
      this.type = null;
      this.side = side;
      this.min = min;
      this.diff(10.0F);
   }

   private static String DESC(EffType type, int min) {
      return "Roll " + min + " " + type + " sides at once";
   }

   private static String DESC(EntSide type, int min) {
      String desc = type.getBaseEffect().describe();
      if (type == ESB.blankPetrified) {
         desc = "petrified";
      } else if (type == ESB.blankSingleUsed) {
         desc = "single-used";
      }

      return "Roll " + min + " " + desc + " sides at once";
   }

   @Override
   public boolean allDiceLandedCheck(List<EntSideState> dice) {
      int count = 0;

      for (EntSideState ess : dice) {
         if (this.type != null && ess.getCalculatedEffect().getType() == this.type) {
            count++;
         }

         if (this.side != null && ess.getCalculatedTexture() == this.side.getTexture()) {
            count++;
         }
      }

      return count >= this.min;
   }

   public static List<AfterRollAchievement> makeAll() {
      return Arrays.asList(
         new NumSidesRolledAchievement("Snake-eyes", EffType.Blank, 5, ItemLib.byName("bent spoon")),
         new NumSidesRolledAchievement("Petrified", ESB.blankPetrified, 3, ItemLib.byName("basilisk scale")),
         new NumSidesRolledAchievement("Fully-Petrified", ESB.blankPetrified, 5)
      );
   }
}
