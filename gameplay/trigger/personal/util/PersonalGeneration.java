package com.tann.dice.gameplay.trigger.personal.util;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.NotRequirement;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.trigger.global.gen.GlobalGeneration;
import com.tann.dice.gameplay.trigger.personal.Armour;
import com.tann.dice.gameplay.trigger.personal.CopySide;
import com.tann.dice.gameplay.trigger.personal.DeathAfterNumHits;
import com.tann.dice.gameplay.trigger.personal.ForceEquip;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.MultiplyDamageTaken;
import com.tann.dice.gameplay.trigger.personal.OnDamage;
import com.tann.dice.gameplay.trigger.personal.OnRescue;
import com.tann.dice.gameplay.trigger.personal.Permadeath;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.ShieldsRemaining;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.eff.AfterUseDiceEffect;
import com.tann.dice.gameplay.trigger.personal.eff.EndOfTurnEff;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfTurnSelf;
import com.tann.dice.gameplay.trigger.personal.equipRestrict.EquipRestrictCol;
import com.tann.dice.gameplay.trigger.personal.equipRestrict.EquipRestrictHp;
import com.tann.dice.gameplay.trigger.personal.hp.BonusHpPerBase;
import com.tann.dice.gameplay.trigger.personal.hp.EmptyMaxHp;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.immunity.AbilityImmune;
import com.tann.dice.gameplay.trigger.personal.immunity.HealImmunity;
import com.tann.dice.gameplay.trigger.personal.immunity.ShieldImmunity;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyAlliedItems;
import com.tann.dice.gameplay.trigger.personal.linked.MultiDifferentPersonal;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.linked.snapshotCondition.SnapshotCondition;
import com.tann.dice.gameplay.trigger.personal.linked.snapshotCondition.SnapshotConditionType;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.PersonalConditionLink;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.gameplay.trigger.personal.spell.AfterUseAbility;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnSpell;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPetrified;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PersonalGeneration {
   public static Personal random(Random r) {
      int val = r.nextInt(42);
      switch (val) {
         case 0:
            return new MaxHP(r.nextInt(10) - 5);
         case 1:
            return new CopyAlliedItems(r.nextInt(10));
         case 2:
            return new AfterUseDiceEffect(EffUtils.random(r, false));
         case 3:
            return new OnDamage(EffUtils.random(r), true, null);
         case 4:
            return new DamageAdjacentsOnDeath(r.nextInt(5));
         case 5:
            return new EndOfTurnEff(EffUtils.random(r, false));
         case 6:
            return new AfterUseAbility(r.nextInt(4), EffUtils.random(r, false));
         case 7:
         default:
            return makeRandomAffectSides(r);
         case 8:
            return new ItemSlots(r.nextInt(3));
         case 9:
            return new StartPoisoned(r.nextInt(10));
         case 10:
            return new Permadeath();
         case 11:
            return new ShieldsRemaining(EffUtils.random(r, false), r.nextBoolean());
         case 12:
            return new OnDeathEffect(EffUtils.random(r, false));
         case 13:
         case 14:
         case 15:
            return makeLinked(r, random(r));
         case 16:
         case 17:
            return makeMultiDifferent(r);
         case 18:
            return new StartPetrified(1 + r.nextInt(2) * 5);
         case 19:
            return new CopySide(SpecificSidesType.getNiceSidesTypeSingle(r), SpecificSidesType.getNiceSidesTypeSingle(r));
         case 20:
            return new DeathAfterNumHits(r.nextInt(6) + 2);
         case 21:
            return new IncomingEffBonus(r.nextInt(5) - 2, r.nextBoolean(), EffType.niceRandom(r));
         case 22:
            return new LearnSpell(Tann.random(SpellLib.makeAllSpellsList(), r));
         case 23:
            return new MultiplyDamageTaken(r.nextInt(4) + 1);
         case 24:
            return new StartOfTurnSelf(EffUtils.random(r, true));
         case 25:
            return new BonusHpPerBase(r.nextInt(5), r.nextInt(8) + 1);
         case 26:
            return new EmptyMaxHp(r.nextInt(10));
         case 27:
            return new AbilityImmune();
         case 28:
            return new ShieldImmunity();
         case 29:
            return new HealImmunity();
         case 30:
            return new StartRegenned(r.nextInt(5));
         case 31:
            return new Armour(r.nextInt(4));
         case 32:
            return new OnRescue(EffUtils.random(r, false));
      }
   }

   private static Personal makeMultiDifferent(Random r) {
      List<Personal> personals = new ArrayList<>();
      personals.add(randomThatOnlyMakesSenseWithMore(r));
      int amtOthers = 1 + r.nextInt(2);

      for (int i = 0; i < amtOthers; i++) {
         Personal p = random(r);
         personals.add(p);
      }

      return new MultiDifferentPersonal(personals);
   }

   private static Personal randomThatOnlyMakesSenseWithMore(Random r) {
      int val = r.nextInt(5);
      switch (val) {
         case 0:
            return new AsIfHasItem(ItemLib.random(r));
         case 1:
            return new EquipRestrictCol(Tann.random(HeroCol.basics(), r));
         case 2:
            return new EquipRestrictCol(Tann.random(HeroCol.values(), r));
         case 3:
            return new EquipRestrictHp();
         default:
            return new ForceEquip();
      }
   }

   public static AffectSides makeRandomAffectSides(Random r) {
      int numConditions = r.nextInt(2);
      List<AffectSideCondition> cond = new ArrayList<>();

      for (int i = 0; i < numConditions; i++) {
         cond.add(AffectSideCondition.makeRandom(r));
      }

      int numEffects = Math.min(1, r.nextInt(4) + 1);
      List<AffectSideEffect> aff = new ArrayList<>();

      for (int i = 0; i < numEffects; i++) {
         aff.add(AffectSideEffect.makeRandom(r));
      }

      return new AffectSides(cond, aff);
   }

   private static Personal makeLinked(Random r, Personal p) {
      int val = r.nextInt(6);
      switch (val) {
         case 0:
            if (p.allTurnsOnly()) {
               return p;
            }

            return new PersonalTurnRequirement(GlobalGeneration.randomTR(r), p);
         case 1:
            return PersonalPerN.basicMultiple(r.nextInt(4), p);
         case 2:
         case 3:
         case 4:
            return new PersonalConditionLink(makeConReq(r), p);
         default:
            return new PersonalConditionLink(new GenericStateCondition(ra(StateConditionType.values(), r)), p);
      }
   }

   private static ConditionalRequirement makeConReq(Random r) {
      int val = r.nextInt(5);
      switch (val) {
         case 0:
         case 1:
         case 2:
         case 3:
            return new SnapshotCondition(Tann.random(SnapshotConditionType.values(), r), r.nextInt(6));
         case 4:
         default:
            return new NotRequirement(makeConReq(r));
      }
   }

   public static <T> T ra(T[] vals, Random r) {
      return AffectSideEffect.ra(vals, r);
   }
}
