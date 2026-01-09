package com.tann.dice.gameplay.effect.targetable;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.Snapshot;
import java.util.List;

public class SimpleTargetable implements Targetable {
   Eff effect;
   Ent source;

   public SimpleTargetable(Ent source, Eff effect) {
      this.effect = effect;
      this.source = source;
   }

   @Override
   public Eff getBaseEffect() {
      return this.effect;
   }

   @Override
   public Eff getDerivedEffects() {
      return this.getBaseEffect();
   }

   @Override
   public Eff getDerivedEffects(Snapshot snapshot) {
      return this.getBaseEffect();
   }

   @Override
   public boolean isUsable(Snapshot snapshot) {
      return true;
   }

   @Override
   public Ent getSource() {
      return this.source;
   }

   @Override
   public boolean isPlayer() {
      return this.source == null || this.source.isPlayer();
   }

   @Override
   public void afterUse(Snapshot snapshot, Eff preDerivedEffect, List<Integer> extraData) {
   }

   @Override
   public void beforeUse(Snapshot snapshot, Eff preDerivedEffect, List<Integer> extraData) {
   }
}
