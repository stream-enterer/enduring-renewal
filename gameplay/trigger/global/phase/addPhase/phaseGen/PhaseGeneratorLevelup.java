package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.SimpleChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.LevelupHeroChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.SkipChoosable;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhaseGeneratorLevelup extends PhaseGenerator {
   public static final int MAX_LEVEL = 999;
   static final int BASE_CHOICES = 2;

   @Override
   public List<Phase> generate(DungeonContext dc) {
      int choices = dc.getLevelupOptions(2);
      if (choices < 0) {
         return Arrays.asList(new MessagePhase("Negative levelup options"));
      } else {
         List<TP<Hero, HeroType>> types = getLevelupOptions(dc, choices);
         if (types == null) {
            return Arrays.asList(new MessagePhase("[purple]Error finding a hero to levelup..."));
         } else {
            List<Choosable> options = new ArrayList<>();

            for (int i = 0; i < types.size(); i++) {
               options.add(new LevelupHeroChoosable((HeroType)types.get(i).b));
            }

            if (com.tann.dice.Main.getSettings().hasAttemptedLevel() || options.isEmpty()) {
               options.add(new RandomTieredChoosable(getPartyLevelupToTier(dc.getParty().getHeroes()), 1, ChoosableType.Levelup));
               options.add(new SkipChoosable());
            }

            return Arrays.asList(new SimpleChoicePhase(options));
         }
      }
   }

   public static LevelupHeroChoosable getRandom(DungeonContext dc) {
      List<TP<Hero, HeroType>> luo = getLevelupOptions(dc, 1);
      return new LevelupHeroChoosable((HeroType)luo.get(0).b);
   }

   private static int getPartyLevelupToTier(List<Hero> heroes) {
      int minLevel = 999;

      for (Hero h : heroes) {
         if (h.canLevelUp()) {
            minLevel = Math.min(minLevel, h.getLevel());
         }
      }

      return minLevel + 1;
   }

   public static List<TP<Hero, HeroType>> getLevelupOptions(DungeonContext dungeonContext, int amt) {
      boolean allowDuplicates = false;
      List<Global> globals = dungeonContext.getModifierGlobalsIncludingLinked();

      for (Global gt : globals) {
         allowDuplicates |= gt.allowDuplicateHeroLevelups();
      }

      List<Hero> toLevelUp = new ArrayList<>();
      Party party = dungeonContext.getParty();
      List<Hero> heroes = new ArrayList<>(party.getHeroes());

      for (int i = heroes.size() - 1; i >= 0; i--) {
         if (!heroes.get(i).canLevelUp()) {
            heroes.remove(i);
         }
      }

      List<Hero> potentials = new ArrayList<>();
      int minLevel = getPartyLevelupToTier(heroes) - 1;

      for (Hero h : heroes) {
         if (h.getLevel() == minLevel) {
            potentials.add(h);
         }
      }

      Collections.shuffle(potentials);
      if (potentials.size() == 0) {
         return null;
      } else {
         if (potentials.size() < amt) {
            for (int ix = 0; ix < amt; ix++) {
               toLevelUp.add(Tann.random(potentials));
            }
         } else {
            toLevelUp.addAll(potentials.subList(0, amt));
         }

         final Party p = dungeonContext.getParty();
         Collections.sort(toLevelUp, new Comparator<Hero>() {
            public int compare(Hero o1, Hero o2) {
               return p.colIndex(o1.getHeroType().heroCol) - p.colIndex(o2.getHeroType().heroCol);
            }
         });
         List<HeroType> currentHeroTypes = new ArrayList<>();

         for (Hero hx : heroes) {
            currentHeroTypes.add(hx.getHeroType());
         }

         List<HeroType> tmpSeen = new ArrayList<>(dungeonContext.makeSeenHeroTypes());
         List<TP<Hero, HeroType>> result = new ArrayList<>();
         List<HeroType> currentPlusOffered = new ArrayList<>(currentHeroTypes);
         if (allowDuplicates) {
            currentPlusOffered.clear();
            tmpSeen.clear();
         }

         HeroGenType hgt = PipeHero.getGenType(globals);

         for (Hero hx : toLevelUp) {
            HeroType option = HeroTypeUtils.getOption(hx, hgt, dungeonContext, currentPlusOffered, tmpSeen);
            if (option.isMissingno() && !Tann.contains(HeroCol.basics(), option.heroCol) && hx.getLevel() >= 0 && hx.getLevel() < 9) {
               option = HeroTypeLib.byName(
                  hx.getName(false)
                     + ".hp."
                     + (hx.entType.hp + 4)
                     + ".tier."
                     + (hx.getHeroType().getTier() + 1)
                     + ".n."
                     + cleanName(hx.getName(true, false))
                     + ""
               );
            }

            currentPlusOffered.add(option);
            TP<Hero, HeroType> tt = new TP<>(hx, option);
            result.add(tt);
         }

         return result;
      }
   }

   private static String cleanName(String name) {
      return name.replace(".", "");
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.LEVELUP_REWARD;
   }
}
