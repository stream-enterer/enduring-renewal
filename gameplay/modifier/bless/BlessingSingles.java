package com.tann.dice.gameplay.modifier.bless;

import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.fightLog.event.snapshot.ManaGainEvent;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.generation.CurseDistribution;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge.ChallengePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.global.GlobalMaxMana;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartOfCombat;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartTurnEff;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuality;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllEntitiesRestricted;
import com.tann.dice.gameplay.trigger.global.linked.GlobalHeroTier;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinkedGeneric;
import com.tann.dice.gameplay.trigger.global.linked.GlobalMulti;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorChallenge;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLootSpecificTier;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorStandardLoot;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.roll.GlobalKeepRerolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementAllButFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementBoss;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementMod;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementRange;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementAll;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementEveryN;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirstN;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.gameplay.trigger.global.spell.GlobalLearnSpell;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellCostChange;
import com.tann.dice.gameplay.trigger.personal.AvoidDeathPenalty;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeToMyPosition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.MultiplyEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.hp.StartDamagedPer;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyInvItems;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerNHeroLevel;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlessingSingles {
   private static Modifier addBlessAfter(int level, int blessValue) {
      float tier = CurseDistribution.getMultLevelAndAfter(level, blessValue);
      String name = "L" + level + " Blessing";
      if (blessValue != 4) {
         name = name + "^" + blessValue;
      }

      return new Modifier(
         tier,
         name,
         new GlobalLevelRequirement(
            new LevelRequirementRange(level),
            new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, blessValue, true, ModifierPickContext.Difficulty_But_Midgame))
         )
      );
   }

   public static List<Modifier> makeSingles() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(
         Arrays.asList(
            new Modifier(1.0F, "Pipe Dream", new GlobalHeroes(new AffectSides(SpecificSidesType.MiddleTwo, new AddKeyword(Keyword.sprint)))),
            new Modifier(4.0F, "Me First", new GlobalHeroes(new AffectSides(new AddKeyword(Keyword.first)))),
            new Modifier(1.0F, "Latent", new GlobalHeroes(new StartRegenned(3)), new GlobalHeroes(new StartPoisoned(3))),
            new Modifier(
               TierUtils.doubleXp(), "Double XP", new GlobalLevelRequirement(new LevelRequirementMod(2, 1), new GlobalAddPhase(new PhaseGeneratorLevelup()))
            ),
            new Modifier(
               TierUtils.doubleLoot(),
               "Double Loot",
               new GlobalLevelRequirement(new LevelRequirementMod(2, 0), new GlobalAddPhase(new PhaseGeneratorStandardLoot()))
            ),
            new Modifier(4.0F, "Monster Right Pain", new GlobalMonsters(new AffectSides(SpecificSidesType.Right, new AddKeyword(Keyword.pain)))),
            new Modifier(10.0F, "Monster Left Pain", new GlobalMonsters(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.pain)))),
            new Modifier(25.0F, "MonsterMidDeath", new GlobalMonsters(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.death))))
         )
      );
      result.add(addBlessAfter(9, 4));
      result.add(addBlessAfter(9, 10));
      result.add(addBlessAfter(19, 4));
      result.add(
         new Modifier(
            5.0F,
            "Nicknack Knapsack",
            new GlobalLevelRequirement(new LevelRequirementAllButFirst(), new GlobalAddPhase(new PhaseGeneratorLootSpecificTier(1))),
            new GlobalHeroes(new ItemSlots(1))
         )
      );
      result.add(new Modifier(ModTierUtils.bonusAllHeroHp(1.9F), "Gym", new GlobalHeroes(new PersonalPerN(new MaxHP(1), new PerNHeroLevel()))));
      result.add(new Modifier(3.0F, "Unsummon", new GlobalMonsters(new AffectSides(new TypeCondition(EffType.Summon), new FlatBonus(-1)))));
      result.add(new Modifier(ModTierUtils.extraMonsterHP(-0.2F), "Damaged Monsters", new GlobalMonsters(new StartDamagedPer(1, 4))));
      result.add(new Modifier(7.0F, "Free Turn", new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalMonsters(new AffectSides(new FlatBonus(-1))))));
      result.add(new Modifier(ModTierUtils.startWithMana(0.8F), "Mana Spring", new GlobalSpellCostChange(-2, 6)));
      result.add(new Modifier(ModTierUtils.bonusAllHeroHp(2.0F), "2 hero hp", new GlobalHeroes(new MaxHP(2))));
      result.add(new Modifier(ModTierUtils.bonusAllHeroHp(4.0F), "4 hero hp", new GlobalMulti(new GlobalHeroes(new MaxHP(2)), 2)).rarity(Rarity.HUNDREDTH));
      result.add(
         new Modifier(1.0F, "Rest", new GlobalLevelRequirement(new LevelRequirementRange(19), new GlobalHeroes(new AffectSides(new FlatBonus(5)))))
            .rarity(Rarity.FIFTH)
      );
      result.add(new Modifier(1.0F, "5 max mana", new GlobalMaxMana(5)));
      result.add(
         new Modifier(3.0F, "Essence Thief", new GlobalMonsters(new OnDeathEffect(new EffBill().mana(1).bEff(), new ManaGainEvent(1, "Essence Thief"), false)))
      );
      result.add(new Modifier(3.0F, "Healest", new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Heal), new FlatBonus(1)))).rarity(Rarity.TENTH));
      result.add(new Modifier(2.0F, "Keep Rolls", new GlobalKeepRerolls()));
      result.add(
         new Modifier(8.0F, "Reliable", new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Blank), new ChangeToMyPosition(SpecificSidesType.Middle))))
      );
      result.add(new Modifier(4.0F, "Shield Plus", new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Shield), new FlatBonus(1)))).rarity(Rarity.TENTH));
      result.add(new Modifier(4.0F, "Treasure Seeker", new GlobalHeroes(new ItemSlots(1)), new GlobalItemQuality(1)));
      result.add(new Modifier(6.0F, "Monster Blank", new GlobalMonsters(new AffectSides(SpecificSidesType.Left, new ReplaceWithBlank(ChoosableType.Modifier)))));
      result.add(new Modifier(3.0F, "Poison Immunity", new GlobalHeroes(new DamageImmunity(true, false, false))));
      result.add(new Modifier(3.0F, "Turn 3 Heal", new GlobalTurnRequirement(3, new GlobalStartTurnEff(new EffBill().group().healAndShield(3).bEff()))));
      result.add(
         new Modifier(
            3.0F,
            "Greased Dice",
            new GlobalTurnRequirement(new TurnRequirementN(1), new GlobalBonusRerolls(1)),
            new GlobalLevelRequirement(new LevelRequirementBoss(), new GlobalBonusRerolls(1))
         )
      );
      result.add(new Modifier(ModTierUtils.doubleSides(5.0F), "Double Pips", new GlobalHeroes(new AffectSides(new MultiplyEffect(2)))));
      result.add(new Modifier(ModTierUtils.doubleSides(10.0F), "Triple Pips", new GlobalHeroes(new AffectSides(new MultiplyEffect(3)))));
      result.add(new Modifier(3.0F, "Lucky Start", new GlobalTurnRequirement(new TurnRequirementN(1), new GlobalBonusRerolls(2))));
      result.add(
         new Modifier(
            7.0F, "Great Start", new GlobalTurnRequirement(new TurnRequirementN(1), new GlobalHeroes(new AffectSides(new FlatBonus(1)).buffPriority()))
         )
      );
      result.add(
         new Modifier(
            ModTierUtils.startWithMana(2.0F),
            "Fizzing",
            new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalStartTurnEff(new EffBill().mana(2).bEff()))
         )
      );
      result.add(
         new Modifier(
            ModTierUtils.startWithMana(4.0F),
            "Crackling",
            new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalStartTurnEff(new EffBill().mana(4).bEff()))
         )
      );
      result.add(new Modifier(3.0F, "Preparation", new GlobalStartOfCombat(new EffBill().shield(2).group().bEff())));
      result.add(
         new Modifier(
            3.0F,
            "Save Spell",
            new GlobalLearnSpell(
               new SpellBill()
                  .cost(1)
                  .title("Save")
                  .eff(new EffBill().friendly().keywords(Keyword.cleanse, Keyword.singleCast).healAndShield(5).visual(VisualEffectType.Undying))
            )
         )
      );
      result.add(
         new Modifier(
            4.0F, "Bolt Spell", new GlobalLearnSpell(new SpellBill().cost(3).title("bolt").eff(new EffBill().damage(5).visual(VisualEffectType.Lightning)))
         )
      );
      result.add(
         new Modifier(
            ModTierUtils.startWithMana(2.0F),
            "Jewelled Chalice",
            new GlobalTurnRequirement(
               new TurnRequirementFirstN(4), new GlobalStartTurnEff(new ManaGainEvent(1, "Jewelled Chalice"), new EffBill().mana(1).bEff())
            )
         )
      );
      result.add(
         new Modifier(
            ModTierUtils.startWithMana(4.0F),
            "Infinite Chalice",
            new GlobalTurnRequirement(new TurnRequirementAll(), new GlobalStartTurnEff(new ManaGainEvent(1, "Infinite Chalice"), new EffBill().mana(1).bEff())),
            new GlobalTurnRequirement(
               new TurnRequirementEveryN(4), new GlobalStartTurnEff(new ManaGainEvent(2, "Infinite Chalice"), new EffBill().mana(2).bEff())
            )
         )
      );
      result.add(new Modifier(5.0F, "Fumes", new GlobalAllEntitiesRestricted(false, TargetingRestriction.MostHealth, new StartPoisoned(2))));
      result.add(new Modifier(3.0F, "Youth", new GlobalHeroTier(1, new MaxHP(4)), new GlobalHeroTier(2, new MaxHP(2))));
      result.add(new Modifier(2.0F, "Underworld Deal", new GlobalHeroes(new AvoidDeathPenalty())));
      result.add(new Modifier(6.0F, "Hamstring", new GlobalMonsters(new AffectSides(SpecificSidesType.RightTwo, new FlatBonus(-1, -1)))));
      result.add(new Modifier(3.0F, "Cataclysm", new GlobalTurnRequirement(new TurnRequirementN(7), new GlobalEndTurnEff(new EffBill().kill().group().bEff()))));
      result.add(new Modifier(2.0F, "Deep Pockets", new GlobalHeroes(new ItemSlots(1))));
      result.add(ItemLib.makeBlessingMulti("Stun Specialist", ItemLib.byName("Fearless"), ItemLib.byName("Wand of Stun")));
      result.add(ItemLib.makeBlessingMulti("Growth Fan", ItemLib.byName("Seedling"), ItemLib.byName("Glowing Egg")));
      result.add(new Modifier(3.0F, "Middle Shield", new GlobalHeroes(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.selfShield)))));
      result.add(new Modifier(ModTierUtils.heroBonusAllSides(1.0F) * 5.0F, "Favour of Horus", new GlobalHeroes(new AffectSides(new FlatBonus(1)))));
      result.add(new Modifier(2.0F, "Challenge Each Fight", new GlobalAddPhase(new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Standard))));
      result.add(
         new Modifier(
            ModTierUtils.startWithMana(1.8F),
            "Threee",
            new GlobalTurnRequirement(new TurnRequirementEveryN(3), new GlobalStartTurnEff(new EffBill().mana(3).bEff()))
         )
      );
      result.add(new Modifier(25.0F, "Barrel Time", new GlobalMonsters(new DamageAdjacentsOnDeath(5))));
      result.add(new Modifier(5.0F, "Bone Math", new GlobalMonsters(new DamageAdjacentsOnDeath(1))));
      result.add(new Modifier(5.0F, "Display Case", new GlobalLinkedGeneric(new GlobalHeroes(new CopyInvItems()), new GlobalLinkedGeneric.GenCon() {
         @Override
         public String describe() {
            return "there is exactly one item in the inventory";
         }

         @Override
         public boolean holdsFor(DungeonContext context, int turn) {
            return context.getParty().getItems(false).size() == 1;
         }
      })));
      result.add(new Modifier(200.0F, "Peace", new GlobalMonsters(new AffectSides(new FlatBonus(-5)))));
      return result;
   }
}
