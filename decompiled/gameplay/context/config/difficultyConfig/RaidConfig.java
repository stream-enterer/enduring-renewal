package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementAllButFirst;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RaidConfig extends DifficultyConfig {
   public RaidConfig(Difficulty difficulty) {
      super(Mode.RAID, difficulty);
   }

   public RaidConfig(String serial) {
      this(Difficulty.valueOf(serial));
   }

   public static List<ContextConfig> make() {
      List<ContextConfig> configs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         configs.add(new RaidConfig(d));
      }

      return configs;
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      Collection<Global> result = super.getSpecificModeAddPhases();
      result.addAll(Arrays.asList(new GlobalLevelRequirement(new LevelRequirementAllButFirst(), new GlobalAddPhase(new PhaseGeneratorLevelup()))));
      return result;
   }

   @Override
   public List<Modifier> getStartingModifiers() {
      return Arrays.asList(ModifierLib.byName("Double Monsters"));
   }

   @Override
   protected boolean offerStandardRewards() {
      return false;
   }

   @Override
   protected boolean offerChanceEvents() {
      return false;
   }

   @Override
   public Party getStartingParty(PartyLayoutType chosen, AntiCheeseRerollInfo info) {
      List<Hero> heroes = new ArrayList<>();

      for (int i = 0; i < 2; i++) {
         for (HeroCol heroCol : chosen.getColsInstance()) {
            heroes.add(HeroTypeUtils.getRandom(heroCol, 1).makeEnt());
         }
      }

      return new Party(heroes);
   }
}
