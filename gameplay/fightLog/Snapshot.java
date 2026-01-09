package com.tann.dice.gameplay.fightLog;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.fightLog.command.Command;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.EndTurnCommand;
import com.tann.dice.gameplay.fightLog.command.FutureAbility;
import com.tann.dice.gameplay.fightLog.command.TargetableCommand;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpecificSpellCostChange;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Snapshot {
   private FightLog fightLog;
   private int mana;
   private int rolls;
   private List<DieTargetable> diceUsedThisTurn = new ArrayList<>();
   private List<Ability> abilitiesUsedThisTurn = new ArrayList<>();
   private List<Ability> abilitiesUsedThisFight = new ArrayList<>();
   private List<EntState> states = new ArrayList<>();
   private List<Hero> heroesAliveAtStartOfTurn;
   private List<Monster> reinforcements = new ArrayList<>();
   private List<SnapshotEvent> events = new ArrayList<>();
   private List<Global> extraGlobals = new ArrayList<>();
   private boolean playerTurn = true;
   int turn = 0;
   List<FutureAbility> futureAbilities = new ArrayList<>();
   public static final int MAX_ALIVE_HEROES = 20;
   List<Ent> aliveEntities;
   private List<EntState> aliveHeroStates = null;
   private List<EntState> aliveMonsterStates = null;
   List<Hero> aliveHeroEntities;
   List<Monster> aliveMonsterEntities;
   private static final int HP_RECURSION_LIMIT = 50;
   private static final int BASE_MAX_FULLNESS_PIXELS = 165;
   private static final int BASE_MAX_FULLNESS_UNITS = 28;
   public static final boolean USE_UNITS = false;
   public static final int BASE_MAX_FULLNESS = 165;
   Integer cachedMaxFullness;
   List<Global> cachedGlobals = null;
   private static final int STARTING_MANA = 3;
   Integer maxMana = null;
   private static final int BASE_MAX_ROLLS = 2;
   private Integer maxRolls;
   boolean refusedSurrender;
   List<Command> appliedCommandsThisTurn = new ArrayList<>();
   List<Command> appliedCommandsTotal = new ArrayList<>();
   List<Ent> tmp = new ArrayList<>();
   List<Ent> tmp2 = new ArrayList<>();

   public long getShifterSeed(int sideIndex, EntState entState) {
      long result = 1L;
      Level l = this.fightLog.dungeonContext.getCurrentLevel();

      for (MonsterType monsterType : l.getMonsterList()) {
         result = result * -7046029254386353131L + monsterType.getName().hashCode();
      }

      result += this.turn;

      for (Modifier currentModifier : this.fightLog.getContext().getCurrentModifiers()) {
         result *= currentModifier.getName().hashCode() * -7046029254386353130L + 74565L;
      }

      Ent ent = entState.getEnt();
      if (ent instanceof Hero) {
         result += this.heroesAliveAtStartOfTurn.indexOf(ent);
      } else {
         try {
            int index = this.fightLog.getSnapshot(FightLog.Temporality.StartOfTurn).getAliveMonsterEntities().indexOf(ent);
            result += index;
         } catch (Exception var9) {
            TannLog.error(var9, "monseed");
         }
      }

      for (Hero hero : this.fightLog.getContext().getParty().getHeroes()) {
         result *= hero.name.hashCode() * -7046029254386353130L + 74565L;
      }

      return result + sideIndex;
   }

   public Snapshot(FightLog fightLog) {
      this.fightLog = fightLog;
   }

   public void setupCombat() {
      this.extraGlobals.clear();
      this.abilitiesUsedThisTurn.clear();
      this.diceUsedThisTurn.clear();
      this.abilitiesUsedThisFight.clear();
      this.heroesAliveAtStartOfTurn = this.getAliveHeroEntities();
   }

   public void startTurn() {
      this.appliedCommandsThisTurn.clear();
      this.turn++;
      this.resetCachedLists();
      this.heroesAliveAtStartOfTurn = this.getAliveHeroEntities();
      if (this.turn == 1) {
         for (EntState es : new ArrayList<>(this.states)) {
            es.startOfFight();
         }
      }

      this.resetMaxRolls();
      this.resetRolls();
      this.processFutureAbilities();

      for (boolean player : Tann.BOTH) {
         for (EntState es : this.getStates(this.getEntities(player, null))) {
            es.startTurn(this.turn);
         }
      }

      List<Global> globals = this.getGlobals();

      for (int i = 0; i < globals.size(); i++) {
         globals.get(i).startOfTurnGeneral(this, this.turn);
      }

      for (EntState state : this.states) {
         state.notJustSummoned();
      }

      this.abilitiesUsedThisTurn.clear();
      this.diceUsedThisTurn.clear();
   }

   private void processFutureAbilities() {
      for (FutureAbility fa : this.futureAbilities) {
         this.target(fa.target, fa.targetable, false);
      }

      this.futureAbilities.clear();
   }

   public List<EntState> target(Ent target, Targetable targetable, boolean useDie) {
      if (targetable instanceof DieTargetable) {
         this.diceUsedThisTurn.add((DieTargetable)targetable);
      }

      Eff eff = targetable.getDerivedEffects(this).copy();
      List<EntState> totalTargets = new ArrayList<>();
      Ent source = targetable.getSource();
      EntState sourceState = null;
      if (source != null) {
         sourceState = this.getState(source);
      }

      if (target == null && eff.needsTarget() && eff.hasRestriction(TargetingRestriction.LeastHp)) {
         EntState bestTargetState = this.getEntWithHp(targetable.isPlayer() == eff.isFriendly(), false, false);
         if (bestTargetState != null) {
            target = bestTargetState.getEnt();
         }
      }

      int keywordValue = eff.getValue();
      List<EntState> firstTargets = this.getActualTargets(target, eff, targetable.getSource());
      if (!firstTargets.isEmpty()) {
         for (Keyword k : eff.getKeywords()) {
            boolean valid = true;
            ConditionalBonus cb = k.getConditionalBonus();
            if (cb != null && (!cb.requirement.preCalculate() || targetable instanceof Ability)) {
               for (int i = 0; i < firstTargets.size(); i++) {
                  EntState es = firstTargets.get(i);
                  valid &= cb.isValid(sourceState, es, eff);
               }

               if (valid) {
                  keywordValue += cb.affectValue(eff, sourceState, firstTargets.get(0), keywordValue);
               }
            }
         }
      }

      if (targetable instanceof DieTargetable) {
         DieTargetable dt = (DieTargetable)targetable;
         EntSide side = dt.getSide();
         if (sourceState.skipTurn()) {
            return totalTargets;
         }

         if (useDie) {
            sourceState.useSide(side);
            int threshold = 1;
            if (eff.hasKeyword(Keyword.quadUse)) {
               threshold = 4;
            } else if (eff.hasKeyword(Keyword.doubleUse)) {
               threshold = 2;
            } else if (eff.hasKeyword(Keyword.hyperUse)) {
               threshold = KUtils.getValue(eff);
            }

            sourceState.useDie(threshold);
         }

         sourceState.afterUse(side);
      }

      int kv = KUtils.getValue(eff, keywordValue);
      if (eff.hasKeyword(Keyword.duplicate) && targetable instanceof DieTargetable) {
         EntSide original = ((DieTargetable)targetable).getSide();
         EntSideState ess2 = new EntSideState(sourceState, original);
         ess2.changeTo(sourceState.getSideState(original));
         EntSide es = new EntSide(ess2.getCalculatedTexture(), ess2.getCalculatedEffect(), EntSize.reg);
         this.target(
            null,
            new SimpleTargetable(targetable.getSource(), new EffBill().group().buff(new Buff(1, new AffectSides(new ReplaceWith(es)))).friendly().bEff()),
            false
         );
      }

      for (Keyword keyword : eff.getKeywordForGameplay()) {
         Keyword meta = keyword.getMetaKeyword();
         if (meta != null && keyword.name().startsWith("self")) {
            sourceState.hit(new EffBill().nothing().value(kv).keywords(meta).bEff(), source);
         }
      }

      if (eff.hasKeyword(Keyword.selfShield)) {
         sourceState.hit(new EffBill().shield(kv).bEff(), source);
      }

      if (eff.hasKeyword(Keyword.manaGain)) {
         this.untargetedUse(new EffBill().mana(kv).bEff(), null);
      }

      if (eff.hasKeyword(Keyword.manacost)) {
         this.untargetedUse(new EffBill().mana(-kv).bEff(), null);
      }

      if (eff.hasKeyword(Keyword.hyperBoned)) {
         this.untargetedUse(new EffBill().summon("bones", kv).bEff(), null);
      }

      if (eff.hasKeyword(Keyword.boned)) {
         this.untargetedUse(new EffBill().summon("bones", 1).bEff(), null);
      }

      if (eff.hasKeyword(Keyword.future)) {
         this.futureAbilities.add(new FutureAbility(targetable, target, this));
         return new ArrayList<>();
      } else {
         if (!eff.needsTarget()
            && eff.getTargetingType() != TargetingType.Self
            && eff.getTargetingType() != TargetingType.Group
            && eff.getTargetingType() != TargetingType.ALL) {
            this.untargetedUse(eff, targetable.getSource());
         }

         if (eff.getTargetingType() == TargetingType.Self) {
            for (EntState es : this.getActualTargets(target, eff, targetable.getSource())) {
               if (!totalTargets.contains(es)) {
                  totalTargets.add(es);
               }

               es.hit(eff, targetable.getSource(), targetable);
            }
         } else if (eff.getTargetingType() == TargetingType.SpellSource) {
            if (!(targetable instanceof Spell)) {
               TannLog.log("spellsource coming from not spell? " + eff, TannLog.Severity.error);
               return new ArrayList<>();
            }

            Spell spell = (Spell)targetable;
            EntState es = this.getSpellSource(spell);
            if (es == null) {
               TannLog.log("Spellsource ent has no state: " + eff, TannLog.Severity.error);
            } else {
               es.hit(eff, null);
            }
         }

         List<EntState> actualTargets = this.getActualTargets(target, eff, targetable.getSource());
         if (eff.getTargetingType() != TargetingType.Self) {
            for (EntState es : actualTargets) {
               if (!totalTargets.contains(es)) {
                  totalTargets.add(es);
               }

               es.hit(eff, targetable.getSource(), targetable);
            }
         }

         if (eff.getBonusUntargetedEffect() != null) {
            Eff bonusUntargetedEffect = eff.getBonusUntargetedEffect();
            List<EntState> actualTargetsx = this.getActualTargets(target, bonusUntargetedEffect, targetable.getSource());
            if (!bonusUntargetedEffect.needsTarget()
               && bonusUntargetedEffect.getTargetingType() != TargetingType.Self
               && bonusUntargetedEffect.getTargetingType() != TargetingType.Group
               && bonusUntargetedEffect.getTargetingType() != TargetingType.ALL
               && bonusUntargetedEffect.getTargetingType() != TargetingType.Top) {
               this.untargetedUse(bonusUntargetedEffect, targetable.getSource());
            } else {
               for (EntState es : actualTargetsx) {
                  if (!totalTargets.contains(es)) {
                     totalTargets.add(es);
                  }

                  es.hit(bonusUntargetedEffect, targetable.getSource(), targetable);
               }
            }
         }

         if (eff.hasKeyword(Keyword.pain)) {
            sourceState.takePain(KUtils.getValue(eff, keywordValue));
         }

         if (eff.hasKeyword(Keyword.selfHeal)) {
            sourceState.hit(new EffBill().heal(kv).bEff(), source);
         }

         this.resetCachedLists();
         return totalTargets;
      }
   }

   public EntState getSpellSource(Spell spell) {
      for (Ent h : this.getEntities(true, null)) {
         for (Personal t : this.getState(h).getActivePersonals()) {
            Spell s = t.getSpell();
            if (s == spell) {
               return this.getState(h);
            }
         }
      }

      return null;
   }

   public void untargetedUse(Eff e, Ent source) {
      if (e.getTargetingType() != TargetingType.Self && e.getTargetingType() != TargetingType.Group) {
         int value = e.getValue();
         if (!e.hasValue() || value != 0) {
            switch (e.getType()) {
               case SnapshotEvent:
                  this.addEvent(e.getSnapshotEvent());
                  break;
               case Mana:
                  this.gainMana(value);
                  break;
               case Summon:
                  List<Ent> summons = e.getSummons();
                  if (!summons.isEmpty()) {
                     EntState sourceState = this.getState(source);
                     if (summons.get(0) instanceof Monster) {
                        for (int ix = 0; ix < summons.size(); ix++) {
                           int summonerAbsoluteIndex = this.states.indexOf(sourceState);
                           boolean lastSummon = ix == summons.size() - 1;
                           boolean staggerSummons = false;
                           boolean summonBelow = true;
                           int var19 = summonerAbsoluteIndex + 1;
                           Ent de = summons.get(ix);
                           de.setSummonedBy(source);
                           de.setRealFightLog(this.fightLog);
                           if (this.canSupport(de.getSize())) {
                              EntState es = new EntState(de, this);
                              this.states.add(var19, es);
                              es.startOfFight();
                           } else {
                              this.reinforcements.add((Monster)de);
                           }

                           this.resetCachedLists();
                        }
                     } else {
                        for (int ix = 0; ix < summons.size() && this.getMaxExtraHeroes() > 0; ix++) {
                           Hero h = (Hero)summons.get(ix);
                           h.setRealFightLog(this.fightLog);
                           EntState es = new EntState(h, this);
                           es.useDie();
                           this.states.add(es);
                           es.startOfFight();
                           this.resetCachedLists();
                        }
                     }
                  }
                  break;
               case Reroll:
                  this.addRolls(value);
                  break;
               case Resurrect:
                  List<EntState> dead = this.getStates(source == null || source.isPlayer(), true);
                  int leftToResurrect = value;

                  for (int i = 0; i < dead.size() && leftToResurrect > 0 && this.getMaxExtraHeroes() > 0; i++) {
                     EntState es = dead.get(i);
                     if (es.canResurrect()) {
                        es.resurrect();
                        leftToResurrect--;
                     }
                  }
                  break;
               case Enchant:
                  List<Global> gs = e.getEnchantMod().getGlobals();
                  this.addExtraGlobal(gs);
            }
         }
      } else {
         this.target(null, new SimpleTargetable(source, e), false);
      }
   }

   private int getMaxExtraHeroes() {
      return Math.max(0, 20 - this.getAliveHeroStates().size());
   }

   private void gainMana(int value) {
      this.mana += value;

      for (Global gt : this.getGlobals()) {
         this.mana = gt.affectFinalMana(this.mana);
      }
   }

   public void addEvent(SnapshotEvent event) {
      this.events.add(event);
   }

   public List<EntState> getActualTargets(Ent targetEnt, Eff eff, Ent source) {
      boolean player = false;
      if (source == null || source.isPlayer()) {
         player = true;
      }

      if (eff.needsTarget() && targetEnt == null) {
         return new ArrayList<>();
      } else {
         List<EntState> friends = this.getAliveEntStates(player);
         List<EntState> aliveEnemies = this.getAliveEntStates(!player);
         List<EntState> startOfTurnHeroes = this.getStates(this.getHeroesAliveAtStartOfTurn());
         List<EntState> enemies = player ? aliveEnemies : startOfTurnHeroes;
         List<EntState> potentials = eff.isFriendly() ? friends : enemies;
         EntState target = this.getState(targetEnt);
         EntState sourceState = this.getState(source);
         TargetingType type = eff.getTargetingType();
         List<EntState> result = new ArrayList<>();
         switch (type) {
            case Single:
               result.add(target);
               break;
            case Top:
               if (aliveEnemies.size() > 0) {
                  result.add(aliveEnemies.get(0));
               }
               break;
            case TopAndBot:
               if (potentials.size() > 0) {
                  result.add(potentials.get(0));
               }

               if (potentials.size() > 1) {
                  result.add(potentials.get(potentials.size() - 1));
               }
               break;
            case Bot:
               if (aliveEnemies.size() > 0) {
                  result.add(aliveEnemies.get(aliveEnemies.size() - 1));
               }
               break;
            case Mid:
               if (aliveEnemies.size() > 0) {
                  result.add(aliveEnemies.get(aliveEnemies.size() / 2));
               }
               break;
            case Self:
               result.add(sourceState);
               break;
            case Group:
               if (player && eff.isFriendly() && eff.getType() == EffType.Damage) {
                  result.addAll(this.getStates(true, null));
               } else {
                  result.addAll(eff.isFriendly() ? friends : enemies);
               }
               break;
            case ALL:
               result.addAll(friends);
               result.addAll(enemies);
            case Untargeted:
         }

         if (eff.hasKeyword(Keyword.cleave) && result.size() == 1) {
            result.addAll(this.getAdjacents(result.remove(0), player, true, 1, 1));
         }

         if (eff.hasKeyword(Keyword.descend) && result.size() == 1) {
            result.addAll(this.getAdjacents(result.remove(0), player, true, 0, 1));
         }

         if (!eff.needsTarget() && (source == null || source.isPlayer())) {
            for (int i = result.size() - 1; i >= 0; i--) {
               EntState es = result.get(i);

               for (ConditionalRequirement restriction : eff.getRestrictions()) {
                  if (!restriction.isValid(this, sourceState, es, eff)) {
                     result.remove(es);
                     break;
                  }
               }
            }
         }

         for (int i = result.size() - 1; i >= 0; i--) {
            if (result.get(i) == null) {
               result.remove(i);
            }
         }

         return result;
      }
   }

   public List<EntState> getAdjacents(EntState sourceState) {
      return this.getAdjacents(sourceState, 1);
   }

   public List<EntState> getAdjacents(EntState sourceState, int dist) {
      List<EntState> adjacents = new ArrayList<>();
      int index = this.states.indexOf(sourceState);
      if (index == -1) {
         throw new RuntimeException("no state found for dead monster???");
      } else {
         for (int delta = -1; delta <= 1; delta += 2) {
            int found = 0;

            for (int i = index + delta; i >= 0 && i < this.states.size(); i += delta) {
               EntState potentialState = this.states.get(i);
               if (potentialState.getEnt().isPlayer() == sourceState.getEnt().isPlayer() && !potentialState.isDead()) {
                  adjacents.add(potentialState);
                  if (++found >= dist) {
                     break;
                  }
               }
            }
         }

         return adjacents;
      }
   }

   public List<? extends EntState> getAdjacents(EntState target, boolean updateWithRecentDeaths, boolean includeSelf, int amountAbove, int amountBelow) {
      if (target != null && target.getEnt() != null) {
         boolean playerTarget = target.getEnt().isPlayer();
         List<EntState> team;
         if (!updateWithRecentDeaths && playerTarget) {
            team = this.getStates(this.heroesAliveAtStartOfTurn);
         } else {
            team = playerTarget ? this.getAliveHeroStates() : this.getAliveMonsterStates();
         }

         int teamIndex = team.indexOf(target);
         return getInDirectionsFromIndex(team, teamIndex, amountBelow, amountAbove, !updateWithRecentDeaths, includeSelf);
      } else {
         return new ArrayList<>();
      }
   }

   private static List<EntState> getInDirectionsFromIndex(
      List<EntState> states, int index, int amountBelow, int amountAbove, boolean allowDead, boolean includeSelf
   ) {
      if (index >= 0 && index < states.size()) {
         List<EntState> results = new ArrayList<>();

         for (int i = 0; i < amountAbove; i++) {
            EntState tmp = getSingleInDir(states, allowDead, index, -(amountAbove - i));
            if (tmp != null) {
               results.add(tmp);
            }
         }

         EntState me = states.get(index);
         if (includeSelf && (allowDead || !me.isDead())) {
            results.add(me);
         }

         for (int ix = 0; ix < amountBelow; ix++) {
            EntState tmp = getSingleInDir(states, allowDead, index, amountBelow - ix);
            if (tmp != null) {
               results.add(tmp);
            }
         }

         return results;
      } else {
         throw new RuntimeException("uhoh direction " + index + ":" + states.size());
      }
   }

   private static EntState getSingleInDir(List<EntState> states, boolean allowDead, int index, int delta) {
      int dir = delta > 0 ? 1 : -1;
      int numFound = 0;

      for (int i = index + dir; i < states.size() && i >= 0; i += dir) {
         EntState es = states.get(i);
         if (allowDead || !es.isDead()) {
            if (++numFound == Math.abs(delta)) {
               return es;
            }
         }
      }

      return null;
   }

   public EntState getState(Ent ent) {
      for (int i = 0; i < this.states.size(); i++) {
         EntState es = this.states.get(i);
         if (es.getEnt() == ent) {
            return es;
         }
      }

      return null;
   }

   private List<EntState> getStates(List<? extends Ent> entities) {
      List<EntState> results = new ArrayList<>();

      for (Ent de : entities) {
         results.add(this.getState(de));
      }

      return results;
   }

   public Snapshot copy() {
      Snapshot result = new Snapshot(this.fightLog);
      result.turn = this.turn;
      result.mana = this.mana;
      result.rolls = this.rolls;
      result.maxRolls = this.maxRolls;

      for (EntState state : this.states) {
         EntState copy = state.copy();
         copy.setSnapshot(result);
         result.states.add(copy);
      }

      result.extraGlobals = new ArrayList<>(this.extraGlobals);
      result.reinforcements = new ArrayList<>(this.reinforcements);
      result.heroesAliveAtStartOfTurn = this.heroesAliveAtStartOfTurn;
      result.abilitiesUsedThisTurn = new ArrayList<>(this.abilitiesUsedThisTurn);
      result.diceUsedThisTurn = new ArrayList<>(this.diceUsedThisTurn);
      result.abilitiesUsedThisFight = new ArrayList<>(this.abilitiesUsedThisFight);
      result.futureAbilities = new ArrayList<>(this.futureAbilities);
      result.events = new ArrayList<>(this.events);
      result.refusedSurrender = this.refusedSurrender;
      result.playerTurn = this.playerTurn;
      result.appliedCommandsThisTurn = new ArrayList<>(this.appliedCommandsThisTurn);
      result.appliedCommandsTotal = new ArrayList<>(this.appliedCommandsTotal);
      result.resetCachedLists();
      return result;
   }

   public void addEntities(List<? extends Ent> entities) {
      for (Ent m : entities) {
         if (m.isPlayer()) {
            this.states.add(new EntState(m, this));
         } else {
            this.reinforcements.add((Monster)m);
         }
      }

      this.updateAllBaseStats();
      this.resetCachedLists();
      this.setupFromReinforcements();
   }

   public void endTurn(boolean player) {
      if (player) {
         this.mana = Math.min(this.mana, this.getMaxMana());
      } else {
         List<Global> globs = this.getGlobals();

         for (int i = 0; i < globs.size(); i++) {
            globs.get(i).endOfTurnGeneral(this, this.turn);
         }

         for (int i = 0; i < 2; i++) {
            for (EntState es : new ArrayList<>(this.states)) {
               if (es.getEnt().isPlayer() == (i == 1)) {
                  es.endTurn();
               }
            }
         }
      }

      if (player != this.playerTurn) {
         TannLog.error("hm invalid turn state?");
      } else {
         this.playerTurn = !player;
      }

      this.resetCachedLists();
   }

   public boolean anyHidingEnemies() {
      if (this.getAliveMonsterEntities().size() <= 1) {
         return false;
      } else {
         for (EntState es : this.getAliveMonsterStates()) {
            if (!es.isDead() && !es.isForwards()) {
               return true;
            }
         }

         return false;
      }
   }

   public List<Hero> getHeroesAliveAtStartOfTurn() {
      return this.heroesAliveAtStartOfTurn;
   }

   public List<EntState> getAliveEntStates(boolean player) {
      return player ? this.getAliveHeroStates() : this.getAliveMonsterStates();
   }

   public List<Ent> getAliveEntities() {
      if (this.aliveEntities == null) {
         this.aliveEntities = new ArrayList<>();

         for (EntState state : this.states) {
            if (!state.isDead()) {
               this.aliveEntities.add(state.getEnt());
            }
         }
      }

      return this.aliveEntities;
   }

   public List<EntState> getAliveHeroStates() {
      if (this.aliveHeroStates == null) {
         this.aliveHeroStates = new ArrayList<>();

         for (EntState state : this.states) {
            if (!state.isDead() && state.getEnt().isPlayer()) {
               this.aliveHeroStates.add(state);
            }
         }
      }

      return this.aliveHeroStates;
   }

   public List<EntState> getAliveMonsterStates() {
      if (this.aliveMonsterStates == null) {
         this.aliveMonsterStates = new ArrayList<>();

         for (EntState state : this.states) {
            if (!state.isDead() && !state.getEnt().isPlayer()) {
               this.aliveMonsterStates.add(state);
            }
         }
      }

      return this.aliveMonsterStates;
   }

   public List<Hero> getAliveHeroEntities() {
      if (this.aliveHeroEntities == null) {
         this.aliveHeroEntities = new ArrayList<>();

         for (EntState state : this.getAliveHeroStates()) {
            this.aliveHeroEntities.add((Hero)state.getEnt());
         }
      }

      return this.aliveHeroEntities;
   }

   public List<Monster> getAliveMonsterEntities() {
      if (this.aliveMonsterEntities == null) {
         this.aliveMonsterEntities = new ArrayList<>();

         for (EntState state : this.getAliveMonsterStates()) {
            this.aliveMonsterEntities.add((Monster)state.getEnt());
         }
      }

      return this.aliveMonsterEntities;
   }

   public List<? extends Ent> getAliveEntities(boolean player) {
      return player ? this.getAliveHeroEntities() : this.getAliveMonsterEntities();
   }

   private void resetCachedLists() {
      this.aliveHeroStates = null;
      this.aliveHeroEntities = null;
      this.aliveEntities = null;
      this.aliveMonsterEntities = null;
      this.aliveMonsterStates = null;
      this.cachedGlobals = null;
      this.cachedMaxFullness = null;
      this.somethingChangedAllEntities();
   }

   public void somethingChangedAllEntities() {
      for (EntState es : this.states) {
         es.somethingChanged();
      }
   }

   public void checkHpLimits(Ent source, Command command) {
      for (int r = 0; r < 50; r++) {
         boolean recheckRequired = false;

         for (int i = this.states.size() - 1; i >= 0; i--) {
            recheckRequired |= this.states.get(i).checkHpLimits(this.getState(source), command);
         }

         if (!recheckRequired) {
            this.refreshFromReinforcements();
            return;
         }

         this.resetCachedLists();
      }

      throw new RuntimeException("HP Check recursion limit reached");
   }

   private void refreshFromReinforcements() {
      if (this.reinforcements.size() > 0) {
         Monster m = this.reinforcements.get(0);
         if (this.canSupport(m.getSize())) {
            EntState es = new EntState(m, this);
            this.states.add(0, es);
            this.aliveMonsterStates = null;
            this.aliveMonsterEntities = null;
            this.reinforcements.remove(m);
            this.refreshFromReinforcements();
            this.resetCachedLists();
            es.startOfFight();
         }
      }
   }

   private void setupFromReinforcements() {
      while (this.reinforcements.size() > 0 && this.canSupport(this.reinforcements.get(0).getSize())) {
         Monster m = this.reinforcements.remove(0);
         EntState es = new EntState(m, this);
         this.states.add(0, es);
         this.aliveMonsterStates = null;
         this.aliveMonsterEntities = null;
      }

      this.resetCachedLists();
   }

   public void endLevel() {
      for (EntState es : this.states) {
         es.endLevel();
      }
   }

   public void onDeath(EntState entState, EntState killer) {
      for (EntState es : this.getStates(null, false)) {
         if (es != entState) {
            es.onOtherDeath(entState);
         }
      }
   }

   public List<Ent> getEntities(Boolean hero, Boolean dead) {
      List<Ent> results = new ArrayList<>();

      for (EntState es : this.getStates(hero, dead)) {
         results.add(es.getEnt());
      }

      return results;
   }

   public List<EntState> getStates(Boolean hero, Boolean dead) {
      List<EntState> results = new ArrayList<>();

      for (int i = 0; i < this.states.size(); i++) {
         EntState es = this.states.get(i);
         if ((dead == null || es.isDead() == dead) && (hero == null || es.getEnt().isPlayer() == hero)) {
            results.add(es);
         }
      }

      return results;
   }

   public boolean isEnd() {
      return this.isVictory() || this.isLoss();
   }

   public boolean isVictory() {
      return this.getAliveMonsterStates().size() == 0;
   }

   public boolean isLoss() {
      return this.getAliveHeroStates().size() == 0;
   }

   public List<EntState> getPoisoned() {
      List<EntState> result = new ArrayList<>();

      for (EntState es : this.states) {
         if (!es.isDead() && es.getPoisonDamageTaken(true) > es.getTotalRegenThisTurn()) {
            result.add(es);
         }
      }

      return result;
   }

   public List<EntState> getRegened() {
      List<EntState> result = new ArrayList<>();

      for (EntState es : this.states) {
         if (!es.isDead() && es.getTotalRegenThisTurn() > es.getPoisonDamageTaken(true)) {
            result.add(es);
         }
      }

      return result;
   }

   public int getTurn() {
      return this.turn;
   }

   private int getFullness() {
      int total = 0;

      for (EntState es : this.getAliveMonsterStates()) {
         if (!es.isDead()) {
            total += es.getEnt().getSize().getReinforceSize();
         }
      }

      return total;
   }

   private boolean canSupport(EntSize size) {
      return this.getFullness() + size.getReinforceSize() <= this.getMaxFullness();
   }

   private int getMaxFullness() {
      if (this.cachedMaxFullness != null) {
         return this.cachedMaxFullness;
      } else {
         int result = 165;

         for (Global gt : this.getGlobals()) {
            result = gt.affectReinforcements(result);
         }

         this.cachedMaxFullness = result;
         return result;
      }
   }

   public List<Monster> getReinforcements() {
      return this.reinforcements;
   }

   public void spendManaCost(Spell spell) {
      int manaCost = this.getAbilityCost(spell);
      if (this.mana - manaCost < 0) {
         throw new RuntimeException("negative mana???");
      } else {
         this.mana -= manaCost;
      }
   }

   public void onUseAbility(Ability ability) {
      int totalCost = this.getAbilityCost(ability);
      List<EntState> states = this.getStates(null, false);

      for (int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
         EntState es = states.get(stateIndex);
         List<Personal> personals = es.getActivePersonals();

         for (int ti = 0; ti < personals.size(); ti++) {
            personals.get(ti).onSpendAbilityCost(totalCost, this, es);
         }
      }
   }

   public int getTotalMana() {
      return this.mana;
   }

   public FightLog getFightLog() {
      return this.fightLog;
   }

   public EntSideState getSideState(TargetableCommand targetableCommand) {
      Ent source = targetableCommand.getSource();
      if (targetableCommand instanceof DieCommand) {
         DieCommand dc = (DieCommand)targetableCommand;
         return this.getState(source).getSideState(dc.dt.getSide());
      } else {
         return null;
      }
   }

   public boolean isAbilityAvailable(Ability ability) {
      for (Global gt : this.getGlobals()) {
         if (!gt.canUseAbility(ability, this)) {
            return false;
         }
      }

      Eff first = ability.getDerivedEffects(this.getGlobals());
      boolean singleCast = first.hasKeyword(Keyword.singleCast);
      if (singleCast && this.getTimesAbilityUsed(ability) > 0) {
         return false;
      } else {
         boolean cooldown = first.hasKeyword(Keyword.cooldown);
         return !cooldown || this.getNumTimesAbilityUsedThisTurn(ability) <= 0;
      }
   }

   private int getTimesAbilityUsed(Ability ability) {
      return Tann.countInList(ability, this.abilitiesUsedThisFight);
   }

   public List<Global> getGlobals() {
      if (this.cachedGlobals == null) {
         this.cachedGlobals = new ArrayList<>();
         DungeonContext context = this.fightLog.getContext();
         int level = context.getCurrentMod20LevelNumber();
         this.cachedGlobals.addAll(context.getModifierGlobals());
         addLinked(this.cachedGlobals, level, context, this.turn);
         List<Global> entityAndExtraGlobals = new ArrayList<>();
         List<EntState> states = this.getStates(null, false);

         for (int i = 0; i < states.size(); i++) {
            EntState es = states.get(i);
            List<Personal> activeTriggers = es.getActivePersonals();
            int j = 0;

            for (int activeTriggersSize = activeTriggers.size(); j < activeTriggersSize; j++) {
               Personal pt = activeTriggers.get(j);
               Global gt = pt.getGlobalFromPersonalTrigger();
               if (gt != null) {
                  entityAndExtraGlobals.add(gt);
               }
            }
         }

         entityAndExtraGlobals.addAll(this.extraGlobals);
         addLinked(entityAndExtraGlobals, context.getCurrentMod20LevelNumber(), context, this.turn);
         this.cachedGlobals.addAll(entityAndExtraGlobals);
         Collections.sort(this.cachedGlobals, Trigger.sorter);
      }

      return this.cachedGlobals;
   }

   public static void addLinked(List<Global> source, int level, DungeonContext context, int turn) {
      for (int i = 0; i < source.size(); i++) {
         Global gt = source.get(i).getLinkedGlobal(context, turn);
         if (gt != null) {
            source.add(i + 1, gt);
         } else {
            List<Global> lst = source.get(i).getLinkedGlobalList(level, context, turn);
            if (lst != null) {
               source.addAll(i + 1, lst);
            }
         }
      }
   }

   public void updateAllBaseStats() {
      this.resetCachedLists();

      for (EntState es : this.states) {
         es.updateBaseStats();
      }
   }

   public int getMaxMana() {
      if (this.maxMana != null) {
         return this.maxMana;
      } else {
         this.maxMana = 3;
         List<Global> globs = this.getGlobals();

         for (int i = 0; i < globs.size(); i++) {
            this.maxMana = globs.get(i).affectMaxMana(this.maxMana);
         }

         this.maxMana = Math.max(0, this.maxMana);
         return this.maxMana;
      }
   }

   public void afterUseAbility(Ability ability) {
      this.onUseAbility(ability);

      for (EntState es : new ArrayList<>(this.states)) {
         if (!es.isDead()) {
            es.afterAbility(ability);
         }
      }

      Eff first = ability.getDerivedEffects(this);
      int delta = 0;
      if (first.hasKeyword(Keyword.deplete)) {
         delta++;
      }

      if (first.hasKeyword(Keyword.channel)) {
         delta--;
      }

      if (delta != 0) {
         this.addExtraGlobal(new GlobalSpecificSpellCostChange(delta, ability.getTitle()));
      }

      this.abilitiesUsedThisTurn.add(ability);
      this.abilitiesUsedThisFight.add(ability);
   }

   private void addExtraGlobal(List<Global> g) {
      this.extraGlobals.addAll(g);
      this.resetCachedLists();
   }

   private void addExtraGlobal(Global g) {
      this.extraGlobals.add(g);
      this.resetCachedLists();
   }

   public boolean canSaveAHero(EffType with, Snapshot future) {
      List<TP<EntState, EntState>> dyingHeroes = new ArrayList<>();
      List<Ent> dying = new ArrayList<>(this.getAliveEntities(true));
      dying.removeAll(future.getAliveEntities(true));
      if (dying.size() == 0) {
         return false;
      } else {
         for (Ent de : dying) {
            dyingHeroes.add(new TP<>(this.getState(de), future.getState(de)));
         }

         switch (with) {
            case Shield:
               int shieldsAvailable = this.getAvailable(EffType.Shield);
               if (shieldsAvailable == 0) {
                  return false;
               } else {
                  for (TP<EntState, EntState> tx : dyingHeroes) {
                     int blockableTaken = tx.b.getBlockableDamageTaken();
                     int killedBy = -tx.b.getHp() + 1;
                     if (killedBy <= blockableTaken && killedBy <= shieldsAvailable) {
                        return true;
                     }
                  }

                  return false;
               }
            case Heal:
               int healsAvailable = this.getAvailable(EffType.Heal);
               if (healsAvailable == 0) {
                  return false;
               } else {
                  for (TP<EntState, EntState> t : dyingHeroes) {
                     int maxHealing = t.a.getMaxHp() - t.a.getHp();
                     int killedBy = -t.b.getHp() + 1;
                     if (killedBy <= maxHealing && killedBy <= healsAvailable) {
                        return true;
                     }
                  }

                  return false;
               }
            case Damage:
               return this.getKillableMonsters() >= 2;
            default:
               throw new RuntimeException("future save not implemented for " + with);
         }
      }
   }

   public int getAvailable(EffType type) {
      int total = 0;

      for (Ent de : this.getEntities(true, false)) {
         EntSide es = de.getDie().getCurrentSide();
         if (es != null) {
            Eff e = this.getState(de).getSideState(es).getCalculatedEffect();
            if (e.getType() == type) {
               total += e.getValue();
            }
         }
      }

      if (type == EffType.Damage || type == EffType.Shield) {
         total += this.getTotalMana() / 2 * 2;
      }

      return total;
   }

   public int getKillableMonsters() {
      int killables = 0;
      int damageAvailable = this.getAvailable(EffType.Damage);

      for (EntState es : this.getStates(false, false)) {
         if (es.getHp() <= damageAvailable) {
            killables++;
         }
      }

      return killables;
   }

   public int getNumAbilitiesUsed() {
      return this.abilitiesUsedThisTurn.size();
   }

   public int getNumTimesAbilityUsedThisTurn(Ability ability) {
      return Tann.countInList(ability, this.abilitiesUsedThisTurn);
   }

   public int getAbilityCost(Ability ability) {
      if (ability instanceof Spell) {
         return this.getSpellCost((Spell)ability);
      } else if (ability instanceof Tactic) {
         return ((Tactic)ability).getNumCost();
      } else {
         TannLog.error("Cost for: " + ability);
         return 0;
      }
   }

   public int getSpellCost(Spell spell) {
      int cost = spell.getBaseCost();
      List<Global> globals = this.getGlobals();

      for (int i = 0; i < globals.size(); i++) {
         Global gt = globals.get(i);
         cost = gt.affectSpellCost(spell, cost, this);
      }

      return cost;
   }

   public List<SnapshotEvent> getEvents() {
      return this.events;
   }

   public EntState getEntWithHp(boolean player, boolean most, boolean max) {
      int bestHp = most ? 0 : 500;
      EntState result = null;

      for (EntState es : this.getStates(player, false)) {
         int hp = max ? es.getMaxHp() : es.getHp();
         if (most ? hp > bestHp : hp < bestHp) {
            bestHp = hp;
            result = es;
         }
      }

      return result;
   }

   public boolean isMostDamaged(EntState state) {
      float myDamage = state.getMissingHp();

      for (EntState es : this.getStates(state.isPlayer(), false)) {
         if (es.getMissingHp() > myDamage) {
            return false;
         }
      }

      return true;
   }

   public EntState getMostDamagedEnt(boolean playerSource, boolean playerTarget) {
      return this.getMostDamagedEnt(playerSource, playerTarget, 0);
   }

   public EntState getMostDamagedEnt(boolean playerSource, boolean playerTarget, int index) {
      final float shieldVal;
      if (playerSource) {
         shieldVal = 0.0F;
      } else {
         shieldVal = 0.001F;
      }

      List<EntState> states = this.getStates(playerTarget, false);
      Collections.sort(states, new Comparator<EntState>() {
         public int compare(EntState o1, EntState o2) {
            float o1val = o1.getMaxHp() - o1.getHp() - o1.getShields() * shieldVal;
            float o2val = o2.getMaxHp() - o2.getHp() - o2.getShields() * shieldVal;
            return (int)Math.signum(o2val - o1val);
         }
      });
      if (states.size() <= index) {
         return null;
      } else {
         EntState result = states.get(index);
         return !result.isDamaged() ? null : result;
      }
   }

   public int getRolls() {
      return this.rolls;
   }

   public void addRolls(int amount) {
      this.rolls += amount;

      for (Global gt : this.getGlobals()) {
         this.rolls = gt.affectFinalRerolls(this.rolls);
      }
   }

   public int getMaxRolls() {
      if (this.maxRolls != null) {
         return this.maxRolls;
      } else {
         this.maxRolls = 2;

         for (Global gt : this.getGlobals()) {
            this.maxRolls = gt.affectMaxRerolls(this.maxRolls, this.turn);
         }

         for (Global gt : this.getGlobals()) {
            this.maxRolls = gt.affectFinalRerolls(this.maxRolls);
         }

         return this.maxRolls;
      }
   }

   public void resetMaxRolls() {
      this.maxRolls = null;
   }

   public void resetRolls() {
      if (this.keepRerolls()) {
         this.rolls = this.rolls + this.getMaxRolls();
      } else {
         this.rolls = this.getMaxRolls();
      }
   }

   private boolean keepRerolls() {
      for (Global global : this.getGlobals()) {
         if (global.keepRerolls()) {
            return true;
         }
      }

      return false;
   }

   public void spendRoll() {
      this.rolls--;
   }

   public void unSpendRoll() {
      this.rolls++;
   }

   public void setOverrideRolls(int overrideRolls) {
      this.rolls = overrideRolls;
   }

   public int getTotalAbilitiesUsedThisTurn() {
      return this.abilitiesUsedThisTurn.size();
   }

   public int getNumAbilitiesUsedThisFight() {
      return this.abilitiesUsedThisFight.size();
   }

   public int getTotalSpellsUsedThisFight() {
      return this.spellOnly(this.abilitiesUsedThisFight);
   }

   public int getTotalSpellsUsedThisTurn() {
      return this.spellOnly(this.abilitiesUsedThisTurn);
   }

   private int spellOnly(List<Ability> abilitiesUsedThisTurn) {
      int cnt = 0;

      for (int i = 0; i < abilitiesUsedThisTurn.size(); i++) {
         if (abilitiesUsedThisTurn.get(i) instanceof Spell) {
            cnt++;
         }
      }

      return cnt;
   }

   public boolean warrantsSurrender() {
      if (this.reinforcements.size() > 0) {
         return false;
      } else if (this.refusedSurrender) {
         return false;
      } else if (this.isVictory()) {
         return false;
      } else {
         for (Global gt : this.getGlobals()) {
            if (gt.avoidFlee(this)) {
               return false;
            }
         }

         for (Global gtx : this.getGlobals()) {
            if (gtx.flee(this)) {
               return true;
            }
         }

         return false;
      }
   }

   private int getTotalHp(boolean player) {
      int total = 0;

      for (EntState es : this.getStates(player, false)) {
         total += es.getHp();
      }

      return total;
   }

   public void refusedSurrender() {
      this.refusedSurrender = true;
   }

   public float getHeroHpDividedByMonster() {
      int monsterHp = this.getTotalHp(false);
      int playerHp = this.getTotalHp(true);
      return (float)playerHp / monsterHp;
   }

   public int getNumDiceUsedThisTurn() {
      return this.diceUsedThisTurn.size();
   }

   public int getNumDiceUsedThisTurn(Ent source) {
      int total = 0;

      for (DieTargetable dieTargetable : this.diceUsedThisTurn) {
         if (dieTargetable.getSource() == source) {
            total++;
         }
      }

      return total;
   }

   public boolean anyMandatoryUnusedDice() {
      for (EntState es : this.getStates(true, false)) {
         if (es.canUse()
            && es.getCurrentSideState().getCalculatedEffect().hasKeyword(Keyword.mandatory)
            && DungeonScreen.get().targetingManager.isUsable(es.getEnt().getDie().getTargetable())) {
            return true;
         }
      }

      return false;
   }

   public boolean isPlayerTurn() {
      return this.playerTurn;
   }

   public void registerCommand(Command command) {
      this.appliedCommandsThisTurn.add(command);
      this.appliedCommandsTotal.add(command);
   }

   public TargetableCommand getPreviousCommandOfType(Class<? extends TargetableCommand> targetClazz) {
      return this.getPreviousCommandOfType(0, targetClazz);
   }

   public <T extends TargetableCommand> T getPreviousCommandOfType(int scanIndex, Class<? extends TargetableCommand> targetClazz) {
      int startIndex = this.appliedCommandsThisTurn.size() - 1;

      for (int i = startIndex; i >= 0; i--) {
         Command command = this.appliedCommandsThisTurn.get(i);
         if (targetClazz.isInstance(command)) {
            if (scanIndex == 0) {
               return (T)command;
            }

            scanIndex--;
         }
      }

      return null;
   }

   public List<Integer> getSideIndicesFromTurnsAgoAndEnt(int turnsAgo, Ent ent) {
      List<Integer> result = new ArrayList<>();
      int turnsElapsed = 0;
      List<Command> cs = this.appliedCommandsTotal;

      for (int i = cs.size() - 1; i >= 0; i--) {
         Command command = cs.get(i);
         if (command instanceof EndTurnCommand && ((EndTurnCommand)command).player) {
            turnsElapsed++;
         }

         if (turnsElapsed == turnsAgo && command instanceof DieCommand) {
            DieCommand dc = (DieCommand)command;
            if (dc.getSource() == ent) {
               result.add(dc.dt.getSideIndex());
            }
         }
      }

      return result;
   }

   public List<EntSideState> getSideStatesFromTurnsAgoAndEnt(int turnsAgo, Ent ent) {
      List<EntSideState> result = new ArrayList<>();
      int turnsElapsed = 0;
      List<Command> cs = this.appliedCommandsTotal;

      for (int i = cs.size() - 1; i >= 0; i--) {
         Command command = cs.get(i);
         if (command instanceof EndTurnCommand && ((EndTurnCommand)command).player) {
            turnsElapsed++;
         }

         if (turnsElapsed == turnsAgo && command instanceof DieCommand) {
            DieCommand dc = (DieCommand)command;
            if (dc.getSource() == ent) {
               Snapshot s = this.fightLog.getSnapshotBefore(dc);
               if (s != null) {
                  EntSideState ss = s.getSideState(dc);
                  if (ss != null) {
                     result.add(ss);
                  }
               }
            }
         }
      }

      return result;
   }

   public boolean hasAnyLockRestrictions() {
      return false;
   }

   public void allFlee() {
      this.reinforcements.clear();

      for (EntState es : this.getStates(false, false)) {
         es.flee();
      }

      this.checkHpLimits(null, null);
   }

   public EntSideState getMostRecentlyUsedDieCommandEffect() {
      for (int scanIndex = 0; scanIndex < 10; scanIndex++) {
         FightLog f = this.getFightLog();
         DieCommand dc = this.getPreviousCommandOfType(scanIndex, DieCommand.class);
         if (dc == null) {
            break;
         }

         Snapshot before = f.getSnapshotBefore(dc);
         if (before != null) {
            EntState es = before.getState(dc.getSource());
            if (es != null) {
               return es.getSideState(dc.dt.getSide());
            }
         }
      }

      return null;
   }

   public EntSideState getFirstEnemyAttackState() {
      for (EntState es : this.getStates(false, false)) {
         if (!es.isDead() && !es.isStunned()) {
            DieCommand possibleCommand = this.fightLog.getFirstAttackAfter(this, es.getEnt());
            if (possibleCommand != null) {
               Snapshot possibleSnapshot = es.getSnapshot().getFightLog().getSnapshotBefore(possibleCommand);
               if (possibleSnapshot != null) {
                  EntState possibleState = possibleSnapshot.getState(es.getEnt());
                  if (possibleState != null) {
                     return possibleState.getSideState(possibleCommand.dt.getSide());
                  }
               }
            }
         }
      }

      return null;
   }

   public List<Ent> getAllTargeters(Ent ent, boolean targeters) {
      List<Ent> results = this.tmp;
      results.clear();
      List<Command> allCommands = this.getFightLog().getAllCommands();
      int i = 0;

      for (int allCommandsSize = allCommands.size(); i < allCommandsSize; i++) {
         Command c = allCommands.get(i);
         List<Ent> basicTargets = c.getAllTargets();
         if (basicTargets != null) {
            List<Ent> eventualTargets = this.tmp2;
            eventualTargets.clear();
            int j = 0;

            for (int basicTargetsSize = basicTargets.size(); j < basicTargetsSize; j++) {
               Ent bt = basicTargets.get(j);
               EntState btState = this.getState(bt);
               if (btState != null) {
                  Ent eventualRedirect = btState.getEventualRedirect().getEnt();
                  if (!eventualTargets.contains(eventualRedirect)) {
                     eventualTargets.add(eventualRedirect);
                  }
               }
            }

            if (c.getSource() != null && !this.getState(c.getSource()).isDead()) {
               if (targeters) {
                  if (c.getSource().isPlayer() != ent.isPlayer() && eventualTargets.contains(ent) && !results.contains(c.getSource())) {
                     results.add(c.getSource());
                  }
               } else if (c.getSource() == ent) {
                  j = 0;

                  for (int basicTargetsSizex = basicTargets.size(); j < basicTargetsSizex; j++) {
                     Ent de = basicTargets.get(j);
                     EntState tmp = this.getState(de);
                     if (tmp != null) {
                        EntState es = tmp.getEventualRedirect();
                        Ent ev = es.getEnt();
                        if (!results.contains(ev)) {
                           results.add(ev);
                        }
                     }
                  }
               }
            }
         }
      }

      return results;
   }
}
