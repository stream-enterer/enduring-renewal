package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalAllowDuplicateHeroes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChoosePartyConfig extends DifficultyConfig {
   final List<HeroType> startingHeroes;

   public ChoosePartyConfig(Difficulty difficulty, List<HeroType> startingHeroes) {
      super(Mode.CHOOSE_PARTY, difficulty);
      this.startingHeroes = startingHeroes;
   }

   public static ChoosePartyConfig fromString(String save) {
      String[] split = save.split(",");
      List<HeroType> start = new ArrayList<>();

      for (int i = 1; i < split.length; i++) {
         start.add(HeroTypeUtils.byName(split[i]));
      }

      return new ChoosePartyConfig(Difficulty.valueOf(split[0]), start);
   }

   @Override
   public String serialise() {
      String s = this.difficulty.name();

      for (HeroType ht : this.startingHeroes) {
         s = s + ",";
         s = s + ht.getName(false);
      }

      return s;
   }

   public static List<ContextConfig> make(List<HeroType> types) {
      List<ContextConfig> contextConfigs = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         contextConfigs.add(new ChoosePartyConfig(d, types));
      }

      return contextConfigs;
   }

   @Override
   protected List<Global> getSpecificDifficultyModeGlobals() {
      return Arrays.asList(new GlobalAllowDuplicateHeroes());
   }

   @Override
   public Party getStartingParty(PartyLayoutType chosen, AntiCheeseRerollInfo info) {
      List<Hero> heroes = new ArrayList<>();

      for (HeroType ht : this.startingHeroes) {
         heroes.add(ht.makeEnt());
      }

      return new Party(heroes);
   }

   @Override
   public boolean antiCheeseHeroes() {
      return false;
   }
}
