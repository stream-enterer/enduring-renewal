package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FXAlpha extends FXContainer {
   final float delay;

   public FXAlpha(Actor actor) {
      this(actor, 0.0F);
   }

   public FXAlpha(Actor actor, float delay) {
      super(actor);
      this.delay = delay;
      this.loadShader(ShaderFolder.alpha);
      this.setColor(1.0F, 1.0F, 1.0F, 0.0F);
      this.addAction(Actions.sequence(Actions.delay(delay), Actions.alpha(1.0F, this.getDuration(), Interpolation.pow2Out), Actions.removeActor()));
   }

   private float getFadeDuration() {
      return 0.4F;
   }

   @Override
   public float getDuration() {
      return this.delay + this.getFadeDuration();
   }

   public void draw(Batch batch, float parentAlpha) {
      ShaderProgram previous = batch.getShader();
      batch.setShader(this.program);
      this.group.setPosition(this.originalPosition.x, this.originalPosition.y);
      this.program.setUniformf("u_alpha", this.getColor().a);
      this.program.setUniformf("u_bounds", this.originalPosition.x, this.originalPosition.y, this.group.getWidth(), this.group.getHeight());
      this.group.draw(batch, 1.0F);
      batch.setShader(previous);
   }
}
