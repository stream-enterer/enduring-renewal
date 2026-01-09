package com.tann.dice.screens.dungeon.panels.combatEffects.cross;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class CrossActor extends CombatEffectActor {
   Ent target;

   public CrossActor(Ent target) {
      this.target = target;
      this.setSize(target.getEntPanel().getWidth(), target.getEntPanel().getHeight());
   }

   @Override
   protected void start(FightLog fightLog) {
      this.target.getEntPanel().addActor(this);
      this.setScale(0.0F, 0.0F);
      this.setColor(Colours.red);
      this.addAction(
         Actions.sequence(
            new Action[]{
               Actions.run(new Runnable() {
                  @Override
                  public void run() {
                     Sounds.playSound(Sounds.slice);
                  }
               }),
               Actions.scaleTo(1.0F, 0.0F, this.getImpactDuration() / 2.0F, Interpolation.pow2Out),
               Actions.run(new Runnable() {
                  @Override
                  public void run() {
                     Sounds.playSound(Sounds.slice);
                  }
               }),
               Actions.scaleTo(1.0F, 1.0F, this.getImpactDuration() / 2.0F, Interpolation.pow2Out),
               Actions.fadeOut(this.getExtraDurationInternal()),
               Actions.removeActor()
            }
         )
      );
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.5F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.12F;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      float line1 = this.getScaleX();
      float line2 = this.getScaleY();
      Draw.drawLine(
         batch, this.getX(), this.getY() + this.getHeight(), this.getX() + this.getWidth() * line2, this.getY() + this.getHeight() * (1.0F - line2), 1.5F
      );
      Draw.drawLine(
         batch,
         this.getX() + this.getWidth(),
         this.getY() + this.getHeight(),
         this.getX() + this.getWidth() * (1.0F - line1),
         this.getY() + this.getHeight() * (1.0F - line1),
         1.5F
      );
      super.draw(batch, parentAlpha);
   }
}
