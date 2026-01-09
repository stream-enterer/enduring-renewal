package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.resetPhase;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class ResetPhase extends Phase {
   ResetPanel resetPanel;

   @Override
   public void activate() {
      Sounds.playSound(Sounds.deathHero);
      this.resetPanel = new ResetPanel();
      DungeonScreen.get().addActor(this.resetPanel);
      Tann.center(this.resetPanel);
      Tann.slideIn(this.resetPanel, Tann.TannPosition.Top, (int)(com.tann.dice.Main.height / 2 - this.resetPanel.getHeight() / 2.0F));
   }

   @Override
   public void deactivate() {
      Sounds.playSound(Sounds.pop);
      this.getFightLog().getContext().clearForLoop();
      DungeonScreen.get().save();
      Tann.slideAway(this.resetPanel, Tann.TannPosition.Top, true);
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return new StandardButton("[purple]The end?", Colours.purple, 53, 20);
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.purple;
   }

   @Override
   public String serialise() {
      return "6";
   }

   @Override
   public boolean canSave() {
      return false;
   }
}
