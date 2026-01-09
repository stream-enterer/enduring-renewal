package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffect;
import com.tann.dice.screens.shaderFx.DeathType;
import java.util.ArrayList;
import java.util.List;

public abstract class TargetableCommand extends Command {
   public final Targetable targetable;
   public Ent target;
   List<Ent> lastTargets = new ArrayList<>();
   boolean usesDie = true;

   public TargetableCommand(Targetable effect, Ent target) {
      this.targetable = effect;
      this.target = target;
   }

   @Override
   public CombatEffect makeCombatEffect() {
      try {
         return VisualEffectType.generate(this, this.targetable, this.target, this.getSource());
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   @Override
   public void internalEnact(Snapshot snapshot) {
      Eff eff = this.targetable.getDerivedEffects(snapshot);
      this.targetable.beforeUse(snapshot, eff, this.getExtraData());
      if (!eff.hasValue() || eff.getValue() > 0) {
         this.lastTargets.clear();

         for (EntState es : snapshot.target(this.target, this.targetable, this.usesDie)) {
            this.lastTargets.add(es.getEnt());
         }

         this.targetable.afterUse(snapshot, eff, this.getExtraData());
      }
   }

   protected List<Integer> getExtraData() {
      return null;
   }

   @Override
   protected void postEnact(Snapshot snapshot) {
   }

   @Override
   protected void playSoundNoEffect() {
      Eff e = this.targetable.getDerivedEffects(DungeonScreen.get().getFightLog().getSnapshotBefore(this));
      if (e.getType() == EffType.Or) {
         e.getOr(this.target.isPlayer()).playSound();
      } else {
         e.playSound();
      }
   }

   @Override
   protected void showAnimation(CombatEffect combatEffect) {
      combatEffect.internalStart();
   }

   @Override
   public List<Ent> getAllTargets() {
      return this.lastTargets;
   }

   @Override
   public boolean canUndo() {
      return this.usesDie;
   }

   @Override
   public boolean onRescue(Hero saved, Ent saver, Snapshot present, Snapshot prePresent) {
      boolean effect = false;
      Eff eff = this.targetable.getDerivedEffects(prePresent);
      if (eff.hasKeyword(Keyword.rescue)) {
         present.getState(saver).hit(new EffBill().recharge().bEff(), null);
         present.getState(saver).hit(new EffBill().self().event(TextEvent.RESCUE_EVENT).bEff(), null);
         effect = true;
      }

      if (eff.hasKeyword(Keyword.evil)) {
         EntState k = present.getState(saver);
         k.kill(DeathType.Burn);
         effect = true;
      }

      return effect;
   }

   @Override
   public boolean onKill(Ent killer, Snapshot prePresent, Snapshot present) {
      boolean effect = false;
      Eff eff = this.targetable.getDerivedEffects(prePresent);
      if (eff.hasKeyword(Keyword.rampage)) {
         EntState k = present.getState(killer);
         k.hit(new EffBill().recharge().bEff(), null);
         k.hit(new EffBill().self().event(TextEvent.RAMPAGE_EVENT).bEff(), null);
         effect = true;
      }

      if (eff.hasKeyword(Keyword.guilt)) {
         EntState k = present.getState(killer);
         k.kill(DeathType.Burn);
         effect = true;
      }

      return effect;
   }

   public void setUsesDie(boolean b) {
      this.usesDie = b;
   }

   public boolean hasType(EffType type) {
      Eff e = this.targetable.getDerivedEffects();
      return e.hasType(type, false, this.target);
   }
}
