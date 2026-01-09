package com.tann.dice.gameplay.progress.stats.stat.endOfRun;

import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.leaderboard.SpeedrunLeaderboard;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.StatMergeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpeedrunStat extends GameEndStat {
   public SpeedrunStat(ContextConfig cc) {
      super(getName(cc));
   }

   @Override
   protected StatMergeType getMergeType() {
      return StatMergeType.Lowest;
   }

   @Override
   public void endOfRun(DungeonContext context, boolean victory) {
      if (this.validForSpeedrun(context)) {
         if (victory && this.getName().equals(getName(context.getContextConfig()))) {
            this.setValue((int)context.getFinalTimeSeconds());
         }
      }
   }

   private boolean validForSpeedrun(DungeonContext context) {
      List<Modifier> mods = context.getCurrentModifiers();
      boolean hasSkip = mods.contains(ModifierLib.byName("skip"));
      boolean basicParty = context.getParty().getPLT() == PartyLayoutType.Basic || !SpeedrunLeaderboard.needsBasic(context.getContextConfig().mode);
      boolean hasTweak = this.hasTweak(context);
      return basicParty && hasSkip && !hasTweak;
   }

   private boolean hasTweak(DungeonContext context) {
      for (Modifier mod : context.getCurrentModifiers()) {
         if (mod.getTier() == 0 && !mod.getName().equalsIgnoreCase("skip")) {
            return true;
         }
      }

      return false;
   }

   public static Collection<? extends Stat> makeAll() {
      List<Stat> stats = new ArrayList<>();

      for (ContextConfig cc : Mode.getAllSaveBearingConfigs()) {
         if (!cc.skipStats()) {
            stats.add(new SpeedrunStat(cc));
         }
      }

      return stats;
   }

   public static String getName(ContextConfig cc) {
      return cc.getSpecificKey() + "-fastest-time-bs31";
   }

   @Override
   public boolean validFor(ContextConfig contextConfig) {
      return this.getName().equalsIgnoreCase(getName(contextConfig));
   }

   @Override
   public boolean isBoring() {
      return true;
   }
}
