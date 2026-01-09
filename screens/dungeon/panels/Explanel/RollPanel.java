package com.tann.dice.screens.dungeon.panels.Explanel;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.util.Pixl;

public class RollPanel {
   public static Group make(Ent de) {
      NetPanel np = new NetPanel(de, false);
      Pixl main = new Pixl(3, 3).border(de.getColour());
      main.actor(np);
      EntSide side = de.getDie().getCurrentSide();
      Group extras = null;
      if (side != null) {
         Explanel explanel = new Explanel(side, de);
         extras = explanel.treatExtrasAsMain();
         main.actor(explanel);
      }

      Pixl full = new Pixl(0).actor(main.pix());
      if (extras != null) {
         full.row().actor(extras);
      }

      return full.pix();
   }
}
