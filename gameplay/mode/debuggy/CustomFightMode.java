package com.tann.dice.gameplay.mode.debuggy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.general.nightmare.NightmareConfig;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorHardcoded;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.resolver.MonsterTypeResolver;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CustomFightMode extends Mode {
   List<MonsterType> currentMonsterList = new ArrayList<>();
   final Group bvHolder = Tann.makeGroup(100, 130);
   Actor bv = null;

   public CustomFightMode() {
      super("Fight");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"'Reverse level generation'", "If you think it seems wrong, it probably is. Remember boss fights are harder"};
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      return this.makeSelectorActor();
   }

   private Actor makeSelectorActor() {
      if (this.currentMonsterList.isEmpty()) {
         this.addMonster(MonsterTypeLib.randomWithRarity());
      } else {
         this.relayoutBV();
      }

      return this.bvHolder;
   }

   private void removeMonster(MonsterType mt) {
      this.currentMonsterList.remove(mt);
      if (this.refreshScreen()) {
         Sounds.playSound(Sounds.pop);
      }
   }

   private void addMonster(MonsterType monsterType) {
      this.currentMonsterList.add(monsterType);
      if (this.refreshScreen()) {
         Sounds.playSound(Sounds.pip);
      }
   }

   private boolean refreshScreen() {
      this.relayoutBV();
      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (s == null) {
         return false;
      } else {
         s.popAllMedium();
         return true;
      }
   }

   private void relayoutBV() {
      this.bvHolder.clear();
      this.bvHolder.addActor(this.bv = this.balanceView(this.currentMonsterList));
      Tann.center(this.bv);
   }

   private Actor balanceView(final List<MonsterType> asList) {
      List<Integer> valids = DebugUtilsUseful.getValidLevels(asList);
      int lowest = DebugUtilsUseful.getLowest(asList);
      final int runToFightWith = valids.isEmpty() ? lowest : Tann.middle(valids);
      Pixl p = new Pixl(3, 3).border(Colours.grey);
      final MonsterTypeResolver mtr = new MonsterTypeResolver() {
         public void resolve(MonsterType monsterType) {
            CustomFightMode.this.addMonster(monsterType);
         }
      };
      StandardButton sb = new StandardButton("+");
      sb.setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);
            mtr.activate();
         }
      });
      p.actor(sb);
      StandardButton sbx = new StandardButton("+rng");
      sbx.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomFightMode.this.addMonster(MonsterTypeLib.randomWithRarity());
         }
      });
      p.actor(sbx);
      StandardButton sbxx = new StandardButton("fight");
      sbxx.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               if (!CustomFightMode.this.currentMonsterList.isEmpty() && runToFightWith <= 30) {
                  Party p = Party.generate(runToFightWith - 1);
                  DungeonContext dc = new DungeonContext(
                     new CustomFightMode.CustomFightConfig(), p, runToFightWith, new Level(asList.toArray(new MonsterType[0]))
                  );
                  p.onFirstInit(dc);
                  GameStart.start(dc);
               } else {
                  Sounds.playSound(Sounds.error);
               }
            }
         }
      );
      p.actor(sbxx);
      p.row(5);

      for (final MonsterType mt : asList) {
         Actor a = new ImageActor(mt.portrait);
         p.actor(a);
         a.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               CustomFightMode.this.removeMonster(mt);
               return true;
            }
         });
      }

      if (!this.currentMonsterList.isEmpty()) {
         p.row(5);
         p.text("valid for: ").row();

         for (int i = 0; i < valids.size(); i++) {
            p.text("" + valids.get(i));
         }

         if (valids.size() == 0) {
            p.text("[blue]" + lowest + "");
         }
      }

      return p.pix();
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList();
   }

   @Override
   public String getSaveKey() {
      return "cfm";
   }

   @Override
   public boolean skipStats() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }

   public static class CustomFightConfig extends ContextConfig {
      public CustomFightConfig() {
         super(Mode.CUSTOM_FIGHT);
      }

      @Override
      public Collection<Global> getSpecificModeAddPhases() {
         return Arrays.asList(new GlobalLevelRequirement(new LevelRequirementFirst(), new GlobalAddPhase(new PhaseGeneratorHardcoded(new LevelEndPhase(true)))));
      }

      @Override
      public DungeonContext makeContext(AntiCheeseRerollInfo original) {
         return PickMode.makeRestartContext();
      }

      @Override
      public int getTotalLength() {
         return 1;
      }

      @Override
      public List<TP<Zone, Integer>> getDefaultLevelTypes() {
         return new NightmareConfig().getDefaultLevelTypes();
      }
   }
}
