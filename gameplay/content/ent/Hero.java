package com.tann.dice.gameplay.content.ent;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.progress.stats.StatUpdate;
import com.tann.dice.gameplay.save.SaveStateData;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.RollManager;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;

public class Hero extends Ent {
   private boolean diedLastRound;
   boolean allowInjection = false;

   public Hero(HeroType type) {
      super(type);
      if (this.getHeroCol() == null) {
         throw new RuntimeException("ah no col for " + type);
      } else {
         this.setColour(type.heroCol.col);
         this.setupLapels(type.level);
      }
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   public HeroType transformLevelup(HeroType newType) {
      try {
         if (newType == null) {
            newType = HeroTypeLib.getMissingno();
         }

         for (Personal activeTrigger : this.getBlankState().getActivePersonals()) {
            HeroType tmp = activeTrigger.affectLevelup(this.getHeroType(), newType);
            if (tmp != null && !tmp.isMissingno()) {
               newType = tmp;
            }
         }

         return newType;
      } catch (Exception var5) {
         TannLog.error(var5, "transform levelup");
         return HeroTypeLib.getMissingno();
      }
   }

   public void levelUpTo(HeroType newType, DungeonContext dc) {
      if (dc != null) {
         newType = this.transformLevelup(newType);
      }

      this.entType = newType;
      this.name = newType.getName(false);
      this.traits = newType.traits.toArray(new Trait[0]);
      this.setupLapels(this.getLevel());
      this.setSides(newType.sides);
      this.updateOutOfCombat();
      this.getDie().resetSides();
      if (dc != null) {
         dc.getParty().heroOnChooseStuff(dc, newType.makeEnt());
      }
   }

   public int getLevel() {
      return this.getHeroType().level;
   }

   @Override
   public int getPixelSize() {
      return this.getSize().getPixels();
   }

   @Override
   public void deathSound() {
      Sounds.playSound(Sounds.deathHero);
   }

   @Override
   public void stopped() {
      super.stopped();
      if (!BulletStuff.isSimulating() && !TestRunner.isTesting()) {
         this.stoppedGameplayImplications();
         RollManager rm = DungeonScreen.get().rollManager;
         if (rm.allDiceStopped()) {
            List<EntSideState> currentStates = new ArrayList<>();

            for (Ent e : this.fightLog.getActiveEntities(true)) {
               EntState es = e.getState(FightLog.Temporality.Present);
               EntSide eSide = e.getDie().getCurrentSide();
               if (es != null && eSide != null) {
                  currentStates.add(es.getSideState(eSide));
               } else {
                  TannLog.error("hmm, something broken with all dice stopped");
               }
            }

            if (!this.fightLog.getContext().skipStats()) {
               for (StatUpdate su : this.fightLog.getStatUpdates()) {
                  su.updateAllDiceLanded(currentStates);
               }
            }

            DungeonScreen ds = DungeonScreen.get();
            if (ds != null) {
               ds.allHeroDiceLanded();
            }

            Phase p = PhaseManager.get().getPhase();
            if (p instanceof PlayerRollingPhase) {
               ((PlayerRollingPhase)p).setAutosaved();
            }

            DungeonScreen.get().save();
         }

         Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         if (present.getRolls() == 0 && rm.allDiceStopped()) {
            Tann.delay(0.3F, new Runnable() {
               @Override
               public void run() {
                  if (PhaseManager.get().getPhase() instanceof PlayerRollingPhase) {
                     DungeonScreen.get().confirmClicked(false);
                  }
               }
            });
         }
      }
   }

   public boolean stoppedGameplayImplications() {
      EntSide side = this.die.getCurrentSide();
      EntState pres = this.getState(FightLog.Temporality.Present);
      Eff e = pres.getSideState(side).getCalculatedEffect();
      boolean done = false;
      if (e.hasKeyword(Keyword.cantrip)
         && e.getType() != EffType.Resurrect
         && (e.getType() != EffType.Summon || !(EntTypeUtils.byName(e.getSummonType()) instanceof HeroType))) {
         DungeonScreen ds = DungeonScreen.get();
         boolean usable = ds.targetingManager.isUsable(new DieTargetable(this, this.die.getSideIndex()), false, true);
         usable |= e.getValue() > 0 && e.getType() == EffType.Reroll;
         if (usable) {
            this.activateCantrip(ds.getFightLog());
         }

         done = true;
      }

      if (pres.isAutoLock()
         || pres.isAutoLockLite() && e.getType() == EffType.Blank && !this.fightLog.getSnapshot(FightLog.Temporality.Present).hasAnyLockRestrictions()) {
         this.die.toggleLock();
         DungeonScreen.get().onLock();
         done = true;
      }

      return done;
   }

   public void activateCantrip(FightLog fightLog) {
      DieCommand dc = new DieCommand(new DieTargetable(this, this.die.getSideIndex()), null);
      dc.setUsesDie(false);
      Snapshot present = fightLog.getSnapshot(FightLog.Temporality.Present);
      if (present.getState(this).isDead()) {
         Sounds.playSound(Sounds.chip);
      } else {
         Eff first = dc.targetable.getDerivedEffects(fightLog.getSnapshot(FightLog.Temporality.Present));
         if (first.needsTarget()) {
            List<Ent> potentials = TargetingManager.getRecommendedTargets(present, dc.targetable, dc.getSource() == null || dc.getSource().isPlayer());
            if (potentials.size() == 0) {
               potentials = TargetingManager.getValidTargets(present, dc.targetable, dc.getSource() == null || dc.getSource().isPlayer());
            }

            if (potentials.size() == 0) {
               return;
            }

            dc.target = Tann.random(potentials);
         } else if (!first.canBeUsedUntargeted(present)) {
            return;
         }

         fightLog.addCommand(dc, false);
         this.getDie().setCantripFlash(5.0F, 0.45F);
      }
   }

   public HeroType getHeroType() {
      return (HeroType)this.entType;
   }

   public boolean isDiedLastRound() {
      return this.diedLastRound;
   }

   public void setDiedLastRound(boolean diedLastRound) {
      this.diedLastRound = diedLastRound;
   }

   @Override
   public Color getColour() {
      return this.getHeroCol().col;
   }

   public HeroCol getHeroCol() {
      return this.getHeroType().heroCol;
   }

   public String fullSaveString() {
      List<String> lst = new ArrayList<>();
      lst.add(this.getHeroType().getSaveString());
      if (this.isDiedLastRound()) {
         lst.add(SaveStateData.deadHeroTag);
      }

      for (Item i : this.getItems()) {
         lst.add(i.getSaveString());
      }

      return Tann.commaList(lst, "~", "~");
   }

   public void setLevelupOption(Hero target) {
      this.allowInjection = true;
      this.blankState = null;
      this.setDiedLastRound(target.isDiedLastRound());
   }

   @Override
   protected boolean allowContextInjection() {
      return this.allowInjection;
   }

   public boolean canLevelUp() {
      if (this.getHeroType().level < 0) {
         return false;
      } else {
         for (Personal pt : this.getBlankState().getActivePersonals()) {
            if (!pt.canLevelUp()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasItem(Item item) {
      for (int i = 0; i < this.itemAsList.size(); i++) {
         if (this.itemAsList.get(i) == item) {
            return true;
         }
      }

      return false;
   }
}
