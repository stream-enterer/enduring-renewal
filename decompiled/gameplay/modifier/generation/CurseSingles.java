package com.tann.dice.gameplay.modifier.generation;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.GlobalCollision;
import com.tann.dice.gameplay.trigger.global.GlobalDuplicateMonsters;
import com.tann.dice.gameplay.trigger.global.GlobalReinforcementsLimitMultiply;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.level.GlobalAddMonster;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllEntitiesRestricted;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllMonstersExcept;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSize;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSpecificEntTypes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementBoss;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.NotLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirstN;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.gameplay.trigger.global.spell.GlobalAbilitiesLimit;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellCostChange;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellKeyword;
import com.tann.dice.gameplay.trigger.global.spell.change.MultiplyChange;
import com.tann.dice.gameplay.trigger.personal.Armour;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.OnRescue;
import com.tann.dice.gameplay.trigger.personal.Permadeath;
import com.tann.dice.gameplay.trigger.personal.SetStartingHp;
import com.tann.dice.gameplay.trigger.personal.Undying;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.EvennessCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.ExactlyCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HighestCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveAllKeywords;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SetValue;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.hp.BonusHpPerBase;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHpSet;
import com.tann.dice.gameplay.trigger.personal.immunity.AbilityImmune;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerNHeroLevel;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerNItem;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.gameplay.trigger.personal.position.BackRow;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPetrified;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import java.util.Arrays;
import java.util.List;

public class CurseSingles {
   public static List<Modifier> makeSingles() {
      return Arrays.asList(
         new Modifier(
               -1.0F,
               "Ouroboros",
               new GlobalSpecificEntTypes(
                  MonsterTypeLib.byName("imp"), new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(EntSidesBlobSmall.summonHexia.val(1)))
               )
            )
            .rarity(Rarity.FIVE_HUNDREDTH),
         new Modifier(ModTierUtils.blanked(5.0F), "Blank", new GlobalHeroes(new AffectSides(new ReplaceWithBlank(ChoosableType.Modifier)))),
         new Modifier(ModTierUtils.blankedStasis(5.0F), "Jammed", new GlobalHeroes(new AffectSides(new ReplaceWith(ESB.blankStasis)))),
         new Modifier(ModTierUtils.blankedStuck(5.0F), "Stuck", new GlobalHeroes(new AffectSides(new ReplaceWith(ESB.blankStuck)))),
         new Modifier(
            -4.0F,
            "Heavy Sleeper",
            new GlobalAllEntitiesRestricted(true, new GenericStateCondition(StateConditionType.DiedLasFight), new AffectSides(new ReplaceWith(ESB.blankStasis)))
         ),
         new Modifier(-6.0F, "Permadeath", new GlobalHeroes(new Permadeath())),
         new Modifier(-2.0F, "Cooldown Spells", new GlobalSpellKeyword(Keyword.cooldown)),
         new Modifier(-1.0F, "Horde", new GlobalReinforcementsLimitMultiply(1.5F), GlobalRarity.fromRarity(Rarity.FIFTH)),
         new Modifier(
            -3.0F,
            "Spider Soul",
            new GlobalTurnRequirement(
               new TurnRequirementFirst(), new GlobalMonsters(new OnDeathEffect(new EffBill().kill().targetType(TargetingType.Top).bEff()))
            )
         ),
         new Modifier(ModTierUtils.startPoisoned(4.5F), "Item Poison", new GlobalHeroes(new PersonalPerN(new StartPoisoned(1), new PerNItem()))),
         new Modifier(-3.0F, "Hero Immunity", new GlobalHeroes(new AbilityImmune())),
         new Modifier(-5.0F, "Monster Immunity", new GlobalMonsters(new AbilityImmune())),
         new Modifier(-3.0F, "Improvised Armour", new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalMonsters(new Armour(1)))),
         new Modifier(-2.0F, "Summoning Circle", new GlobalMonsters(new AffectSides(new TypeCondition(EffType.Summon), new FlatBonus(1)))),
         new Modifier(
               -1.0F,
               "Bonezone",
               new GlobalSpecificEntTypes(MonsterTypeLib.byName("bones"), new AffectSides(new ReplaceWith(EntSidesBlobSmall.summonBones.val(2))))
            )
            .rarity(Rarity.THOUSANDTH),
         new Modifier(-12.0F, "Deaths Door", new GlobalHeroes(new SetStartingHp(1))),
         new Modifier(-8.0F, "Stone Rain", new GlobalEndTurnEff(new EffBill().friendly().group().damage(1).keywords(Keyword.petrify))),
         new Modifier(ModTierUtils.startPoisoned(2.1F) * 5.0F, "Poison Tendrils", new GlobalHeroes(new PersonalPerN(new StartPoisoned(1), new PerNHeroLevel()))),
         new Modifier(-2.0F, "Contagion", new GlobalAllEntitiesRestricted(true, TargetingRestriction.MostHealth, new StartPoisoned(1)))
            .rarity(Rarity.HUNDREDTH),
         new Modifier(
               -3.0F,
               "Turn 3 Death",
               new GlobalTurnRequirement(new TurnRequirementN(3), new GlobalHeroes(new AffectSides(new AddKeyword(Keyword.death)).buffPriority()))
            )
            .rarity(Rarity.TENTH),
         new Modifier(-3.0F, "Reduced Defence", new GlobalHeroes(new IncomingEffBonus(-1, EffType.Heal, EffType.Shield))),
         new Modifier(-8.0F, "Heartless", new GlobalHeroes(new OnRescue(new EffBill().self().kill().bEff()).hide())).rarity(Rarity.TENTH),
         new Modifier(-12.0F, "Slime Horde", new GlobalAddMonster(MonsterTypeLib.byName("slimelet"), 10)),
         new Modifier(-7.0F, "4hp", new GlobalHeroes(new MaxHpSet(4))).rarity(Rarity.TENTH),
         new Modifier(-3.0F, "Uhh", new GlobalMonsters(new AffectSides(new SetValue(3)))).rarity(Rarity.THOUSANDTH),
         new Modifier(-7.0F, "Rise", GlobalAllMonstersExcept.summonOnDeath(MonsterTypeLib.byName("bones"))),
         new Modifier(-8.0F, "Grave spam", GlobalAllMonstersExcept.summonOnDeath(MonsterTypeLib.byName("grave"))).rarity(Rarity.HUNDREDTH),
         new Modifier(-4.0F, "Odd single use", new GlobalHeroes(new AffectSides(new EvennessCondition(false), new AddKeyword(Keyword.singleUse)))),
         new Modifier(-2.0F, "3 pip pain", new GlobalHeroes(new AffectSides(new ExactlyCondition(3), new AddKeyword(Keyword.pain)))),
         new Modifier(-8.0F, "No Spells", new GlobalAbilitiesLimit(0)).rarity(Rarity.TENTH),
         new Modifier(-4.0F, "Sticky Fingers", new GlobalHeroes(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.sticky)))),
         new Modifier(-5.0F, "Single Spells", new GlobalSpellKeyword(Keyword.singleCast)),
         new Modifier(-1.0F, "Boss Bones", new GlobalLevelRequirement(new LevelRequirementBoss(), new GlobalAddMonster(MonsterTypeLib.byName("bones")))),
         new Modifier(
            -2.0F,
            "Non boss Bones",
            new GlobalLevelRequirement(new NotLevelRequirement(new LevelRequirementBoss()), new GlobalAddMonster(MonsterTypeLib.byName("bones")))
         ),
         new Modifier(ModTierUtils.keywordToSides(SpecificSidesType.Top, ModTierUtils.blanked(5.0F)), "Stony Grasp", new GlobalHeroes(new StartPetrified(1))),
         new Modifier(ModTierUtils.blanked(1.08F) * 5.0F, "Stony Grasp/2", new GlobalHeroes(new StartPetrified(6))),
         new Modifier(-2.0F, "Flighty", new GlobalAllEntitiesRestricted(false, new GenericStateCondition(StateConditionType.Damaged), new BackRow(false))),
         new Modifier(-5.0F, "Barricade", new GlobalAllEntitiesRestricted(false, TargetingRestriction.AllMostDamaged, new BackRow(false))),
         new Modifier(-5.0F, "Heavy Weapons", new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.heavy)))),
         new Modifier(-2.0F, "Bones Bones", new GlobalHeroes(new DamageAdjacentsOnDeath(1))).rarity(Rarity.FIVE_HUNDREDTH),
         new Modifier(
            -1.0F,
            "Archery Training",
            new GlobalSpecificEntTypes(new AffectSides(new FlatBonus(2)), MonsterTypeLib.byName("archer"), MonsterTypeLib.byName("sniper"))
         ),
         new Modifier(-4.0F, "Depleted Spells", new GlobalSpellKeyword(null, Keyword.deplete)),
         new Modifier(-5.0F, "Expensive Spells", new GlobalSpellCostChange(1)),
         new Modifier(-6.0F, "Expensiver Spells", new GlobalSpellCostChange(new MultiplyChange(2))),
         new Modifier(-3.0F, "Left Weak", new GlobalHeroes(new AffectSides(SpecificSidesType.Left, new FlatBonus(-1)))),
         new Modifier(-7.0F, "Migraine", new GlobalHeroes(new AffectSides(SpecificSidesType.LeftTwo, new FlatBonus(-1)))),
         new Modifier(-5.0F, "Undying Monsters", new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalMonsters(new Undying()))),
         new Modifier(-12.0F, "Undying Monsters/2", new GlobalTurnRequirement(new TurnRequirementFirstN(2), new GlobalMonsters(new Undying()))),
         new Modifier(
            ModTierUtils.monsterImmuneTurnOne(1.0F),
            "Immune Monsters",
            new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalMonsters(new DamageImmunity(true, true, true)))
         ),
         new Modifier(
            ModTierUtils.monsterImmuneTurnOne(0.6F), "Stone First", new GlobalTurnRequirement(1, new GlobalMonsters(new StoneSpecialHp(PipLoc.all())))
         ),
         new Modifier(
            ModTierUtils.monsterImmuneTurnTwo(1.0F),
            "Turn 2 Immune",
            new GlobalTurnRequirement(new TurnRequirementN(2), new GlobalMonsters(new DamageImmunity(true, true, true)))
         ),
         new Modifier(-4.0F, "Wanded", new GlobalHeroes(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.singleUse)))),
         new Modifier(
            -6.0F, "Creaky Joints", new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalHeroes(new AffectSides(new FlatBonus(-1)).buffPriority()))
         ),
         new Modifier(-5.0F, "Highest Pain", new GlobalHeroes(new AffectSides(new HighestCondition(), new AddKeyword(Keyword.pain)))),
         new Modifier(-8.0F, "Lowest Exert", new GlobalHeroes(new AffectSides(new HighestCondition(false), new AddKeyword(Keyword.exert)))),
         new Modifier(-4.0F, "Fewer Reroll", new GlobalBonusRerolls(-1)),
         new Modifier(-10.0F, "2 Fewer Rerolls", new GlobalBonusRerolls(-2)),
         new Modifier(
            ModTierUtils.monsterShieldEachTurn(1.55F),
            "One Kill",
            new GlobalMonsters(new OnDeathEffect(new EffBill().friendly().buff(new Buff(1, new DamageImmunity(true, true))).group().bEff())),
            new GlobalCollision(Collision.ENEMY_SHIELD)
         ),
         new Modifier(-13.0F, "Curse of Horus", new GlobalHeroes(new AffectSides(new FlatBonus(-1)))),
         new Modifier(
            -5.0F,
            "Sticky Blanks",
            new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Blank), new AddKeyword(Keyword.sticky))),
            GlobalRarity.fromRarity(Rarity.TENTH)
         ),
         new Modifier(
            -3.0F,
            "Static Blanks",
            new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Blank), new AddKeyword(Keyword.stasis))),
            GlobalRarity.fromRarity(Rarity.TENTH)
         ),
         new Modifier(-20.0F, "Double Monsters", new GlobalDuplicateMonsters(2), GlobalRarity.fromRarity(Rarity.THIRD)),
         new Modifier(-3.0F, "Mundane", new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Mana), new ChangeType(ESB.dmg, "damage")))),
         new Modifier(-3.0F, "Back to Basics", new GlobalHeroes(new AffectSides(new RemoveAllKeywords())), GlobalRarity.fromRarity(Rarity.THIRD)),
         new Modifier(ModTierUtils.getBonusMonsterHpFlat(5.0F), "Double Monster HP", new GlobalMonsters(new BonusHpPerBase(1, 1))),
         new Modifier(-1.0F, "Left Sticky", new GlobalHeroes(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.sticky)))),
         new Modifier(
               -3.0F,
               "Dread",
               new GlobalAllEntitiesRestricted(true, new GenericStateCondition(StateConditionType.Dying), new AffectSides(new FlatBonus(true, -1)))
            )
            .rarity(Rarity.TENTH),
         new Modifier(-1.0F, "Training", new GlobalSize(EntSize.reg, new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dmg.val(6)))))
      );
   }
}
