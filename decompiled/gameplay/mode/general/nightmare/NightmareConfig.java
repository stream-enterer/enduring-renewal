package com.tann.dice.gameplay.mode.general.nightmare;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.save.RunHistory;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorStandardLoot;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementChance;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementMod;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NightmareConfig extends ContextConfig {
   List<Modifier> originals = new ArrayList<>();
   private RunHistory runHistorySource;
   static Zone ZONE = Zone.Nightmare;

   public NightmareConfig() {
      super(Mode.NIGHTMARE);
   }

   public NightmareConfig(String serial) {
      this(ModifierUtils.deserialiseList(Arrays.asList(serial.split("@5"))));
   }

   @Override
   public String serialise() {
      return Tann.commaList(ModifierLib.serialiseToStringList(this.originals), "@5", "@5");
   }

   public NightmareConfig(List<Modifier> original) {
      super(Mode.NIGHTMARE);
      this.originals = original;
   }

   @Override
   public boolean canRestart() {
      return false;
   }

   public NightmareConfig(RunHistory rh) {
      this(rh.getModifiers());
      this.runHistorySource = rh;
   }

   @Override
   public Party getStartingParty(PartyLayoutType chosen, AntiCheeseRerollInfo info) {
      if (this.runHistorySource == null) {
         TannLog.error("Unable to get starting party for nightmare without runhistory");
         return new Party(Arrays.asList(HeroTypeLib.byName("sdiofgjh").makeEnt()));
      } else {
         return new Party(this.runHistorySource.getPartyData());
      }
   }

   @Override
   public List<Modifier> getStartingModifiers() {
      return this.originals;
   }

   @Override
   public int getLevelOffset() {
      return 20;
   }

   @Override
   public List<TP<Zone, Integer>> getDefaultLevelTypes() {
      List<TP<Zone, Integer>> base = new ArrayList<>(new ClassicConfig(Difficulty.Normal).getDefaultLevelTypes());
      base.add(new TP<>(ZONE, NightmareMode.EXTRA_LEVELS));
      return base;
   }

   private int getGameplayLevel(int levelNumber) {
      return levelNumber;
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
   public Collection<Global> getSpecificModeAddPhases() {
      return Arrays.asList(
         new GlobalAddPhase(new PhaseGeneratorStandardLoot()),
         new GlobalLevelRequirement(new LevelRequirementMod(5, 3), new GlobalAddPhase(new PhaseGeneratorLevelup())),
         new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))),
         new GlobalLevelRequirement(new LevelRequirementChance(0.01F), new GlobalAddPhase(new PhaseGeneratorHardcoded(new MessagePhase("[red][sin][b]doom"))))
      );
   }

   @Override
   public boolean skipFirstPartyInit() {
      return true;
   }
}
