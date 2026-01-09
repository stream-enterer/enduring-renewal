package com.tann.dice.screens.dungeon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TargetingManager {
   private FightLog fightLog;
   private Targetable selectedTargetable;
   TextWriter previousFail;
   private static final HashMap<Targetable, Boolean> usabilityMapFalse = new HashMap<>();
   private static final HashMap<Targetable, Boolean> usabilityMapTrue = new HashMap<>();

   public TargetingManager(FightLog fightLog) {
      this.fightLog = fightLog;
   }

   public Targetable getSelectedTargetable() {
      return this.selectedTargetable;
   }

   public void setSelectedTargetable(Targetable selectedTargetable) {
      com.tann.dice.Main.getCurrentScreen().popAllLight();
      this.selectedTargetable = selectedTargetable;
      this.showExplanel(selectedTargetable);
      if (this.isUsable(selectedTargetable) && selectedTargetable instanceof DieTargetable) {
         EntPanelCombat ep = selectedTargetable.getSource().getEntPanel();
         ep.slideOutToTarget();
      }

      Sounds.playSound(Sounds.pip);
   }

   public void clicked(Ent ent, boolean dieSide) {
      EntState es = this.fightLog.getState(FightLog.Temporality.Present, ent);
      if (es == null || !es.isDead()) {
         if (this.getSelectedTargetable() != null && !this.getSelectedTargetable().isUsable(this.fightLog.getSnapshot(FightLog.Temporality.Present))) {
            DungeonScreen.get().popAllLight();
         }

         if (this.getSelectedTargetable() != null && dieSide) {
            this.target(ent, this.getSelectedTargetable());
         } else {
            this.clickPanel(ent, dieSide);
         }
      }
   }

   public boolean target(Ent ent, Targetable targetable) {
      if (!PhaseManager.get().getPhase().canTarget()) {
         Sounds.playSound(Sounds.error);
         Explanel exp = DungeonScreen.get().getTopExplanel();
         if (exp != null) {
            exp.addDialog(new TextWriter("[red]Can only use abilities during targeting phase", 100, Colours.blue, 2), true);
         }

         return false;
      } else if (targetable == null) {
         return false;
      } else {
         Eff effects = targetable.getDerivedEffects();
         if (effects == null) {
            return false;
         } else if (!targetable.isUsable(this.fightLog.getSnapshot(FightLog.Temporality.Present))) {
            return false;
         } else {
            String invalidReason = this.getInvalidTargetReason(ent, targetable, true);
            if (invalidReason != null) {
               this.showError("[red]" + invalidReason);
               return false;
            } else {
               this.fightLog.addCommand(targetable, ent, false);
               com.tann.dice.Main.getCurrentScreen().popAllLight();
               this.deselectTargetable();
               if (!this.fightLog.isVictoryAssured() && !this.fightLog.isFailed()) {
                  DungeonScreen.get().mildSave();
               }

               return true;
            }
         }
      }
   }

   public String getInvalidTargetReason(Ent target, Targetable targetable, boolean allowBadTargets) {
      Eff firstEffect = targetable.getDerivedEffects();
      if (firstEffect.hasKeyword(Keyword.manacost)) {
         Snapshot ss = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         if (ss.getTotalMana() < KUtils.getValue(firstEffect)) {
            return "Not enough mana";
         }
      }

      if (firstEffect.isUnusableBecauseNerfed()) {
         return "Sides with value <1 cannot be used";
      } else if (firstEffect.getType() == EffType.Blank && firstEffect.getKeywordForGameplay().size() == 0) {
         return "Side does nothing";
      } else {
         TargetingType targetingType = firstEffect.getTargetingType();
         EntState targetState = null;
         EntState sourceState = null;
         if (target != null) {
            targetState = this.fightLog.getState(FightLog.Temporality.Present, target);
         }

         if (targetable.getSource() != null) {
            sourceState = this.fightLog.getState(FightLog.Temporality.Present, targetable.getSource());
         }

         if (firstEffect.needsTarget()) {
            if (target == null) {
               return "No valid targets";
            }

            if (firstEffect.getType() != EffType.Or && firstEffect.isFriendly() != targetable.isPlayer() == target.isPlayer()) {
               return "Target " + Words.entName(firstEffect, false);
            }
         }

         Snapshot s = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         List<EntState> actualTargets = s.getActualTargets(target, firstEffect, sourceState == null ? null : sourceState.getEnt());
         if (actualTargets.size() == 0 && firstEffect.getTargetingType() != TargetingType.Untargeted) {
            if (targetState == null) {
               if (target != null && firstEffect.getRestrictions().size() > 0) {
                  if (com.tann.dice.Main.self().translator.shouldTranslate()) {
                     return "Can't target that";
                  }

                  return "Can only affect " + Words.entName(firstEffect, true) + " " + firstEffect.getRestrictions().get(0).getInvalidString(firstEffect);
               }

               return "No valid targets";
            }

            for (ConditionalRequirement tr : firstEffect.getRestrictions()) {
               if (!tr.isValid(s, sourceState, targetState, firstEffect)) {
                  return tr.getInvalidString(firstEffect);
               }
            }
         }

         List<Ent> validTargets = getValidTargets(this.fightLog.getSnapshot(FightLog.Temporality.Present), targetable, true);
         if (firstEffect.needsTarget() && !validTargets.contains(target)) {
            switch (targetingType) {
               case Single:
                  if (target == null || targetState == null) {
                     return "null err" + target;
                  } else if (firstEffect.isFriendly()) {
                     if (!target.isPlayer()) {
                        return "Target " + Words.entName(firstEffect, false);
                     }
                  } else if (target.isPlayer()) {
                     return "Target " + Words.entName(true, false, false);
                  } else if (!target.isPlayer() && !targetState.isForwards() && !firstEffect.hasKeyword(Keyword.ranged)) {
                     return "Cannot target back-row enemies";
                  }
               default:
                  return "Can't target that";
            }
         } else if (firstEffect.getType() == EffType.Reroll && !firstEffect.hasKeyword(Keyword.future)) {
            return "Can only gain rerolls during roll phase";
         } else if (firstEffect.getTargetingType() != TargetingType.Untargeted && validTargets.size() == 0) {
            return firstEffect.getNoTargetsString();
         } else {
            return firstEffect.getType() == EffType.Resurrect && s.getStates(true, true).isEmpty() ? "No defeated heroes" : null;
         }
      }
   }

   private void showError(String invalidReason) {
      if (this.previousFail != null) {
         this.previousFail.remove();
      }

      Sounds.playSound(Sounds.error);
      if (com.tann.dice.Main.getCurrentScreen().getTopPushedActor() instanceof TextWriter) {
         com.tann.dice.Main.getCurrentScreen().popAllLight();
      }

      TextWriter tw = new TextWriter(invalidReason, 80, Colours.purple, 2);
      Explanel explanel = DungeonScreen.get().getTopExplanel();
      if (explanel != null) {
         explanel.addDialog(tw, true);
      } else {
         tw.setPosition((int)(com.tann.dice.Main.width / 2 - tw.getWidth() / 2.0F), (int)(com.tann.dice.Main.height / 2 - tw.getHeight() / 2.0F));
         com.tann.dice.Main.getCurrentScreen().push(tw, false, true, true, 0.0F);
      }

      this.previousFail = tw;
   }

   public void clickPanelDie(EntDie d) {
      if (!(d.ent instanceof Monster)) {
         if (d.getSideIndex() != -1) {
            if (PhaseManager.get().getPhase().canRoll()) {
               d.toggleLock();
            } else {
               DieTargetable dt = d.getTargetable();
               Eff first = dt.getDerivedEffects();
               if (!this.isUsable(dt)) {
                  this.showError("[red]" + this.getInvalidTargetReason(null, dt, false));
               } else {
                  if (!first.needsTarget()) {
                     this.target(null, dt);
                  } else {
                     this.setSelectedTargetable(dt);
                  }
               }
            }
         }
      }
   }

   public Actor showExplanel(Targetable t) {
      return !PhaseManager.get().getPhase().canTarget() ? this.showExplanelInactive(t) : this.showExplanelActive(t);
   }

   private Actor showExplanelActive(Targetable t) {
      com.tann.dice.Main.getCurrentScreen().popAllLight();
      boolean usable = t.isUsable(this.fightLog.getSnapshot(FightLog.Temporality.Present));
      if (usable) {
         this.showTargetingHighlights();
      }

      Explanel exp;
      if (t instanceof Ability) {
         exp = new Explanel((Ability)t, true);
         if (!usable) {
            exp.addDialog("[red]not enough " + Words.manaString(), true);
         }
      } else {
         if (!(t instanceof DieTargetable)) {
            throw new RuntimeException("unable to make explanel for " + t.getClass().getSimpleName());
         }

         DieTargetable dt = (DieTargetable)t;
         exp = new Explanel(dt.getSide(), dt.getSource());
      }

      Eff first = t.getDerivedEffects();
      boolean allowBadTargets = first.allowBadTargets();
      boolean allInvalid = first.needsTarget()
         && !allowBadTargets
         && getValidTargets(this.fightLog.getSnapshot(FightLog.Temporality.Present), t, true).size() == 0
         && t.isUsable(this.fightLog.getSnapshot(FightLog.Temporality.Present));
      String noTargetsString = first.getNoTargetsString();
      if (!first.needsTarget()) {
         String inv = this.getInvalidTargetReason(null, t, true);
         if (inv != null) {
            allInvalid = true;
            noTargetsString = inv;
         }
      }

      DungeonScreen.get().push(exp, false, true, true, 0.0F);
      exp.reposition();
      if (!com.tann.dice.Main.getSettings().hasAttemptedLevel()) {
         boolean fr = first.isFriendly();
         String msg = com.tann.dice.Main.self().control.getSelectTapString() + " " + (fr ? "[yellow]" : "[purple]") + Words.entName(first, false);
         Actor a = new Pixl(3, 3).border(Colours.green).text(msg).pix();
         a.setTouchable(Touchable.disabled);
         exp.addActor(a);
         Tann.center(a);
         a.setY(-a.getHeight() - exp.getExtraBelowExtent() - 3.0F);
      }

      if (allInvalid && noTargetsString != null) {
         exp.addDialog("[red]" + noTargetsString, true);
      }

      return exp;
   }

   public Actor showExplanelInactive(Targetable t) {
      Explanel exp = DungeonScreen.get().getTopExplanel();
      if (exp != null) {
         com.tann.dice.Main.getCurrentScreen().popSingleLight();
         if (exp.isShowing(t)) {
            Sounds.playSound(Sounds.pop);
            return null;
         }
      }

      if (t instanceof DieTargetable) {
         DieTargetable dt = (DieTargetable)t;
         exp = new Explanel(dt.getSide(), dt.getSource());
      } else {
         if (!(t instanceof Ability)) {
            throw new RuntimeException("unable to make explanel for " + t.getClass().getSimpleName());
         }

         Ability s = (Ability)t;
         exp = new Explanel(s, true);
      }

      exp.reposition();
      com.tann.dice.Main.getCurrentScreen().push(exp, false, true, true, 0.0F);
      Sounds.playSound(Sounds.pip);
      return exp;
   }

   public boolean deselectTargetable() {
      if (this.selectedTargetable == null) {
         return false;
      } else {
         this.clearTargetingHighlights();
         DungeonScreen.get().pop(Explanel.class);
         this.selectedTargetable = null;
         return true;
      }
   }

   private void clickPanel(Ent ent, boolean dieSide) {
      Phase currentPhase = PhaseManager.get().getPhase();
      if (currentPhase.canRoll() && dieSide && ent.isPlayer()) {
         if (com.tann.dice.Main.getCurrentScreen().stackEmpty()) {
            ent.getDie().toggleLock();
            DungeonScreen ds = DungeonScreen.get();
            ds.getTutorialManager().onLock(ds.getFightLog().getSnapshot(FightLog.Temporality.Present).getEntities(true, false));
            ds.onLock();
         } else {
            com.tann.dice.Main.getCurrentScreen().popAllLight();
         }
      } else {
         Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         boolean canTarget = present != null && !present.isEnd() && present.getState(ent) != null && present.getState(ent).canUse() && currentPhase.canTarget();
         if (com.tann.dice.Main.getCurrentScreen().getTopPushedActor() == ent.getDiePanel()) {
            if (this.getSelectedTargetable() != null) {
               Sounds.playSound(Sounds.pop);
            }

            com.tann.dice.Main.getCurrentScreen().popAllLight();
            Sounds.playSound(Sounds.pop);
         } else if (ent.isPlayer()) {
            com.tann.dice.Main.getCurrentScreen().popAllLight();
            if (dieSide && canTarget) {
               this.clickPanelDie(ent.getDie());
            } else {
               ent.getDiePanel().show();
            }
         } else {
            if (dieSide && ent.getEntPanel().holdsDie) {
               Actor a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
               if (a instanceof Explanel) {
                  Explanel e = (Explanel)a;
                  if (e.isShowing(ent.getDie().getCurrentSide())) {
                     Sounds.playSound(Sounds.pop);
                     com.tann.dice.Main.getCurrentScreen().popAllLight();
                     return;
                  }
               }

               com.tann.dice.Main.getCurrentScreen().popAllLight();
               EntSide s = ent.getDie().getCurrentSide();
               Actor topActor = DungeonScreen.get().getTopPushedActor();
               if (topActor instanceof Explanel && ((Explanel)topActor).isShowing(s)) {
                  com.tann.dice.Main.getCurrentScreen().popAllLight();
                  Sounds.playSound(Sounds.pop);
                  return;
               }

               Sounds.playSound(Sounds.pip);
               if (s != null) {
                  Explanel e = new Explanel(s, ent);
                  DungeonScreen.get().push(e, false, true, true, 0.0F);
                  e.reposition();
                  e.addPassives(ent);
                  ent.getEntPanel().setArrowIntensity(1.0F, 0.0F);
                  return;
               }
            } else {
               com.tann.dice.Main.getCurrentScreen().popAllLight();
               ent.getDiePanel().show();
            }
         }
      }
   }

   public void clearTargetingHighlights() {
      for (Ent de : this.fightLog.getSnapshot(FightLog.Temporality.Present).getEntities(null, null)) {
         de.getEntPanel().setPossibleTarget(false);
      }
   }

   public void showTargetingHighlights() {
      this.clearTargetingHighlights();
      Targetable t = this.getSelectedTargetable();
      if (t != null && t.getBaseEffect() != null) {
         for (Ent de : getRecommendedTargets(this.fightLog.getSnapshot(FightLog.Temporality.Present), t, true)) {
            EntState es = this.fightLog.getState(FightLog.Temporality.Present, de);
            if (es != null) {
               de.getEntPanel().setPossibleTarget(true, getBonusTargetingKeywordsFor(t, es));
            } else {
               de.getEntPanel().setPossibleTarget(true);
            }
         }
      }
   }

   private static List<Keyword> getBonusTargetingKeywordsFor(Targetable t, EntState potentialTarget) {
      List<Keyword> result = new ArrayList<>();
      EntState sourceState = null;
      if (t.getSource() != null) {
         sourceState = potentialTarget.getSnapshot().getState(t.getSource());
      }

      Eff e = t.getDerivedEffects(potentialTarget.getSnapshot());

      for (Keyword k : e.getKeywordForGameplay()) {
         ConditionalBonus cb = k.getConditionalBonus();
         if (cb != null) {
            ConditionalRequirement conReq = cb.requirement;
            if (!conReq.preCalculate() && conReq.isValid(potentialTarget.getSnapshot(), sourceState, potentialTarget, e)) {
               result.add(k);
            }
         }
      }

      Tann.uniquify(result);
      return result;
   }

   public void hideTargetingArrows() {
      for (Ent de : this.fightLog.getActiveEntities()) {
         de.getEntPanel().setArrowIntensity(0.0F, 0.0F);
      }
   }

   public Ent getRandomTargetForEnemy(DieTargetable d) {
      Eff first = d.getDerivedEffects();
      if (!first.needsTarget()) {
         return null;
      } else if (first.hasKeyword(Keyword.eliminate)) {
         return this.getHpRestrictTargetForEnemy(false);
      } else if (first.hasKeyword(Keyword.heavy)) {
         return this.getHpRestrictTargetForEnemy(true);
      } else {
         for (int i = 0; i < 4; i++) {
            Ent potentialTarget = this.getRandomTargetForEnemyInternal(d, (i & 1) > 0, (i & 2) > 0);
            if (potentialTarget != null) {
               return potentialTarget;
            }
         }

         return null;
      }
   }

   private Ent getHpRestrictTargetForEnemy(boolean highest) {
      List<Ent> potentials = this.fightLog.getSnapshot(FightLog.Temporality.Present).getEntities(true, false);
      Collections.shuffle(potentials);
      Ent target = null;
      int bestHp = highest ? -999 : 999;

      for (Ent de : potentials) {
         EntState futureState = this.fightLog.getState(FightLog.Temporality.Future, de);
         int hp = futureState.getHp() - futureState.getTotalRegenThisTurn();
         if (hp > bestHp == highest) {
            bestHp = hp;
            target = de;
         }
      }

      return target;
   }

   private Ent getRandomTargetForEnemyInternal(DieTargetable dt, boolean allowOverkill, boolean allowSuboptimal) {
      Eff e = this.fightLog.getSnapshot(FightLog.Temporality.Present).getState(dt.getSource()).getSideState(dt.getSide()).getCalculatedEffect();
      if (e.needsTarget() && e.getType() != EffType.Heal && e.getType() != EffType.Summon) {
         List<Ent> validTargets = getValidTargets(this.fightLog.getSnapshot(FightLog.Temporality.Present), dt, false);
         if (!allowOverkill) {
            for (int i = validTargets.size() - 1; i >= 0; i--) {
               Ent de = validTargets.get(i);
               if (this.fightLog.getState(FightLog.Temporality.Future, de).isDead()) {
                  validTargets.remove(de);
               }
            }
         }

         if (!allowSuboptimal) {
            if (e.hasKeyword(Keyword.cleave)) {
               if (validTargets.size() < 3) {
                  return null;
               }

               validTargets.remove(0);
               validTargets.remove(validTargets.get(validTargets.size() - 1));
            }

            if (e.hasKeyword(Keyword.descend)) {
               if (validTargets.size() < 2) {
                  return null;
               }

               validTargets.remove(validTargets.get(validTargets.size() - 1));
            }
         }

         return validTargets.size() > 0 ? Tann.random(validTargets) : null;
      } else {
         return null;
      }
   }

   public boolean isUsable(Targetable t) {
      return this.isUsable(t, false);
   }

   public boolean isUsable(Targetable t, boolean onlyRecommended) {
      return this.isUsable(t, onlyRecommended, false);
   }

   public boolean isUsable(Targetable t, boolean onlyRecommended, boolean cantrip) {
      Boolean usable = null;
      HashMap<Targetable, Boolean> cache = onlyRecommended ? usabilityMapTrue : usabilityMapFalse;
      usable = cache.get(t);
      if (usable != null) {
         return usable;
      } else {
         Snapshot present = this.fightLog.getSnapshot(FightLog.Temporality.Present);
         Eff first = t.getDerivedEffects();
         if (!cantrip && first.hasKeyword(Keyword.unusable)) {
            return false;
         } else {
            if (first.getTargetingType() == TargetingType.Untargeted) {
               usable = !first.isUnusableBecauseNerfed();
               usable = usable & first.canBeUsedUntargeted(present);
            } else if (onlyRecommended) {
               usable = getRecommendedTargets(present, t, true).size() > 0;
            } else {
               usable = getValidTargets(present, t, true).size() > 0;
            }

            if (first.getType() == EffType.Reroll || first.getType() == EffType.Blank) {
               usable = false;
            }

            if (first.hasKeyword(Keyword.manacost) && present.getTotalMana() < KUtils.getValue(first)) {
               usable = false;
            }

            cache.put(t, usable);
            return usable;
         }
      }
   }

   public void anythingChanged() {
      usabilityMapFalse.clear();
      usabilityMapTrue.clear();
   }

   public static List<Ent> getValidTargets(Snapshot present, Targetable t, boolean player) {
      List<Ent> potentialTargets = new ArrayList<>();
      Eff first = t.getDerivedEffects(present);
      if (player && first.isUnusableBecauseNerfed()) {
         return new ArrayList<>();
      } else {
         TargetingType type = first.getTargetingType();
         Ent source = null;
         EntState sourcePresent = null;
         if (t.getSource() != null) {
            source = t.getSource();
            sourcePresent = present.getState(source);
         }

         List<? extends Ent> friends = present.getAliveEntities(player);
         List<? extends Ent> enemies = present.getAliveEntities(!player);
         List<? extends Ent> probably = first.isFriendly() ? friends : enemies;
         switch (type) {
            case Single:
            case Group:
               potentialTargets.addAll(probably);
               break;
            case ALL:
               potentialTargets.addAll(friends);
               potentialTargets.addAll(enemies);
               break;
            case Self:
               potentialTargets.add(source);
               break;
            case Top:
               if (probably.size() > 0) {
                  potentialTargets.add(probably.get(0));
               }
               break;
            case TopAndBot:
               List<? extends Ent> maybe = first.isFriendly() ? friends : enemies;
               if (maybe.size() > 0) {
                  potentialTargets.add(maybe.get(0));
                  potentialTargets.add(maybe.get(maybe.size() - 1));
               }
            case Untargeted:
         }

         if (first.getType() == EffType.Or) {
            potentialTargets.addAll(friends);
            potentialTargets.addAll(enemies);
         }

         for (int i = potentialTargets.size() - 1; i >= 0; i--) {
            Ent de = potentialTargets.get(i);
            EntState targetPresent = present.getState(de);
            if (!isLegalTarget(first, sourcePresent, targetPresent)) {
               potentialTargets.remove(de);
            }
         }

         return potentialTargets;
      }
   }

   private static boolean isLegalTarget(Eff eff, EntState sourcePresent, EntState targetPresent) {
      if (eff.getType() == EffType.Or) {
         return isLegalTarget(eff.getOr(false), sourcePresent, targetPresent) || isLegalTarget(eff.getOr(true), sourcePresent, targetPresent);
      } else {
         Ent target = targetPresent.getEnt();
         boolean playerSource = sourcePresent == null || sourcePresent.isPlayer();
         if (eff.isFriendly() != (target.isPlayer() == playerSource)) {
            return false;
         } else {
            switch (eff.getTargetingType()) {
               case Single:
                  boolean ranged = eff.hasKeyword(Keyword.ranged) || eff.getType() == EffType.Or && eff.getOr(false).hasKeyword(Keyword.ranged);
                  if (!ranged && !targetPresent.canBeTargetedAsForwards()) {
                     return false;
                  }
               default:
                  switch (eff.getType()) {
                     case Recharge:
                        if (!targetPresent.isUsed()) {
                           return false;
                        }
                     default:
                        for (int j = 0; j < eff.getRestrictions().size(); j++) {
                           if (!eff.getRestrictions().get(j).isValid(targetPresent.getSnapshot(), sourcePresent, targetPresent, eff)) {
                              return false;
                           }
                        }

                        return true;
                  }
            }
         }
      }
   }

   public static List<Ent> getRecommendedTargets(Snapshot present, Targetable t, boolean player) {
      Eff first = t.getDerivedEffects();
      List<Ent> recommendeds = getValidTargets(present, t, player);

      for (int i = recommendeds.size() - 1; i >= 0; i--) {
         Ent de = recommendeds.get(i);
         EntState targetPresent = present.getState(de);
         EntState targetFuture = present.getFightLog().getState(FightLog.Temporality.Future, de);
         if (!isRecommendedTarget(first, t.getSource(), targetPresent, targetFuture)) {
            recommendeds.remove(de);
         }
      }

      if (first.hasKeyword(Keyword.cleave) || first.hasKeyword(Keyword.descend)) {
         int above = 1;
         int below = first.hasKeyword(Keyword.cleave) ? 1 : 0;
         List<Ent> bonusTargets = new ArrayList<>();

         for (int ix = 0; ix < recommendeds.size(); ix++) {
            Ent de = recommendeds.get(ix);
            List<? extends EntState> states = present.getAdjacents(present.getState(de), player, true, above, below);

            for (int j = 0; j < states.size(); j++) {
               EntState potentialTarget = states.get(j);
               if (isLegalTarget(first, present.getState(t.getSource()), potentialTarget)
                  && !recommendeds.contains(potentialTarget.getEnt())
                  && !bonusTargets.contains(potentialTarget.getEnt())) {
                  bonusTargets.add(potentialTarget.getEnt());
               }
            }
         }

         recommendeds.addAll(bonusTargets);
      }

      return recommendeds;
   }

   private static boolean isRecommendedTarget(Eff eff, Ent source, EntState targetPresent, EntState targetFuture) {
      if (eff.getType() == EffType.Or) {
         return isRecommendedTarget(eff.getOr(false), source, targetPresent, targetFuture)
            || isRecommendedTarget(eff.getOr(true), source, targetPresent, targetFuture);
      } else {
         boolean playerSource = source == null || source.isPlayer();
         if (eff.isFriendly() != (playerSource == targetPresent.isPlayer())) {
            return false;
         } else if (!eff.hasKeyword(Keyword.cleanse) || !targetFuture.hasCleansableBuffs() && !targetPresent.hasCleansableBuffs()) {
            if (!eff.hasKeyword(Keyword.damage)
               && !eff.hasKeyword(Keyword.boost)
               && !eff.hasKeyword(Keyword.weaken)
               && !eff.hasKeyword(Keyword.regen)
               && !eff.hasKeyword(Keyword.manaGain)
               && !eff.hasKeyword(Keyword.vitality)) {
               if (eff.hasKeyword(Keyword.smith)) {
                  EntSideState ess = targetPresent.getCurrentSideState();
                  if (ess != null) {
                     Eff gce = ess.getCalculatedEffect();
                     EffType type = gce.getType();
                     if (type == EffType.Damage || type == EffType.Shield || gce.hasKeyword(Keyword.damage) || gce.hasKeyword(Keyword.shield)) {
                        return true;
                     }
                  }
               }

               for (Keyword k : eff.getKeywordForGameplay()) {
                  if (k.getInflict() != null) {
                     return true;
                  }
               }

               EntState sourceState = null;
               if (targetPresent != null) {
                  Snapshot snapshot = targetPresent.getSnapshot();
                  if (snapshot != null) {
                     sourceState = snapshot.getState(source);
                  }
               }

               if (eff.hasKeyword(Keyword.selfShield) && sourceState != null && sourceState.hasIncomingDamage()) {
                  return true;
               } else if (eff.hasKeyword(Keyword.selfHeal) && sourceState != null && sourceState.isDamaged()) {
                  return true;
               } else if (eff.hasKeyword(Keyword.repel) && targetPresent.getSnapshot().getAllTargeters(targetPresent.getEnt(), true).size() > 0) {
                  return true;
               } else {
                  Ent targetEnt = targetPresent.getEnt();
                  EffType toCheck = eff.getType();
                  if (eff.getType() == EffType.JustTarget) {
                     if (eff.isFriendly()) {
                        if (eff.hasKeyword(Keyword.shield)) {
                           toCheck = EffType.Shield;
                        } else if (eff.hasKeyword(Keyword.heal)) {
                           toCheck = EffType.Heal;
                        }
                     } else if (eff.hasKeyword(Keyword.damage)) {
                        toCheck = EffType.Damage;
                     }
                  }

                  switch (toCheck) {
                     case Recharge:
                        return targetPresent.isUsed();
                     case Damage:
                     case Kill:
                        return true;
                     case Shield:
                        if (targetPresent.immuneToShields()) {
                           return false;
                        }

                        return targetFuture.getBlockableDamageTaken() > targetPresent.getBlockableDamageTaken();
                     case Heal:
                        if (targetPresent.immuneToHealing()) {
                           return false;
                        }

                        return targetPresent.getHp() < targetPresent.getMaxHp() || targetPresent.allowOverheal();
                     case HealAndShield:
                        if (targetPresent.immuneToHealing() && targetPresent.immuneToShields()) {
                           return false;
                        }

                        boolean okShield = targetFuture.getBlockableDamageTaken() > targetPresent.getBlockableDamageTaken();
                        boolean okHeal = targetPresent.getHp() < targetPresent.getMaxHp() || targetPresent.allowOverheal();
                        return okShield || okHeal || targetPresent.allowOverheal();
                     case RedirectIncoming:
                        return targetEnt != source && (targetPresent.hasIncomingDamage() || targetPresent.hasIncomingBuffs());
                     case Buff:
                        return eff.getBuff().isRecommendedTarget(sourceState, targetPresent, targetFuture);
                     case Resurrect:
                        return targetPresent.isDead() && targetPresent.canResurrect();
                     case SetToHp:
                        return targetPresent.getHp() != eff.getValue();
                     default:
                        return false;
                  }
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   }
}
