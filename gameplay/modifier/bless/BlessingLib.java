package com.tann.dice.gameplay.modifier.bless;

import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.event.snapshot.ManaGainEvent;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.generation.CurseDistribution;
import com.tann.dice.gameplay.modifier.generation.CurseLib;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.gameplay.modifier.generation.MMS;
import com.tann.dice.gameplay.modifier.generation.ModMaker;
import com.tann.dice.gameplay.modifier.generation.TierMaker;
import com.tann.dice.gameplay.modifier.generation.tierMaker.TierMakerAsc;
import com.tann.dice.gameplay.modifier.generation.tierMaker.TierMakerPreset;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.LevelupHero;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartTurnEff;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuality;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuantity;
import com.tann.dice.gameplay.trigger.global.item.GlobalLevelupQuantity;
import com.tann.dice.gameplay.trigger.global.linked.GlobalEveryNthDice;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.global.linked.perN.PerDefeatedBossGlobal;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorStandardLoot;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementBoss;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementRange;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirst;
import com.tann.dice.gameplay.trigger.global.spell.GlobalNthSpellIsFree;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.finalLayer.SidesMin;
import com.tann.dice.gameplay.trigger.personal.hp.EmptyMaxHp;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartVulnerable;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlessingLib {
   public static List<Modifier> makeAll() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(BlessingSingles.makeSingles());
      result.addAll(makeRerolls());
      result.addAll(makeMegaLevelup());
      result.addAll(makeAntiMortal());
      result.addAll(makePerBoss());
      result.addAll(makeChains());
      result.addAll(makeNth());
      result.addAll(makeRewards());
      return result;
   }

   private static List<Modifier> makeNth() {
      List<Modifier> result = new ArrayList<>();
      result.add(GlobalEveryNthDice.makeNthKeyword(5, Keyword.copycat));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.deathwish));
      result.add(GlobalEveryNthDice.makeNthKeyword(5, Keyword.selfShield));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.selfShield));
      result.add(GlobalEveryNthDice.makeNthKeyword(2, Keyword.selfShield));
      result.add(GlobalEveryNthDice.makeNthKeyword(5, Keyword.selfHeal));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.selfHeal));
      result.add(GlobalEveryNthDice.makeNthKeyword(2, Keyword.selfHeal));
      result.add(GlobalEveryNthDice.makeNthKeyword(5, Keyword.growth));
      return result;
   }

   public static List<Modifier> makeChains() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(GenUtils.bChain("Better Items", 10, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return (i + 1) * 2;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalItemQuality(i + 1);
         }
      }));
      result.addAll(GenUtils.bChain("Essence Capture", 6, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return ModTierUtils.startWithMana((i + 1) * 2.3F);
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalHeroes(new OnDeathEffect(new EffBill().mana(1 + i).bEff(), new ManaGainEvent(1 + i, "Essence Capture"), false));
         }
      }));
      result.addAll(GenUtils.bChain("Leyline", new TierMakerPreset(2, 3, 5), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalNthSpellIsFree(Tann.ith(i, 8, 5, 1), false);
         }
      }));
      result.addAll(CurseLib.makeMonsterFlatHP(true));
      result.addAll(GenUtils.bChain("Hunt", 3, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * 5 + 1;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new StartVulnerable(i + 1));
         }
      }));
      result.addAll(GenUtils.bChain("Survive", 2, new ModMaker() {
         @Override
         public List<Global> make(int level) {
            switch (level) {
               case 0:
                  return Arrays.asList(new GlobalHeroes(new IncomingEffBonus(1, EffType.Heal)), new GlobalHeroes(new EmptyMaxHp(2)));
               case 1:
                  return Arrays.asList(new GlobalHeroes(new IncomingEffBonus(1, EffType.Heal, EffType.Shield)), new GlobalHeroes(new EmptyMaxHp(4)));
               default:
                  return null;
            }
         }
      }));
      result.addAll(GenUtils.bChain("Perceptive", new TierMakerAsc(7), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalItemQuantity((int)Math.pow(i + 1, 1.35F));
         }
      }));
      result.addAll(GenUtils.bChain("Versatile", new TierMakerAsc(7), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalLevelupQuantity((int)Math.pow(i + 1, 1.35F));
         }
      }));
      result.addAll(GenUtils.bChain("Boss Smash", 3, new ModMaker() {
         @Override
         public List<Global> make(int level) {
            int bonus;
            switch (level) {
               case 0:
                  bonus = 1;
                  break;
               case 1:
                  bonus = 3;
                  break;
               case 2:
                  bonus = 99;
                  break;
               default:
                  bonus = -1;
            }

            return Arrays.asList(new GlobalLevelRequirement(new LevelRequirementBoss(), new GlobalHeroes(new AffectSides(new FlatBonus(bonus)))));
         }
      }));
      result.addAll(GenUtils.bChain("Hero Regen", 3, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return index * 4 + 6;
         }
      }, new ModMaker() {
         @Override
         public List<Global> make(int level) {
            return Arrays.asList(new GlobalHeroes(new StartRegenned(level + 1)));
         }
      }));
      return result;
   }

   private static List<Modifier> makePerBoss() {
      List<Modifier> result = new ArrayList<>();
      result.add(new Modifier(6.0F, "Reroll per boss", new PerDefeatedBossGlobal(new GlobalBonusRerolls(1))));
      result.add(
         new Modifier(
            10.0F, "Absorb bosses", new PerDefeatedBossGlobal(new GlobalHeroes(new AffectSides(new FlatBonus(1)))), GlobalRarity.fromRarity(Rarity.TENTH)
         )
      );
      result.add(
         new Modifier(
            ModTierUtils.startWithMana(1.9F),
            "Mana per boss",
            new GlobalTurnRequirement(new TurnRequirementFirst(), new PerDefeatedBossGlobal(new GlobalStartTurnEff(new EffBill().mana(1).bEff()))),
            GlobalRarity.fromRarity(Rarity.TENTH)
         )
      );
      return result;
   }

   public static List<Modifier> makeRerolls() {
      return Arrays.asList(new Modifier(3.0F, "Extra Reroll", new GlobalBonusRerolls(1)), new Modifier(6.0F, "2 Extra Rerolls", new GlobalBonusRerolls(2)));
   }

   public static List<Modifier> makeMegaLevelup() {
      int[] by = new int[]{1, 3};
      String[] pref = new String[]{"Level up", "Ascend"};
      List<Modifier> result = new ArrayList<>();

      for (int i = 0; i < by.length; i++) {
         String prefix = pref[i];
         int nl = by[i];
         LevelupHero lu = new LevelupHero(nl);
         result.add(new Modifier(TierUtils.levelupHeroChoosable(1, 1 + nl) * 5.0F, prefix, new GlobalChangeHeroAll(lu)));
      }

      return result;
   }

   public static List<Modifier> makeAntiMortal() {
      List<Modifier> ls = GenUtils.bChain("Divine", 4, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return Tann.niceTerp(index + 1, 4.0F, 39.0F, 2.45F) + 1.0F;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalHeroes(new SidesMin(i + 1));
         }
      });

      for (Modifier l : ls) {
         l.rarity(Rarity.TWENTIETH);
      }

      return ls;
   }

   public static List<Modifier> makeRewards() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(
         Arrays.asList(
            makeBonusReward(3, true),
            makeBonusReward(7, true),
            makeBonusReward(17, true),
            makeBonusReward(8, false),
            makeBonusReward(12, false),
            makeBonusReward(16, false)
         )
      );
      return result;
   }

   private static Modifier makeBonusReward(int level, boolean levelup) {
      float pw = 2.0F;
      float ratio = CurseDistribution.getMultLevelRange(level, 20);
      if (levelup) {
         pw = TierUtils.levelupHeroChoosable(2) * ratio;
      } else {
         pw = TierUtils.itemModTier(level / 2) * ratio;
      }

      String pref = "Level " + level + " ";
      String n = pref + (levelup ? "levelup" : "loot");
      return new Modifier(
         pw,
         n,
         new GlobalLevelRequirement(
            new LevelRequirementRange(level), new GlobalAddPhase((PhaseGenerator)(levelup ? new PhaseGeneratorLevelup() : new PhaseGeneratorStandardLoot()))
         )
      );
   }
}
