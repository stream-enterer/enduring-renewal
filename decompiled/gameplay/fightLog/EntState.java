package com.tann.dice.gameplay.fightLog;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.command.Command;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.EndTurnCommand;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.DiscardEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.roll.GlobalLockDiceLimit;
import com.tann.dice.gameplay.trigger.personal.Cleansed;
import com.tann.dice.gameplay.trigger.personal.MonsterSkip;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.TraitsRemoved;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveAllKeywords;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SetValue;
import com.tann.dice.gameplay.trigger.personal.hp.EmptyMaxHp;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.item.DiscardItem;
import com.tann.dice.gameplay.trigger.personal.merge.Inflicted;
import com.tann.dice.gameplay.trigger.personal.merge.PetrifySide;
import com.tann.dice.gameplay.trigger.personal.merge.Poison;
import com.tann.dice.gameplay.trigger.personal.merge.Regen;
import com.tann.dice.gameplay.trigger.personal.merge.Vulnerable;
import com.tann.dice.gameplay.trigger.personal.merge.Weaken;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.shaderFx.DeathType;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntState implements Cloneable {
   public static int cnt;
   private int hp;
   private Integer maxHp;
   private Integer emptyMaxHp;
   private int shield;
   private boolean dead;
   private boolean usedDie;
   private int timesUsedThisTurn;
   private Ent ent;
   private int damageTakenThisTurn;
   private int timesDamagedThisTurn;
   private int blockableDamageTaken;
   private int damageBlocked;
   private int poisonDamageTaken;
   private int poisonDamageTakenAfterDeath;
   private int regenReceived;
   private int healingReceived;
   private boolean waitingToDie;
   private List<Buff> buffs = new ArrayList<>();
   private List<EntSide> usedSides = new ArrayList<>();
   private Ent redirectTo;
   private Snapshot snapshot;
   private int minTriggerPipHp = 500000;
   private int deathsForStats = 0;
   private boolean fled;
   List<StateEvent> events = new ArrayList<>();
   private DeathType deathType = DeathType.Alpha;
   private Map<Cleansed.CleanseType, Integer> cleansedMap = new HashMap<>();
   private int turnsElapsed = 0;
   private boolean summonedSoNotAttacking = true;
   private boolean aliveThisTurn = true;
   List<Ent> redirectParts = new ArrayList<>();
   private List<Personal> cachedPrs;
   private Map<EntSide, EntSideState> entSidesMap = new HashMap<>();
   private List<Global> overrideGlobals;
   List<Personal> ignoredPersonals = new ArrayList<>();

   public void updateBaseStats() {
      boolean diedLastRound = false;
      if (this.ent instanceof Hero) {
         diedLastRound = ((Hero)this.ent).isDiedLastRound();
      }

      this.updateBaseStats(diedLastRound);
   }

   public void updateBaseStats(boolean diedLastRound) {
      this.maxHp = null;
      this.getMaxHp();
      this.somethingChanged();
      this.hp = this.getMaxHp();
      this.hp = this.hp - this.emptyMaxHp;
      if (diedLastRound) {
         this.hp = (int)Math.floor(this.hp / 2.0F);
      }

      List<Personal> personals = this.getActivePersonals();

      for (int i = 0; i < personals.size(); i++) {
         Personal p = personals.get(i);
         this.hp = p.affectStartingHp(this.hp);
      }

      this.hp = Math.max(1, this.hp);
      this.minTriggerPipHp = this.hp;
   }

   public EntState(Ent ent, Snapshot snapshot, List<Global> globalOverride) {
      this(ent, snapshot);
      this.overrideGlobals = globalOverride;
      this.updateBaseStats();
   }

   public EntState(Ent ent, Snapshot snapshot) {
      this.snapshot = snapshot;
      this.ent = ent;
      this.updateBaseStats();
      cnt++;
   }

   public void startTurn(int turn) {
      this.startOfTurnShieldStuff();
      if (!this.shouldSkipAdmin()) {
         this.aliveThisTurn = !this.isDead();

         for (Cleansed.CleanseType ct : Cleansed.CleanseType.values()) {
            this.cleansedMap.put(ct, 0);
         }

         this.summonedSoNotAttacking = false;
         this.resetStatsForTurn();
         this.recharge();
         if (!this.isDead()) {
            for (Personal t : this.getActivePersonals()) {
               t.startOfTurn(this, turn);
            }

            if (this.ent.isPlayer() && ChatStateEvent.MuchIncomingOverkill.chance()) {
               EntState future = this.getSnapshot().getFightLog().getState(FightLog.Temporality.Future, this.ent);
               if (future.isDead() && future.getHp() < -this.getMaxHp() * 2 / 3.0F) {
                  this.addEvent(ChatStateEvent.MuchIncomingOverkill);
               }
            }

            this.unskipBuffs();
         }
      }
   }

   private void resetStatsForTurn() {
      this.blockableDamageTaken = 0;
      this.regenReceived = 0;
      this.poisonDamageTaken = 0;
      this.poisonDamageTakenAfterDeath = 0;
      this.damageBlocked = 0;
      this.healingReceived = 0;
      this.damageTakenThisTurn = 0;
      this.timesDamagedThisTurn = 0;
   }

   public void endTurn() {
      if (!this.shouldSkipAdmin()) {
         this.turnsElapsed++;
         if (this.turnsElapsed > 10) {
            this.addEvent(ChatStateEvent.MuchTime, true);
         }

         int totalRegen = 0;

         for (Personal t : this.getActivePersonals()) {
            totalRegen += t.getRegen();
         }

         int totalPoison = this.getBasePoisonPerTurn();
         if (this.immuneToDamage(true)) {
            totalPoison = 0;
         }

         if (totalPoison == 0 && this.getBasePoisonPerTurn() > 0) {
            this.addEvent(TextEvent.ImmuneToPoison);
         }

         for (Personal t : this.getActivePersonals()) {
            totalRegen = t.affectHealing(totalRegen);
         }

         this.regenReceived = Math.max(0, Math.min(this.getMaxHp() - this.hp, totalRegen - totalPoison));
         if ((float)this.regenReceived / this.getMaxHp() > 0.7F && this.regenReceived > 3) {
            this.addEvent(ChatStateEvent.MuchRegen, true);
         }

         int healthDelta = totalRegen - totalPoison;
         if (this.dead) {
            this.regenReceived = 0;
         }

         if (healthDelta > 0) {
            this.heal(healthDelta, false);
         } else if (healthDelta < 0) {
            this.deathType = DeathType.Acid;
            int poizz = this.directDamage(-healthDelta, null, null, null, true);
            if (this.isDead()) {
               this.poisonDamageTakenAfterDeath = poizz;
            } else {
               this.poisonDamageTaken = poizz;
            }

            if (ChatStateEvent.MuchPoisonDamage.chance() && this.poisonDamageTaken > this.getMaxHp() / 2.0F && this.ent.isPlayer() && this.hp > 0) {
               this.addEvent(ChatStateEvent.MuchPoisonDamage);
            }
         }

         if (!this.isDead()) {
            for (Personal t : this.getActivePersonals()) {
               t.endOfTurn(this);
            }
         }

         this.redirectTo = null;
         this.tickBuffs();
      }
   }

   private boolean shouldSkipAdmin() {
      return !this.aliveThisTurn && this.isDead();
   }

   public void startOfTurnShieldStuff() {
      if (this.turnsElapsed != 0) {
         boolean keepShields = false;

         for (Personal t : this.getActivePersonals()) {
            keepShields |= t.keepShields();
         }

         if (!keepShields) {
            this.shield = 0;
         }
      }
   }

   public boolean checkHpLimits(EntState killer, Command command) {
      boolean recheck = false;
      int overHeal = Math.max(0, this.hp - this.getMaxHp());
      this.hp = Math.min(this.hp, this.getMaxHp());
      if (overHeal > 0) {
         this.overHealed(overHeal);
         recheck = true;
         if (!this.allowOverheal() && !(command instanceof EndTurnCommand)) {
            this.addEvent(TextEvent.ALREADY_MAX);
         }
      }

      if ((this.hp <= 0 || this.waitingToDie) && !this.dead) {
         this.die(killer);
         this.snapshot.onDeath(this, killer);
         recheck = true;
      }

      return recheck;
   }

   private void overHealed(int overHeal) {
      for (Personal t : this.getActivePersonals()) {
         t.overHeal(this, overHeal);
      }
   }

   public void hit(Eff eff, Ent source) {
      this.hit(eff, source, null);
   }

   public void hit(EffBill eff, Ent source, Targetable targetable) {
      this.hit(eff.bEff(), source, targetable);
   }

   public int getValueIncludingConditionalBonuses(Eff eff, EntState source) {
      int value = eff.getValue();
      List<Keyword> kw = eff.getKeywordForGameplay();
      if (eff.getDoubleConditions() != null) {
         for (ConditionalRequirement doubleCondition : eff.getDoubleConditions()) {
            if (!doubleCondition.preCalculate() && doubleCondition.isValid(this.snapshot, source, this, eff)) {
               value *= eff.hasKeyword(Keyword.treble) ? 3 : 2;
            }
         }
      }

      for (int i = 0; i < kw.size(); i++) {
         Keyword k = kw.get(i);
         ConditionalBonus cb = k.getConditionalBonus();
         if (cb != null && (source == null || !cb.requirement.preCalculate())) {
            value += cb.affectValue(eff, source, this, value);
         }
      }

      return value;
   }

   public void hit(Eff eff, Ent source, Targetable targetable) {
      if (eff.getType() == EffType.Or) {
         this.hit(eff.getOr(this.ent.isPlayer()), source, targetable);
      } else {
         int value = this.getValueIncludingConditionalBonuses(eff, this.snapshot.getState(source));
         if (value != eff.getValue()) {
            eff = eff.copy();
            eff.setValue(value);
         }

         if (eff.canKill()) {
            this.deathType = eff.getDeathType();
         }

         if (targetable instanceof Ability && this.isAbilityImmune()) {
            this.addEvent(TextEvent.ImmuneToSpells);
            this.getSnapshot().addEvent(SoundSnapshotEvent.clink);
         } else {
            boolean negative = eff.getType() == EffType.Damage
               || eff.hasKeyword(Keyword.damage)
               || source != null && source.isPlayer() != this.ent.isPlayer() && !eff.isFriendly();
            if (negative) {
               if (this.isDodging()) {
                  this.addEvent(TextEvent.Dodge);
                  this.addEvent(ChatStateEvent.DodgedAttack, true);
                  return;
               }

               EntState redir = this.getEventualRedirect();
               if (redir != this) {
                  this.addRedirEvents(redir);
                  redir.hit(eff, source, targetable);
                  return;
               }
            }

            if (!eff.isUnusableBecauseNerfed()) {
               int valuex = eff.getValue();
               if (eff.hasValue() && eff.getValue() != valuex) {
                  eff = eff.copy();
                  eff.setValue(valuex);
               }

               if (eff.hasKeyword(Keyword.dispel)) {
                  this.removeTraits();
               }

               switch (eff.getType()) {
                  case Damage:
                     this.damage(valuex, source, eff, targetable);
                     break;
                  case Shield:
                     this.block(valuex);
                     break;
                  case Heal:
                     this.heal(valuex);
                     break;
                  case HealAndShield:
                     this.block(valuex);
                     this.heal(valuex);
                     break;
                  case Buff:
                     Buff buff = eff.getBuffAndCopy();
                     if (targetable instanceof DieTargetable && this.shouldSkipBuffTick(eff, source, buff)) {
                        buff.skipFirstTick();
                     }

                     this.addBuff(buff);
                     break;
                  case Kill:
                     this.kill(this.deathType);
                     break;
                  case Recharge:
                     this.recharge();
                     break;
                  case RedirectIncoming:
                     if (source != this.ent) {
                        this.redirectTo = source;
                     }

                     if (this.redirectTo != null) {
                        EntState es = this.snapshot.getState(this.redirectTo);
                        if (es != null) {
                           es.afterBlockGained();
                        }
                     }
                     break;
                  case Mana:
                     this.snapshot.untargetedUse(eff, source);
                     break;
                  case Event:
                     StateEvent se = eff.getEvent();
                     if (se.chance()) {
                        this.addEvent(se);
                     }
                     break;
                  case SetToHp:
                     for (int i = 0; i < 10; i++) {
                        if (this.hp < eff.getValue() && this.getMaxHp() < eff.getValue()) {
                           this.addBuff(new MaxHP(eff.getValue() - this.getMaxHp()));
                        }
                     }

                     this.hp = eff.getValue();
                     break;
                  case MultiplyShields:
                     this.block(this.shield * (eff.getValue() - 1));
                     break;
                  case Reroll:
                  case Summon:
                     throw new RuntimeException(eff.getType() + " should not target an ent");
               }

               this.activateOnUseKeywords(eff, eff.getKeywordForGameplay(), source, targetable);
               this.updateMinTriggerPipHp(this.getHp());
            }
         }
      }
   }

   private void addRedirEvents(EntState redir) {
      this.addEvent(TextEvent.Redirected);
      this.addEvent(PanelHighlightEvent.redirect);
      this.addEvent(ChatStateEvent.Redirected, true);
      redir.addEvent(PanelHighlightEvent.redirect);
   }

   public void takePain(int pain) {
      if (this.isDodging()) {
         this.addEvent(TextEvent.Dodge);
         this.addEvent(ChatStateEvent.DodgedAttack, true);
      } else {
         EntState redir = this.getEventualRedirect();
         if (redir != this) {
            this.addRedirEvents(redir);
            redir.damage(pain, null, null, null);
         } else {
            this.damage(pain, null, null, null);
         }
      }
   }

   private boolean isDodging() {
      for (Personal t : this.getActivePersonals()) {
         if (t.dodgeAttack()) {
            return true;
         }
      }

      return false;
   }

   private void activateOnUseKeywords(Eff eff, List<Keyword> keywords, Ent source, Targetable targetable) {
      int kVal = KUtils.getValue(eff);

      for (int keywordIndex = 0; keywordIndex < keywords.size(); keywordIndex++) {
         Keyword k = keywords.get(keywordIndex);
         if (k.getInflict() != null) {
            this.hit(new EffBill().buff(new Buff(1, new Inflicted(k.getInflict()))), source, targetable);
         }

         switch (k) {
            case heal:
               this.heal(kVal);
               break;
            case shield:
               this.block(kVal);
               break;
            case damage:
               this.damage(kVal, source, eff, targetable);
               break;
            case cleanse:
               this.cleanse(kVal);
               break;
            case poison:
               this.poison(kVal);
               break;
            case petrify:
               this.petrify(kVal);
               break;
            case regen:
               this.regen(kVal);
               break;
            case vitality:
               this.addBuff(new EmptyMaxHp(kVal));
               break;
            case wither:
               this.addBuff(new MaxHP(-kVal));
               break;
            case hypnotise:
               this.hit(new EffBill().buff(new Buff(1, new AffectSides(new TypeCondition(EffType.Damage), new SetValue(0)).show(true))), source, targetable);
               break;
            case fierce:
               if (this.hp <= KUtils.getValue(eff)) {
                  this.flee();
               }
               break;
            case repel:
               Eff e = new EffBill().damage(kVal).bEff();

               for (Ent de : new ArrayList<>(this.snapshot.getAllTargeters(this.ent, true))) {
                  EntState es = this.snapshot.getState(de);
                  es.hit(e, source, targetable);
               }
               break;
            case vulnerable:
               this.addBuff(new Buff(1, new Vulnerable(kVal)));
               break;
            case weaken:
               this.hit(new EffBill().buff(new Buff(1, new Weaken(kVal))), source, targetable);
               break;
            case boost:
               this.hit(new EffBill().friendly().buff(new Buff(1, new AffectSides(new FlatBonus(true, kVal)))), source, targetable);
               break;
            case smith:
               this.hit(
                  new EffBill()
                     .friendly()
                     .buff(new Buff(1, new AffectSides(new TypeCondition(Arrays.asList(EffType.Shield, EffType.Damage), false), new FlatBonus(true, kVal)))),
                  source,
                  targetable
               );
               break;
            case share:
               List<Keyword> ks = new ArrayList<>(eff.getKeywords());
               ks.remove(Keyword.share);
               this.hit(new EffBill().friendly().buff(new Buff(1, new AffectSides(new AddKeyword(ks)))), source, targetable);
               break;
            case annul:
               this.hit(new EffBill().friendly().buff(new Buff(1, new AffectSides(new RemoveAllKeywords()))), source, targetable);
               break;
            case permaBoost:
               this.hit(new EffBill().friendly().buff(new Buff(new AffectSides(new FlatBonus(true, kVal)))), source, targetable);
         }
      }
   }

   private boolean isAbilityImmune() {
      for (Personal pt : this.getActivePersonals()) {
         if (pt.immuneToAbilities()) {
            return true;
         }
      }

      return false;
   }

   public void petrify(int amt) {
      if (amt > 0) {
         List<Integer> existing = new ArrayList<>();

         for (Personal pt : this.getActivePersonals()) {
            if (pt instanceof PetrifySide) {
               existing = ((PetrifySide)pt).getPetrified();
            }
         }

         List<Integer> sidesToPetrify = new ArrayList<>();

         for (int i = 0; sidesToPetrify.size() < amt; i++) {
            if (i >= 6) {
               sidesToPetrify.add(null);
            } else {
               int sideIndex = SpecificSidesType.PetrifyOrder.sideIndices[i];
               if (!existing.contains(sideIndex)) {
                  sidesToPetrify.add(sideIndex);
               }
            }
         }

         if (sidesToPetrify.size() > 0) {
            this.addBuff(new Buff(new PetrifySide(sidesToPetrify)));
         }
      }
   }

   public boolean immuneToHealing() {
      for (Personal t : this.getActivePersonals()) {
         if (t.immuneToHealing()) {
            return true;
         }
      }

      return false;
   }

   public boolean immuneToDamage(boolean poison) {
      for (Personal t : this.getActivePersonals()) {
         if (t.immuneToDamage(poison)) {
            return true;
         }
      }

      return false;
   }

   public boolean immuneToShields() {
      for (Personal t : this.getActivePersonals()) {
         if (t.immuneToShields()) {
            return true;
         }
      }

      return false;
   }

   private boolean shouldSkipBuffTick(Eff eff, Ent source, Buff buff) {
      return !buff.isInfinite() && source != null && !source.isPlayer() && this.ent.isPlayer();
   }

   private boolean checkRedirectLoop() {
      Ent check = this.ent;
      this.redirectParts.clear();

      while (check != null) {
         if (this.redirectParts.contains(check)) {
            return true;
         }

         this.redirectParts.add(check);
         check = this.snapshot.getState(check).redirectTo;
      }

      return false;
   }

   public void poison(int value) {
      for (Personal t : this.getActivePersonals()) {
         if (t.poisonSpecificImmunity()) {
            this.addEvent(TextEvent.ImmuneToPoison);
            return;
         }
      }

      this.addBuff(new Buff(new Poison(value)));
   }

   public void regen(int value) {
      if (!this.immuneToHealing()) {
         this.addBuff(new Buff(new Regen(value)));
      }
   }

   public void resurrect() {
      if (this.canResurrect()) {
         this.waitingToDie = false;
         this.aliveThisTurn = true;
         this.clearBuffsResurrect();
         this.resetStatsForTurn();
         this.somethingChanged();
         this.dead = false;
         this.updateBaseStats(false);
         if (this.isPlayer()) {
            this.useDie();
         } else {
            this.addBuff(new Buff(1, new MonsterSkip()));
         }

         if (this.deathsForStats > 2) {
            this.addEvent(ChatStateEvent.HeroResurrectedMulti, true);
         } else {
            this.addEvent(ChatStateEvent.HeroResurrected, true);
         }
      }
   }

   public boolean canResurrect() {
      for (Personal pt : this.getActivePersonals()) {
         if (pt.stopResurrect()) {
            return false;
         }
      }

      return true;
   }

   private void clearBuffsResurrect() {
      for (int i = this.buffs.size() - 1; i >= 0; i--) {
         Buff b = this.buffs.get(i);
         if (!b.personal.persistThroughDeathBuff()) {
            this.buffs.remove(i);
         }
      }
   }

   private void removeTraits() {
      if (this.ent.traits.length > 0) {
         this.addBuff(new TraitsRemoved());
      }
   }

   private void cleanse(int value) {
      this.addBuff(new Buff(1, new Cleansed(value)));

      for (int i = this.buffs.size() - 1; i >= 0; i--) {
         Buff b = this.buffs.get(i);
         boolean fullyCleansed = this.attemptCleanse(b);
         if (fullyCleansed) {
            this.buffs.remove(i);
         }
      }

      this.somethingChanged();
   }

   public void addBuff(Personal personal) {
      this.addBuff(new Buff(personal));
   }

   public void addBuff(Buff buff) {
      if (buff.getCleanseType() != null) {
         boolean cleansed = this.attemptCleanse(buff);
         if (cleansed) {
            this.addEvent(TextEvent.ImmuneToDebuff);
            return;
         }
      }

      if (buff.personal instanceof PetrifySide) {
         this.addEvent(TextEvent.Petrify);
      }

      boolean skip = false;

      for (int i = 0; i < this.buffs.size(); i++) {
         Buff b = this.buffs.get(i);
         if (b.canMerge(buff)) {
            b = b.copy();
            b.merge(buff);
            this.buffs.set(i, b);
            skip = true;
            break;
         }
      }

      if (!skip) {
         this.buffs.add(buff);
      }

      this.somethingChanged();
   }

   public int numBuffs() {
      return this.buffs.size();
   }

   private boolean attemptCleanse(Buff buff) {
      int totalCleanseAmt = this.getTotalCleanseAmt();
      if (totalCleanseAmt == 0) {
         return false;
      } else {
         Cleansed.CleanseType ct = buff.getCleanseType();
         if (ct == null) {
            return false;
         } else {
            Integer alreadyCleansed = this.cleansedMap.get(ct);
            if (alreadyCleansed == null) {
               alreadyCleansed = 0;
            }

            int cleanseRemaining = totalCleanseAmt - alreadyCleansed;
            if (cleanseRemaining <= 0) {
               return false;
            } else {
               TP<Integer, Boolean> cleanseResult = buff.cleanseBy(cleanseRemaining);
               this.cleansedMap.put(ct, alreadyCleansed + cleanseResult.a);
               return cleanseResult.b;
            }
         }
      }
   }

   private int getTotalCleanseAmt() {
      int total = 0;

      for (Personal pt : this.getActivePersonals()) {
         total += pt.getCleanseAmt();
      }

      return total;
   }

   private void block(int value) {
      if (this.immuneToShields()) {
         this.addEvent(TextEvent.Immune);
      } else {
         for (Personal t : this.getActivePersonals()) {
            value = t.affectShields(value);
         }

         this.shield += value;
         this.afterBlockGained();
      }
   }

   private void afterBlockGained() {
      for (Personal t : this.getActivePersonals()) {
         this.shield = t.affectFinalShields(this.shield);
      }

      List<Ent> targetrs = this.snapshot.getAllTargeters(this.ent, true);

      for (int i = 0; i < targetrs.size(); i++) {
         EntState es = this.snapshot.getState(targetrs.get(i));
         if (es != null) {
            es.targetGainedShield(this);
         }
      }
   }

   private void targetGainedShield(EntState src) {
      List<Personal> personals = this.getActivePersonals();

      for (int i = 0; i < personals.size(); i++) {
         personals.get(i).targetGainsShield(this, src);
      }
   }

   public int damage(int incoming, Ent source, Eff eff, Targetable targetable) {
      if (this.immuneToDamage(false)) {
         this.addEvent(TextEvent.ImmuneToDamage);
         return 0;
      } else {
         int absorbed = Math.min(this.shield, incoming);
         this.damageBlocked += absorbed;
         this.shield -= absorbed;
         int damageDealt = this.directDamage(incoming - absorbed, source, eff, targetable, false);
         this.blockableDamageTaken += damageDealt;
         this.checkBuffsForRemoval();
         return damageDealt;
      }
   }

   private boolean preventDeath() {
      boolean allowDeath = true;

      for (Personal t : this.getActivePersonals()) {
         allowDeath &= t.allowDeath(this);
      }

      return !allowDeath;
   }

   private void unskipBuffs() {
      for (int i = 0; i < this.buffs.size(); i++) {
         this.buffs.get(i).unskip();
      }
   }

   private void tickBuffs() {
      for (int i = 0; i < this.buffs.size(); i++) {
         this.buffs.get(i).turn();
      }

      this.checkBuffsForRemoval();
   }

   private void checkBuffsForRemoval() {
      for (int i = this.buffs.size() - 1; i >= 0; i--) {
         Buff b = this.buffs.get(i);
         if (b.expired()) {
            this.buffs.remove(b);
            this.somethingChanged();
         }
      }
   }

   public int directDamage(int damage, Ent source, Eff sourceEff, Targetable targetable, boolean poison) {
      if (damage <= 0) {
         return 0;
      } else if (this.immuneToDamage(poison)) {
         this.addEvent(TextEvent.ImmuneToDamage);
         return 0;
      } else {
         for (Personal t : this.getActivePersonals()) {
            damage = t.alterTakenDamage(damage, sourceEff, this.snapshot, this, targetable);
         }

         if (damage <= 0) {
            this.addEvent(TextEvent.ImmuneToDamage);
            return 0;
         } else {
            if (damage >= this.hp && this.preventDeath()) {
               damage = this.hp - 1;
               this.addEvent(TextEvent.ImmuneToDamage);
            }

            this.hp -= damage;
            this.damageTakenThisTurn += damage;
            this.timesDamagedThisTurn++;
            if (!this.isDead() && damage > 0) {
               EntState sourceState = this.snapshot.getState(source);
               this.damageTaken(sourceState, sourceEff, damage, targetable);
            }

            return damage;
         }
      }
   }

   private void updateMinTriggerPipHp(int oldNewHp) {
      this.minTriggerPipHp = Math.min(oldNewHp, this.minTriggerPipHp);
   }

   public int getMinTriggerPipHp() {
      return this.minTriggerPipHp;
   }

   private void damageTaken(EntState source, Eff sourceEff, int damage, Targetable targetable) {
      int minTriggerHp = this.getMinTriggerPipHp();

      for (Personal t : this.getActivePersonals()) {
         t.damageTaken(source, this, this.snapshot, damage, this.damageTakenThisTurn, sourceEff, targetable, minTriggerHp);
      }

      this.updateMinTriggerPipHp(this.getHp());
   }

   public void flee() {
      if (!this.isDead() && this.hp > 0) {
         this.kill(DeathType.Flee);
      }
   }

   public void kill(DeathType deathType) {
      this.fled = deathType == DeathType.Flee;
      if (deathType != DeathType.Flee && this.preventDeath()) {
         this.addEvent(TextEvent.Undying);
      } else {
         this.deathType = deathType;
         this.waitingToDie = true;
      }
   }

   private void die(EntState killer) {
      if (!this.dead) {
         if (this.isPlayer() && killer == this && ChatStateEvent.AdjacentSuicide.chance()) {
            List<EntState> adjacents = this.snapshot.getAdjacents(this, 1);
            if (adjacents.size() > 0) {
               Tann.random(adjacents).addEvent(ChatStateEvent.AdjacentSuicide);
            }
         }

         if (killer != null
            && killer.ent.isPlayer()
            && this.getHp() <= -this.getMaxHp() / 3 - 3
            && !this.ent.isPlayer()
            && ChatStateEvent.AdjacentOverkillMonsterMuchly.chance()) {
            List<EntState> adjacents = this.snapshot.getAdjacents(killer, 1);
            if (adjacents.size() > 0) {
               Tann.random(adjacents).addEvent(ChatStateEvent.AdjacentOverkillMonsterMuchly);
            }
         }

         if (this.ent.isPlayer() && killer != null && !killer.getEnt().isPlayer()) {
            killer.addEvent(ChatStateEvent.Noob, true);
         }

         this.waitingToDie = false;
         if (!this.isFled()) {
            this.deathsForStats++;

            for (Personal t : this.getActivePersonals()) {
               t.onDeath(this, this.snapshot);
            }
         }

         this.dead = true;
      }
   }

   private void heal(int heal) {
      this.heal(heal, true);
   }

   private void heal(int heal, boolean personalsAffectAmount) {
      if (!this.dead) {
         if (this.immuneToHealing()) {
            this.addEvent(TextEvent.Immune);
         } else {
            if (personalsAffectAmount) {
               for (Personal t : this.getActivePersonals()) {
                  heal = t.affectHealing(heal);
               }
            }

            int maxToHeal = this.getMaxHp() - this.hp;
            heal = Math.max(0, heal);
            this.hp += heal;
            this.healingReceived = this.healingReceived + Math.max(0, Math.min(heal, maxToHeal));
         }
      }
   }

   public List<Personal> getActivePersonals() {
      if (this.cachedPrs == null) {
         this.cachedPrs = this.calculateActivePrs();
      }

      return this.cachedPrs;
   }

   private List<Personal> calculateActivePrs() {
      List<Personal> calc = new ArrayList<>();
      if (this.allowTraits()) {
         for (int i = 0; i < this.ent.traits.length; i++) {
            calc.add(this.ent.traits[i].personal);
         }
      }

      int indexToAdd = calc.size();

      for (int i = 0; i < this.getGlobals().size(); i++) {
         Personal extra = this.getGlobals().get(i).getLinkedPersonal(this);
         if (extra != null) {
            calc.add(calc.size() - indexToAdd, extra);
         }
      }

      List<Item> eq = this.ent.getItems();

      for (int ix = 0; ix < eq.size(); ix++) {
         Item e = eq.get(ix);
         if (!this.ignoreItem(e)) {
            calc.addAll(e.getPersonals());
         }
      }

      for (int ixx = 0; ixx < this.buffs.size(); ixx++) {
         calc.add(this.buffs.get(ixx).personal);
      }

      for (int activePersonalIndex = calc.size() - 1; activePersonalIndex >= 0; activePersonalIndex--) {
         Personal t = calc.get(activePersonalIndex);
         List<Personal> links = this.getAllLinkLinkLink(t, this.snapshot);
         if (links != null) {
            calc.addAll(activePersonalIndex + 1, links);
         }
      }

      calc.removeAll(this.ignoredPersonals);
      this.removeDuplicateSingulars(calc);
      Collections.sort(calc, Trigger.sorter);
      return calc;
   }

   private void removeDuplicateSingulars(List<Personal> calc) {
      for (int i = calc.size() - 1; i >= 1; i--) {
         Personal p1 = calc.get(i);
         if (p1.singular()) {
            boolean dupe = false;

            for (int i1 = 0; i1 < i; i1++) {
               Personal p2 = calc.get(i1);
               if (p2.singular() && p2.getClass() == p1.getClass()) {
                  dupe = true;
                  break;
               }
            }

            if (dupe) {
               calc.remove(i);
            }
         }
      }
   }

   private boolean allowTraits() {
      int i = 0;

      for (int buffsSize = this.buffs.size(); i < buffsSize; i++) {
         Buff b = this.buffs.get(i);
         if (!b.personal.allowTraits()) {
            return false;
         }
      }

      return true;
   }

   public boolean ignoreItem(Item e) {
      for (int i = 0; i < this.buffs.size(); i++) {
         if (this.buffs.get(i).personal.ignoreItem(e)) {
            return true;
         }
      }

      return false;
   }

   private List<Personal> getAllLinkLinkLink(Personal t, Snapshot snapshotMaybeNull) {
      List<Personal> linkedPersonals;
      if (snapshotMaybeNull != null) {
         linkedPersonals = t.getLinkedPersonals(snapshotMaybeNull, this);
      } else {
         linkedPersonals = t.getLinkedPersonalsNoSnapshot(this);
      }

      if (linkedPersonals == null) {
         return null;
      } else {
         List<Personal> var10 = new ArrayList<>(linkedPersonals);
         List<Personal> linksToCheck = new ArrayList<>(var10);
         int recursions = 0;
         int MAX_RECURSION = 200;
         int MAX_ELEMENTS = 30;

         while (linksToCheck.size() > 0) {
            Personal ch = linksToCheck.remove(0);
            List<Personal> linkLink;
            if (snapshotMaybeNull != null) {
               linkLink = ch.getLinkedPersonals(snapshotMaybeNull, this);
            } else {
               linkLink = ch.getLinkedPersonalsNoSnapshot(this);
            }

            if (linkLink != null && linkLink.size() > 0) {
               if (++recursions > 200) {
                  throw new RuntimeException("Recursive personal error");
               }

               if (linksToCheck.size() > 30) {
                  throw new RuntimeException("Personal recursion size limit reached");
               }

               var10.addAll(linkLink);
               linksToCheck.addAll(linkLink);
            }
         }

         return var10;
      }
   }

   public void startOfFight() {
      this.somethingChanged();

      for (Personal t : this.getActivePersonals()) {
         t.startOfCombat(this.snapshot, this);
      }
   }

   public boolean hasCleansableBuffs() {
      for (int i = 0; i < this.buffs.size(); i++) {
         if (this.buffs.get(i).getCleanseType() != null) {
            return true;
         }
      }

      return false;
   }

   public void afterUse(EntSide actualSide) {
      for (Personal t : this.getActivePersonals()) {
         t.afterUse(this, actualSide);
      }
   }

   public boolean isDead() {
      return this.isDead(true);
   }

   public boolean isDead(boolean includeFled) {
      return this.isFled() && !includeFled ? false : this.dead;
   }

   public int getMaxHp() {
      if (this.maxHp == null) {
         int limitReduction = 0;
         this.maxHp = this.ent.entType.hp;
         this.emptyMaxHp = 0;

         for (int i = 0; i < this.getActivePersonals().size(); i++) {
            Personal pt = this.getActivePersonals().get(i);
            this.emptyMaxHp = this.emptyMaxHp + pt.bonusEmptyMaxHp(this.maxHp, this.emptyMaxHp);
            this.maxHp = this.maxHp + pt.getBonusMaxHp(this.maxHp, this);
            int limitedMax = pt.limitHp(this.maxHp);
            limitReduction += this.maxHp - limitedMax;
            this.maxHp = limitedMax;
         }

         this.emptyMaxHp = Math.max(0, this.emptyMaxHp - limitReduction);
         this.cachedPrs = null;
      }

      this.maxHp = Math.max(1, this.maxHp);
      return this.maxHp;
   }

   public int getHp() {
      return this.hp;
   }

   public Ent getEnt() {
      return this.ent;
   }

   public EntState copy() {
      try {
         EntState result = (EntState)this.clone();
         result.setupClonedLists();
         return result;
      } catch (CloneNotSupportedException var2) {
         System.out.println("fuckoff javaaa");
         return null;
      }
   }

   private void setupClonedLists() {
      List<Buff> newBuffs = new ArrayList<>();
      int i = 0;

      for (int buffsSize = this.buffs.size(); i < buffsSize; i++) {
         Buff b = this.buffs.get(i);
         newBuffs.add(b.copy());
      }

      this.buffs = newBuffs;
      this.usedSides = new ArrayList<>(this.usedSides);
      this.entSidesMap = new HashMap<>();
      this.events = new ArrayList<>(this.events);
      this.ignoredPersonals = new ArrayList<>(this.ignoredPersonals);
      this.cleansedMap = new HashMap<>(this.cleansedMap);
      this.cachedPrs = null;
   }

   public boolean isUsed() {
      return this.usedDie;
   }

   public void useDie() {
      this.useDie(1);
   }

   public void useDie(int max) {
      this.timesUsedThisTurn++;
      this.usedDie = this.timesUsedThisTurn >= max;
   }

   public void recharge() {
      this.usedDie = false;
      this.timesUsedThisTurn = 0;
   }

   public int getBlockableDamageTaken() {
      return this.blockableDamageTaken;
   }

   public int getDamageTakenThisTurn() {
      return this.damageTakenThisTurn;
   }

   public int getTimesDamagedThisTurn() {
      return this.timesDamagedThisTurn;
   }

   public int getShields() {
      return this.shield;
   }

   public int getDamageBlocked() {
      return this.damageBlocked;
   }

   public boolean isForwards() {
      List<Personal> actives = this.getActivePersonals();

      for (int i = 0; i < actives.size(); i++) {
         Personal t = actives.get(i);
         if (t.backRow()) {
            return false;
         }
      }

      return true;
   }

   public boolean canBeTargetedAsForwards() {
      if (this.isForwards()) {
         return true;
      } else {
         for (EntState es : this.snapshot.getAliveEntStates(this.ent.isPlayer())) {
            if (es.isForwards()) {
               return false;
            }
         }

         return true;
      }
   }

   public int getBasePoisonPerTurn() {
      int result = 0;

      for (Personal t : this.getActivePersonals()) {
         result += t.getPoisonDamage();
      }

      return result;
   }

   public void somethingChanged() {
      this.cachedPrs = null;
      int prevMaxHp = this.maxHp;
      int prevEmptyMaxHp = this.emptyMaxHp;
      this.maxHp = null;
      int newMaxHp = this.getMaxHp();
      boolean wasAlive = this.hp > 0;
      this.hp = this.hp + Math.max(0, newMaxHp - prevMaxHp - (this.emptyMaxHp - prevEmptyMaxHp));
      if (wasAlive && this.hp < 0) {
         this.hp = 1;
      }

      this.clearSideMap();
   }

   public void useSide(EntSide side) {
      this.usedSides.add(side);
   }

   public void setSnapshot(Snapshot snapshot) {
      this.snapshot = snapshot;
   }

   public void endLevel() {
      if (this.ent instanceof Hero) {
         boolean reportDeath = this.getDeathsForStats() > 0;
         if (reportDeath) {
            boolean avoidDeathPenalty = false;

            for (Personal t : this.getActivePersonals()) {
               avoidDeathPenalty |= t.avoidDeathPenalty();
            }

            if (avoidDeathPenalty) {
               reportDeath = false;
            }
         }

         ((Hero)this.ent).setDiedLastRound(reportDeath);
      }

      for (Personal pt : this.getActivePersonals()) {
         pt.endOfLevel(this, this.snapshot);
      }
   }

   public int getPoisonDamageTaken(boolean includeAfterDeath) {
      return this.poisonDamageTaken + (includeAfterDeath ? this.poisonDamageTakenAfterDeath : 0);
   }

   public Snapshot getSnapshot() {
      return this.snapshot;
   }

   public boolean isStunned() {
      List<Personal> actives = this.getActivePersonals();

      for (int i = 0; i < actives.size(); i++) {
         Personal t = actives.get(i);
         if (t.preventAction()) {
            return true;
         }
      }

      return false;
   }

   public boolean skipTurn() {
      return this.isDead() || this.isStunned();
   }

   public int getTotalRegenThisTurn() {
      return this.regenReceived;
   }

   public int getHealingThisTurn() {
      return this.healingReceived;
   }

   public boolean canUse() {
      return !this.isUsed() && !this.isStunned();
   }

   public boolean onKill(Command source, Ent killed) {
      boolean effected = false;
      if (source instanceof DieCommand && ChatStateEvent.OtherKillArrow.chance()) {
         Eff effs = ((DieCommand)source).targetable.getDerivedEffects(this.getSnapshot());
         if (effs.hasKeyword(Keyword.ranged)) {
            List<EntState> others = this.snapshot.getAdjacents(this, 1);
            if (others.size() > 0) {
               Tann.random(others).addEvent(ChatStateEvent.OtherKillArrow);
            }
         }
      }

      for (Personal p : this.getActivePersonals()) {
         p.onKill(this, killed);
      }

      return effected;
   }

   public boolean onRescue(Hero saved, Command command) {
      EntState savedState = this.snapshot.getState(saved);
      boolean effected = false;

      for (Personal t : this.getActivePersonals()) {
         effected |= t.onRescue(this);
      }

      if (!this.snapshot.isVictory() && command instanceof DieCommand) {
         DieCommand dc = (DieCommand)command;
         boolean friendly = dc.targetable.getDerivedEffects().isFriendly();
         boolean selfSave = saved == this.ent;
         if (friendly) {
            if (selfSave) {
               savedState.addEvent(ChatStateEvent.SelfSave, true);
            } else {
               savedState.addEvent(ChatStateEvent.SaveThank, true);
            }
         } else if (!selfSave) {
            savedState.addEvent(ChatStateEvent.SaveThankKill, true);
         }
      }

      return effected;
   }

   public EntSideState getSideState(int sideIndex) {
      if (sideIndex >= 0 && sideIndex <= 5) {
         return this.getSideState(this.getEnt().getSides()[sideIndex]);
      } else {
         throw new RuntimeException("out of bounds side index " + sideIndex);
      }
   }

   public EntSideState getSideState(EntSide side) {
      if (this.entSidesMap.get(side) == null) {
         EntSideState ess = new EntSideState(this, side);
         this.entSidesMap.put(side, ess);
      }

      return this.entSidesMap.get(side);
   }

   public void clearSideMap() {
      this.entSidesMap.clear();
   }

   public int getSideIndex(EntSide original) {
      return Tann.indexOf(this.ent.getSides(), original);
   }

   public boolean isDamaged() {
      return this.getHp() < this.getMaxHp();
   }

   public List<StateEvent> getStateEvents() {
      return this.events;
   }

   public void addEvent(StateEvent stateEvent, boolean chance) {
      if (!chance || stateEvent.chance()) {
         DungeonScreen ds = DungeonScreen.get();
         if (ds == null || !ds.isLoading()) {
            if (!this.isDead()) {
               this.events.add(stateEvent);
            }
         }
      }
   }

   public void addEvent(StateEvent stateEvent) {
      this.addEvent(stateEvent, false);
   }

   public void onTotalKills(int kills) {
      if (kills >= 3 && ChatStateEvent.TripleOrMoreKill.chance()) {
         this.addEvent(ChatStateEvent.TripleOrMoreKill);
      }
   }

   private List<Global> getGlobals() {
      if (this.overrideGlobals != null) {
         return this.overrideGlobals;
      } else {
         return (List<Global>)(this.snapshot == null ? new ArrayList<>() : this.snapshot.getGlobals());
      }
   }

   public boolean hasOverrideGlobals() {
      return this.overrideGlobals != null;
   }

   public void afterAbility(Ability spell) {
      for (Personal t : this.getActivePersonals()) {
         t.afterUseAbility(this.getSnapshot(), spell, this);
      }
   }

   public int getPoisonDamageTaken() {
      return this.poisonDamageTaken;
   }

   public boolean hasPersonal(Class<? extends Personal> personalClass) {
      for (Personal pt : this.getActivePersonals()) {
         if (personalClass.isInstance(pt)) {
            return true;
         }
      }

      return false;
   }

   public int getDeathsForStats() {
      return this.deathsForStats;
   }

   public EntState getEventualRedirect() {
      if (!this.checkRedirectLoop() && this.redirectTo != null) {
         EntState es = this.snapshot.getState(this.redirectTo);
         return es.getEventualRedirect();
      } else {
         return this;
      }
   }

   public void ignorePersonal(Personal personal) {
      this.ignoredPersonals.add(personal);
      this.somethingChanged();
   }

   public EntState getDeltaPosAllowDeath(int delta) {
      if (this.getSnapshot() == null) {
         return this;
      } else {
         List<EntState> states = this.getSnapshot().getStates(this.ent.isPlayer(), null);
         return !states.contains(this) ? this : states.get((states.indexOf(this) + delta + states.size()) % states.size());
      }
   }

   public void dieStoppedOn(EntSideState currentSide) {
      for (Personal pt : this.getActivePersonals()) {
         pt.dieStoppedOn(currentSide, this);
      }
   }

   public void dieLocked(EntSideState currentSide) {
      for (Personal pt : this.getActivePersonals()) {
         pt.dieLocked(currentSide, this);
      }
   }

   public boolean isPlayer() {
      return this.getEnt().isPlayer();
   }

   public boolean allowOverheal() {
      for (Personal t : this.getActivePersonals()) {
         if (t.allowOverheal()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasIncomingDamage() {
      return this.getIncomingDamage() > 0;
   }

   public int getIncomingDamage() {
      EntState futureState = this.getFutureState();
      return futureState == null ? 0 : futureState.getBlockableDamageTaken() - this.getBlockableDamageTaken();
   }

   public boolean hasIncomingBuffs() {
      EntState futureState = this.getFutureState();
      return futureState == null ? false : futureState.getActivePersonals().size() > this.getActivePersonals().size();
   }

   private EntState getFutureState() {
      if (this.getSnapshot() == null) {
         return null;
      } else if (this.getSnapshot().getFightLog() == null) {
         return null;
      } else {
         FightLog f = this.getSnapshot().getFightLog();
         return f.getState(FightLog.Temporality.Future, this.ent);
      }
   }

   public DeathType getDeathType() {
      return this.deathType;
   }

   public boolean isFled() {
      return this.fled;
   }

   public void discard(Item i, String message, boolean onPanel) {
      this.addBuff(new DiscardItem(i));
      if (onPanel) {
         this.addEvent(new TextEvent(message));
      } else {
         this.snapshot.addEvent(new DiscardEvent(i, message));
      }
   }

   public EntSideState getCurrentSideState() {
      EntSide es = this.getEnt().getDie().getCurrentSide();
      return es == null ? null : this.getSideState(es);
   }

   public boolean isAutoLockLite() {
      boolean lite = false;

      for (Personal pt : this.getActivePersonals()) {
         lite |= pt.autoLockLite();
      }

      for (Global g : this.snapshot.getGlobals()) {
         if (g instanceof GlobalLockDiceLimit) {
            return false;
         }
      }

      return lite;
   }

   public boolean isAutoLock() {
      boolean result = false;
      EntSideState current = this.getCurrentSideState();
      if (current == null) {
         TannLog.error("Autolock null state");
      } else {
         result |= current.getCalculatedEffect().hasKeyword(Keyword.sticky);
      }

      return result;
   }

   public List<EntSideState> getAllSideStates() {
      List<EntSideState> result = new ArrayList<>();

      for (EntSide es : this.getEnt().getSides()) {
         result.add(this.getSideState(es));
      }

      return result;
   }

   public int getMissingHp() {
      return this.getMaxHp() - this.getHp();
   }

   public int getTurnsElapsed() {
      return this.turnsElapsed;
   }

   public boolean isSummonedSoNotAttacking() {
      return this.summonedSoNotAttacking;
   }

   public boolean isInadvisable(Eff e) {
      switch (e.getType()) {
         case Buff:
            if (e.getTargetingType() == TargetingType.Self) {
               return !e.getBuff().personal.isRecommended(this, this, this.getFutureState());
            }
         default:
            return false;
      }
   }

   public void onOtherDeath(EntState dead) {
      for (Personal t : this.getActivePersonals()) {
         t.onOtherDeath(this.snapshot, dead, this);
      }
   }

   public int getTotalPetrification() {
      int result = 0;

      for (EntSideState ess : this.getAllSideStates()) {
         if (ess.getCalculatedTexture() == ESB.blankPetrified.getTexture()) {
            result++;
         }
      }

      return result;
   }

   public boolean skipEquipScreen() {
      List<Personal> personals = this.getActivePersonals();

      for (int i = 0; i < personals.size(); i++) {
         if (personals.get(i).skipEquipScreen()) {
            return true;
         }
      }

      return false;
   }

   public void notJustSummoned() {
      this.summonedSoNotAttacking = false;
   }

   public boolean isAtMaxHp() {
      return this.hp == this.getMaxHp();
   }

   public boolean canSee(Hero other) {
      return this.snapshot != null && this.snapshot.getState(other) != null;
   }
}
