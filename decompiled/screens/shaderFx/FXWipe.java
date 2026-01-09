package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FXWipe extends FXContainer {
   final Vector2 direction;

   public FXWipe(Actor actor, Vector2 direction) {
      super(actor);
      this.loadShader(ShaderFolder.wipe);
      this.setColor(1.0F, 1.0F, 1.0F, 0.0F);
      this.addAction(Actions.sequence(Actions.delay(0.0F), Actions.alpha(1.0F, 0.4F, Interpolation.pow2InInverse), Actions.removeActor()));
      this.direction = direction;
   }

   @Override
   public float getDuration() {
      return 0.4F;
   }

   public void draw(Batch batch, float parentAlpha) {
      ShaderProgram previous = batch.getShader();
      batch.setShader(this.program);
      this.group.setPosition(this.originalPosition.x, this.originalPosition.y);
      this.program.setUniformf("u_direction", this.direction);
      this.program.setUniformf("u_alpha", this.getColor().a);
      this.program.setUniformf("u_bounds", this.originalPosition.x, this.originalPosition.y, this.group.getWidth(), this.group.getHeight());
      this.group.draw(batch, 1.0F);
      batch.setShader(previous);
   }
}
