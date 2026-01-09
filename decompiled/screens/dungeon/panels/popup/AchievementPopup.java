package com.tann.dice.screens.dungeon.panels.popup;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class AchievementPopup {
   public static Group make(Achievement achievement) {
      Actor hack = new Actor();
      int minWidth = 50;
      hack.setSize(minWidth, 0.0F);
      return new Pixl(3, 2).border(Colours.yellow).actor(hack).row(0).text(achievement.getName()).row().actor(achievement.getImage()).pix();
   }
}
