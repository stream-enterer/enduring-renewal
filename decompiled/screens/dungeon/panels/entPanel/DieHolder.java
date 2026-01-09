package com.tann.dice.screens.dungeon.panels.entPanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class DieHolder extends Actor {
   Ent ent;

   public DieHolder(Ent ent) {
      this.ent = ent;
      this.setSize(ent.getPixelSize(), ent.getPixelSize());
      this.setColor(ent.getColour());
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.ent.getDie().flatDraw && !this.ent.getState(FightLog.Temporality.Present).isSummonedSoNotAttacking()) {
         EntSide side = this.ent.getDie().getCurrentSide();
         if (side != null) {
            side.draw(batch, this.ent, (int)this.getX(), (int)this.getY(), this.ent.getColour(), this.ent.get2DLapel());
         }

         EntState es = this.ent.getState(this.ent.isPlayer() ? FightLog.Temporality.Present : FightLog.Temporality.Visual);
         Phase current = PhaseManager.get().getPhase();
         if (!es.canUse() && !es.isUsed() || current != null && this.ent.isPlayer() && (es.isUsed() || !current.canRoll() && !current.canTarget())) {
            batch.setColor(Colours.withAlpha(Colours.dark, 0.7F));
            Draw.fillActor(batch, this);
         }
      } else {
         if (this.ent.get2DLapel() != null) {
            batch.setColor(Colours.z_white);
            batch.draw(this.ent.get2DLapel(), (int)this.getX(), (int)this.getY());
         }

         if (this.ent.getDie().flatDraw) {
            EntSide sidex = this.ent.getDie().getCurrentSide();
            if (sidex != null) {
               sidex.draw(batch, this.ent, (int)this.getX(), (int)this.getY(), this.ent.getColour(), this.ent.get2DLapel());
            }

            Draw.fillActor(batch, this, Colours.withAlpha(Colours.dark, 0.4F));
         }
      }

      super.draw(batch, parentAlpha);
   }
}
