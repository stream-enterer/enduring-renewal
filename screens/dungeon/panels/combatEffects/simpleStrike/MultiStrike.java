package com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;

public class MultiStrike extends CombatEffectActor {
   Targetable targetable;
   Ent target;
   final int damage;
   TextureRegion image;
   float impactTime;
   float stickTime;
   float retractTime;
   FightLog fightLog;

   public MultiStrike(
      Targetable targetable, Ent target, int damage, TextureRegion image, float impactTime, float stickTime, float retractTime, FightLog fightLog
   ) {
      this.fightLog = fightLog;
      this.targetable = targetable;
      this.target = target;
      this.damage = damage;
      this.image = image;
      this.impactTime = impactTime;
      this.stickTime = stickTime;
      this.retractTime = retractTime;
   }

   @Override
   protected void start(FightLog fightLog) {
      for (EntState target : this.fightLog
         .getSnapshot(FightLog.Temporality.Visual)
         .getActualTargets(this.target, this.targetable.getBaseEffect(), this.targetable.getSource())) {
         SimpleStrike ss = new SimpleStrike(target.getEnt(), this.damage, this.image, this.impactTime, this.stickTime, this.retractTime);
         ss.start(fightLog);
      }
   }

   @Override
   protected float getImpactDurationInternal() {
      return this.impactTime;
   }

   @Override
   protected float getExtraDurationInternal() {
      return this.stickTime + this.retractTime;
   }
}
