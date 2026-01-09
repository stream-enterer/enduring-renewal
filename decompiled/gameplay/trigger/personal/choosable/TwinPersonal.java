package com.tann.dice.gameplay.trigger.personal.choosable;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;

public class TwinPersonal extends Personal {
   final HeroType heroType;

   public TwinPersonal(HeroType heroType) {
      this.heroType = heroType;
   }

   @Override
   public void onChoose(DungeonContext dc, Choosable source) {
      Hero extraHero = this.getExtraHero();
      if (extraHero != null) {
         HeroType src = HeroTypeLib.byName("Twin");
         dc.getParty().addHero(extraHero, src, dc);
         if (DungeonScreen.get() != null) {
            FightLog f = DungeonScreen.get().getFightLog();
            f.resetDueToFiddling();
         }
      }
   }

   @Override
   public Hero getExtraHero() {
      return this.heroType.makeEnt();
   }

   @Override
   public String getImageName() {
      return "big/twin";
   }

   @Override
   public String describeForSelfBuff() {
      return "There is a copy of me who benefits from my items";
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 4.1F;
   }
}
