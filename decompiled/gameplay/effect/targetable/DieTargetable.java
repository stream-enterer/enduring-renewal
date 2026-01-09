package com.tann.dice.gameplay.effect.targetable;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.named.Exerted;
import com.tann.dice.gameplay.trigger.personal.replaceSides.Decay;
import com.tann.dice.gameplay.trigger.personal.replaceSides.Groooooowth;
import com.tann.dice.gameplay.trigger.personal.replaceSides.Growth;
import com.tann.dice.gameplay.trigger.personal.replaceSides.ReplaceSideIndex;
import com.tann.dice.screens.shaderFx.DeathType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DieTargetable implements Targetable {
   final Ent source;
   final int sideIndex;

   public DieTargetable(Ent source, int sideIndex) {
      this.source = source;
      this.sideIndex = sideIndex;
   }

   @Override
   public Eff getBaseEffect() {
      return this.getSide().getBaseEffect();
   }

   @Override
   public Eff getDerivedEffects() {
      return this.getDerivedEffects(this.source.getFightLog().getSnapshot(FightLog.Temporality.Present));
   }

   public Eff getDerivedEffect(Snapshot snapshot) {
      EntState srcState = snapshot.getState(this.source);
      return srcState == null ? null : srcState.getSideState(this.getSide()).getCalculatedEffect();
   }

   @Override
   public Eff getDerivedEffects(Snapshot snapshot) {
      return snapshot.getState(this.source).getSideState(this.getSide()).getCalculatedEffect();
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
      return this.source.isPlayer();
   }

   @Override
   public void afterUse(Snapshot snapshot, Eff eff, List<Integer> extraData) {
      afterUseKeywords(eff, eff.getKeywordForGameplay(), snapshot, this.source, this.sideIndex);
   }

   @Override
   public void beforeUse(Snapshot snapshot, Eff preDerivedEffect, List<Integer> extraData) {
   }

   private static void afterUseKeywords(Eff src, List<Keyword> keywords, Snapshot snapshot, Ent source, int sideIndex) {
      int kVal = KUtils.getValue(src);
      EntState sourceState = snapshot.getState(source);
      if (keywords.contains(Keyword.lead)) {
         for (EntState state : snapshot.getStates(source.isPlayer(), false)) {
            if (state.getEnt() != source) {
               state.addBuff(new Buff(1, new AffectSides(new TypeCondition(src.getType(), false, false), new FlatBonus(kVal))));
            }
         }
      }

      if (keywords.contains(Keyword.growth)) {
         sourceState.addBuff(new Buff(new Growth(sideIndex, 1)));
      }

      if (keywords.contains(Keyword.hyperGrowth)) {
         sourceState.addBuff(new Buff(new Growth(sideIndex, kVal)));
      }

      if (keywords.contains(Keyword.groooooowth)) {
         sourceState.addBuff(new Buff(new Groooooowth()));
      }

      if (keywords.contains(Keyword.undergrowth)) {
         sourceState.addBuff(new Buff(new Growth(EntDie.opposite(sideIndex, false), 1)));
      }

      if (keywords.contains(Keyword.decay)) {
         sourceState.addBuff(new Buff(new Decay(sideIndex, -1)));
      }

      if (keywords.contains(Keyword.singleUse)) {
         EntSize sz = sourceState.getEnt().getSize();
         sourceState.addBuff(new Buff(new ReplaceSideIndex(sideIndex, sz == EntSize.reg ? ESB.blankSingleUsed : sz.getBlank())));
      }

      if (keywords.contains(Keyword.exert)) {
         Buff b = new Buff(1, new Exerted(sourceState.getEnt().getSize()));
         b.skipFirstTick();
         sourceState.addBuff(b);
      }

      if (keywords.contains(Keyword.potion)) {
         List<Item> potions = source.getPotions(sourceState);
         if (potions.isEmpty()) {
            sourceState.addEvent(new TextEvent("[purple]fraud"));
         } else {
            sourceState.discard(potions.get(0), "[purple]drank", true);
         }
      }

      if (keywords.contains(Keyword.death)) {
         sourceState.kill(DeathType.Singularity);
      }

      if (keywords.contains(Keyword.rite)) {
         for (EntState allyState : snapshot.getStates(true, false)) {
            if (source != allyState.getEnt()) {
               allyState.useDie();
            }
         }
      }

      for (int effIndex = 0; effIndex < keywords.size(); effIndex++) {
         Keyword k = keywords.get(effIndex);
         Keyword group = k.getGroupAct();
         if (group != null) {
            List<Keyword> groupKeyword = Arrays.asList(group);
            List<EntState> aliveEntStates = snapshot.getAliveEntStates(source.isPlayer());

            for (int i = 0; i < aliveEntStates.size(); i++) {
               afterUseKeywords(src, groupKeyword, snapshot, aliveEntStates.get(i).getEnt(), sideIndex);
            }
         }

         if (k.isDoubleAct()) {
            List<Keyword> ls = new ArrayList<>(Arrays.asList(k.getMetaKeyword()));

            for (int i = 0; i < 2; i++) {
               afterUseKeywords(src, ls, snapshot, source, sideIndex);
            }
         }
      }
   }

   public EntSide getSide() {
      return this.source.getSides()[this.sideIndex];
   }

   public int getSideIndex() {
      return this.sideIndex;
   }
}
