package com.tann.dice.gameplay.phase.endPhase.statsPanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.AlternativePop;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class GameEndStatsPanel extends Group implements AlternativePop {
   public static final int MAX_WIDTH = 164;

   public GameEndStatsPanel(DungeonContext context, boolean victory) {
      Pixl p = new Pixl(2, 3);
      p.actor(GameEndUtils.makeTop(context, victory)).row();
      if (context.getCurrentModifiers().size() > 0) {
         p.actor(GameEndUtils.makeModifiers(context)).row();
      }

      p.actor(GameEndUtils.makeHeroes(context));
      if (!context.skipStats()) {
         p.row().actor(GameEndUtils.makeLeft(context)).actor(GameEndUtils.makeRight(context));
      }

      Tann.become(this, p.pix());
      PartyLayoutType plt = context.getParty().getPLT();
      if (plt != null) {
         Actor a = plt.visualiseTiny();
         this.addActor(a);
         int gap = 2;
         a.setPosition(2.0F, this.getHeight() - a.getHeight() - 2.0F);
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }

   @Override
   public boolean alternativePop() {
      Tann.slideAway(this, Tann.TannPosition.Top, 0, true);
      return true;
   }

   public static Actor makeStatsButton(final DungeonContext dungeonContext, final boolean victory) {
      StandardButton stats = new StandardButton("Stats");
      stats.setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);

            Actor esp;
            try {
               Actor espx = new GameEndStatsPanel(dungeonContext, victory);
               esp = Tann.makeScrollpaneIfNecessary(espx);
            } catch (Exception var3) {
               var3.printStackTrace();
               esp = new TextWriter("err");
            }

            esp.setPosition((int)(com.tann.dice.Main.width / 2 - esp.getWidth() / 2.0F), 0.0F);
            com.tann.dice.Main.getCurrentScreen().push(esp, true, true, false, 0.8F);
            Tann.slideIn(esp, Tann.TannPosition.Top, (int)(com.tann.dice.Main.height / 2 - esp.getHeight() / 2.0F), 0.3F);
         }
      });
      return stats;
   }
}
