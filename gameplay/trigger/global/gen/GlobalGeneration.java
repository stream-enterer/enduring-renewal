package com.tann.dice.gameplay.trigger.global.gen;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterGenerated;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalAllowDeadHeroSpells;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroPos;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.LevelupHero;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartOfCombat;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartTurnEff;
import com.tann.dice.gameplay.trigger.global.eff.GlobalSummonMonsterStartTurn;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuality;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithRandomItem;
import com.tann.dice.gameplay.trigger.global.level.GlobalAddMonster;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllEntitiesRestricted;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllMonstersExcept;
import com.tann.dice.gameplay.trigger.global.linked.GlobalEveryNthDice;
import com.tann.dice.gameplay.trigger.global.linked.GlobalHeroTier;
import com.tann.dice.gameplay.trigger.global.linked.GlobalMulti;
import com.tann.dice.gameplay.trigger.global.linked.GlobalPositional;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSize;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSpecificEntTypes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementRange;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.gameplay.trigger.global.spell.GlobalAbilitiesLimit;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellCostChange;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.gameplay.trigger.personal.util.PersonalGeneration;
import com.tann.dice.util.Tann;
import java.util.Random;

public class GlobalGeneration {
   public static Global random(Random r) {
      int val = r.nextInt(22);
      switch (val) {
         case 0:
            return new GlobalAllEntitiesRestricted(r.nextBoolean(), new GenericStateCondition(ra(StateConditionType.values(), r)), PersonalGeneration.random(r));
         case 1:
            return new GlobalAddMonster(PipeMonsterGenerated.makeMonstExt(r.nextInt(9999)));
         case 2:
            return new GlobalEveryNthDice(r.nextInt(5) + 2, ra(Keyword.values(), r));
         case 3:
            return randomSpell(r);
         case 4:
            return new GlobalLevelRequirement(randomLR(r), new GlobalAddPhase(PhaseGenerator.rpg(r)));
         case 5:
            return new GlobalAllEntities(r.nextBoolean(), new AsIfHasItem(ItemLib.random(r)));
         case 6:
            Global g = random(r);
            if (g.isMultiplable()) {
               return new GlobalMulti(g, r.nextInt(4) + 1);
            }

            return g;
         case 7:
            return new GlobalItemQuality(r.nextInt(20) - 10);
         case 8:
            return new GlobalBonusRerolls(r.nextInt(4));
         case 9:
            return new GlobalStartWithRandomItem(r.nextInt(5), r.nextInt(20) - 4);
         case 10:
            return new GlobalChangeHeroPos(ra(HeroPosition.values(), r), new LevelupHero(3));
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            return randomLinkedPersonal(r, PersonalGeneration.random(r));
         case 16:
            return new GlobalTurnRequirement(randomTR(r), new GlobalEndTurnEff(EffUtils.random(r, false)));
         case 17:
            return new GlobalTurnRequirement(randomTR(r), new GlobalStartTurnEff(EffUtils.random(r, false)));
         case 18:
            return new GlobalStartOfCombat(EffUtils.random(r, false));
         case 19:
            return new GlobalSummonMonsterStartTurn(randomTR(r), PipeMonsterGenerated.makeMonstExt(r.nextInt(9999)));
         case 20:
            Global g = random(r);
            if (g.allTurnsOnly()) {
               return g;
            }

            return new GlobalTurnRequirement(randomTR(r), g);
         case 21:
            Global g = random(r);
            if (g.allLevelsOnly()) {
               return g;
            }

            return new GlobalLevelRequirement(randomLR(r), g);
         default:
            return new GlobalAllowDeadHeroSpells();
      }
   }

   private static Global randomSpell(Random r) {
      int val = r.nextInt(2);
      switch (val) {
         case 0:
            return new GlobalAbilitiesLimit(r.nextInt(6));
         default:
            return new GlobalSpellCostChange(r.nextInt(5), r.nextBoolean() ? null : r.nextInt(4));
      }
   }

   private static Global randomTR(Random r, Global random) {
      return new GlobalTurnRequirement(randomTR(r), random);
   }

   public static TurnRequirement randomTR(Random r) {
      return new TurnRequirementN(r.nextInt(5));
   }

   private static LevelRequirement randomLR(Random r) {
      return new LevelRequirementRange(r.nextInt(5));
   }

   private static Global randomLR(Random r, Global random) {
      return new GlobalLevelRequirement(randomLR(r), random);
   }

   public static Global randomLinkedPersonal(Random r, Personal p) {
      int num = r.nextInt(5);
      switch (num) {
         case 0:
            return new GlobalHeroTier(r.nextInt(4), p);
         case 1:
            return new GlobalSize(Tann.random(EntSize.values(), r), p);
         case 2:
            return new GlobalAllMonstersExcept(MonsterTypeLib.random(r), p);
         case 3:
            return new GlobalPositional(Tann.random(HeroPosition.values(), r), p);
         default:
            return new GlobalSpecificEntTypes(p, randomEntTypes(r));
      }
   }

   private static EntType[] randomEntTypes(Random r) {
      EntType[] result = new EntType[r.nextInt(4) + 1];

      for (int i = 0; i < result.length; i++) {
         result[i] = MonsterTypeLib.random(r);
      }

      return result;
   }

   public static <T> T ra(T[] vals, Random r) {
      return AffectSideEffect.ra(vals, r);
   }
}
