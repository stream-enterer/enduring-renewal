package com.tann.dice.gameplay.mode.debuggy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.misc.BalanceConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import java.util.List;

public class BalanceMode extends Mode {
   public BalanceMode() {
      super("[grey]Level Debug");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"see example levels (warning slow)", "try one with random heroes and items"};
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return BalanceConfig.make();
   }

   @Override
   protected void onStartButtonPress(ContextConfig cc) {
      Sounds.playSound(Sounds.pip);
      Actor a = DebugUtilsUseful.makeFreqGroup(Difficulty.Unfair, 20, ((BalanceConfig)cc).level);
      com.tann.dice.Main.getCurrentScreen().push(a);
      Tann.center(a);
   }

   @Override
   public Actor makeWinsActor(ContextConfig config) {
      return new Actor();
   }

   @Override
   public String getSaveKey() {
      return "balance";
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
   public boolean disablePartyLayout() {
      return true;
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }
}
