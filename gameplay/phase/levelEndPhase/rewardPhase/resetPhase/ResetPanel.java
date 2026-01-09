package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.resetPhase;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class ResetPanel extends Group {
   public ResetPanel() {
      Pixl p = new Pixl(4, 4);
      String text = com.tann.dice.Main.t("[purple]You feel weaker.\nAll your items disappear.\nOnly curses remain.\nWill you ever escape this fate?");
      text = "[notranslateall]" + text.replaceAll("\n", "[n]");
      p.text(text);
      p.row();

      for (String s : new String[]{"[red]never"}) {
         StandardButton tb = new StandardButton(s);
         tb.setRunnable(new Runnable() {
            @Override
            public void run() {
               PhaseManager.get().popPhase(ResetPhase.class);
            }
         });
         p.actor(tb);
      }

      Tann.become(this, p.pix());
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }
}
