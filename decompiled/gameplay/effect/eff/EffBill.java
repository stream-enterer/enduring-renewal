package com.tann.dice.gameplay.effect.eff;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffBill {
   private EffType type = EffType.Blank;
   private TargetingType targetingType = TargetingType.Single;
   private Buff buff;
   private String summonType;
   private int value = -999;
   private List<ConditionalRequirement> restrictions = new ArrayList<>();
   private boolean friendly;
   private boolean basic;
   private VisualEffectType visualEffect = VisualEffectType.None;
   private List<Keyword> keywords = new ArrayList<>();
   private StateEvent event;
   private SnapshotEvent snapshotEvent;
   private Eff orFriendly;
   private Eff orEnemy;
   private Eff bonusUntargetedEffect;
   private String overrideDescription;
   private List<ConditionalRequirement> doubleConditions;

   public EffBill() {
   }

   public EffBill(Eff e) {
      this.type = e.getType();
      this.targetingType = e.getTargetingType();
      this.buff = e.getBuff();
      this.summonType = e.getSummonType();
      this.value = e.getValue();
      this.restrictions = e.getRestrictions();
      this.friendly = e.isFriendly();
      this.basic = e.isBasic();
      this.visualEffect = e.getVisual();
      this.keywords = e.getKeywords();
      this.event = e.getEvent();
      this.snapshotEvent = e.getSnapshotEvent();
      this.orFriendly = e.getOr(true);
      this.orEnemy = e.getOr(false);
      this.bonusUntargetedEffect = e.getBonusUntargetedEffect();
      this.overrideDescription = e.getOverrideDescription();
      this.doubleConditions = e.getDoubleConditions();
   }

   public Eff bEff() {
      return new Eff(
         this.type,
         this.targetingType,
         this.visualEffect,
         this.restrictions,
         this.value,
         this.buff,
         this.summonType,
         this.friendly,
         this.value != -999,
         this.keywords,
         this.orFriendly,
         this.orEnemy,
         this.bonusUntargetedEffect,
         this.basic,
         this.event,
         this.snapshotEvent,
         this.overrideDescription,
         this.doubleConditions
      );
   }

   public EffBill type(EffType type, int amount) {
      this.type = type;
      this.value = amount;
      return this;
   }

   public EffBill type(EffType type) {
      this.type = type;
      return this;
   }

   public EffBill targetType(TargetingType type) {
      this.targetingType = type;
      return this;
   }

   public EffBill value(int value) {
      this.value = value;
      return this;
   }

   public EffBill nothing() {
      return this.untargeted().visual(VisualEffectType.Skip).type(EffType.Blank);
   }

   public EffBill damage(int amount) {
      return this.type(EffType.Damage, amount);
   }

   public EffBill shield(int amount) {
      return this.friendly().type(EffType.Shield, amount);
   }

   public EffBill heal(int amount) {
      return this.friendly().type(EffType.Heal, amount).visual(VisualEffectType.HealBasic);
   }

   public EffBill healAndShield(int amount) {
      return this.friendly().type(EffType.HealAndShield, amount).visual(VisualEffectType.HealBasic);
   }

   public EffBill kill() {
      return this.type(EffType.Kill);
   }

   public EffBill reroll(int amount) {
      return this.untargeted().type(EffType.Reroll, amount);
   }

   public EffBill buff(Personal personal) {
      this.buff = new Buff(personal);
      return this.type(EffType.Buff);
   }

   public EffBill buff(Buff buff) {
      this.buff = buff;
      return this.type(EffType.Buff);
   }

   public EffBill redirectIncoming() {
      return this.friendly().type(EffType.RedirectIncoming);
   }

   public EffBill summon(String entType, int value) {
      this.summonType = entType;
      this.visual(VisualEffectType.Summon);
      this.untargeted();
      return this.type(EffType.Summon, value);
   }

   public EffBill recharge() {
      return this.friendly().type(EffType.Recharge);
   }

   public EffBill resurrect(int value) {
      return this.untargeted().friendly().type(EffType.Resurrect, value);
   }

   public EffBill enchant(String modName) {
      this.summonType = modName;
      return this.type(EffType.Enchant).untargeted();
   }

   public EffBill group() {
      return this.targetType(TargetingType.Group);
   }

   public EffBill untargeted() {
      return this.targetType(TargetingType.Untargeted);
   }

   public EffBill self() {
      return this.friendly().targetType(TargetingType.Self);
   }

   public EffBill event(StateEvent event) {
      this.event = event;
      this.friendly();
      return this.type(EffType.Event);
   }

   public EffBill snapshotEvent(SnapshotEvent event) {
      this.snapshotEvent = event;
      this.untargeted();
      return this.type(EffType.SnapshotEvent);
   }

   public EffBill keywords(Keyword... keywords) {
      return this.keywords(Arrays.asList(keywords));
   }

   public EffBill keywords(List<Keyword> keywords) {
      if (this.targetingType == TargetingType.Group && keywords.contains(Keyword.cleave)) {
         throw new RuntimeException("No cleave with group");
      } else {
         this.keywords.addAll(keywords);
         return this;
      }
   }

   public EffBill friendly() {
      this.friendly = true;
      return this;
   }

   public EffBill enemy() {
      this.friendly = false;
      return this;
   }

   public EffBill visual(VisualEffectType visualEffect) {
      this.visualEffect = visualEffect;
      return this;
   }

   public EffBill restrict(StateConditionType stateConditionType) {
      return this.restrict(new GSCConditionalRequirement(stateConditionType));
   }

   public EffBill unrestrict() {
      this.restrictions.clear();
      return this;
   }

   public EffBill restrict(ConditionalRequirement... restriction) {
      Tann.addAll(this.restrictions, restriction);
      return this;
   }

   public EffBill or(EffBill orFriendly, EffBill orEnemy) {
      Eff of = orFriendly.bEff();
      Eff oe = orEnemy.bEff();
      if (of.isFriendly() && !oe.isFriendly() && of.needsTarget() && oe.needsTarget()) {
         this.type = EffType.Or;
         this.orFriendly = of;
         this.orEnemy = oe;
         this.value = -999;
         return this;
      } else {
         throw new RuntimeException("or must be friendly and enemy in the right order");
      }
   }

   public EffBill basic() {
      this.basic = true;
      return this;
   }

   public EffBill mana(int value) {
      this.type = EffType.Mana;
      this.targetingType = TargetingType.Untargeted;
      this.value = value;
      return this;
   }

   public EffBill doubleMana() {
      return this.mana(0).keywords(Keyword.charged);
   }

   public EffBill setToHp(int val) {
      this.type = EffType.SetToHp;
      this.value = val;
      return this;
   }

   public EffBill specialAddKeyword(Keyword... keywords) {
      return this.friendly().buff(new Buff(1, new AffectSides(new AddKeyword(keywords)))).visual(VisualEffectType.Boost);
   }

   public EffBill specialAddKeywordPermanent(Keyword... keywords) {
      return this.friendly().buff(new Buff(new AffectSides(new AddKeyword(keywords)))).visual(VisualEffectType.Boost);
   }

   public EffBill overrideDescription(String description) {
      this.overrideDescription = description;
      return this;
   }

   public EffBill replaceBlanksWith(EntSide es) {
      return this.friendly().group().buff(new Buff(1, new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(es))));
   }

   public EffBill flee() {
      if (this.targetingType == TargetingType.Single) {
         this.self();
      }

      return this.kill().visual(VisualEffectType.Flee);
   }

   public EffBill bonusUntargeted(EffBill effBills) {
      return this.bonusUntargeted(effBills.bEff());
   }

   public EffBill bonusUntargeted(Eff effs) {
      this.bonusUntargetedEffect = effs;
      return this;
   }

   public EffBill justTarget() {
      this.type = EffType.JustTarget;
      return this;
   }

   public EffBill conMult(List<ConditionalRequirement> conMult) {
      this.doubleConditions = conMult;
      return this;
   }

   public EffBill clearRestr() {
      this.restrictions.clear();
      return this;
   }
}
