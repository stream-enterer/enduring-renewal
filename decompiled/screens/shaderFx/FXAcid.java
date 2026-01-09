package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FXAcid extends FXContainer {
   public FXAcid(Actor group) {
      super(group);
      this.loadShader(ShaderFolder.acid);
      this.setColor(1.0F, 1.0F, 1.0F, 0.0F);
      this.addAction(Actions.sequence(Actions.alpha(1.0F, this.getDuration()), Actions.removeActor()));
   }

   @Override
   public float getDuration() {
      return 0.6F;
   }

   public void draw(Batch batch, float parentAlpha) {
      ShaderProgram previous = batch.getShader();
      batch.setShader(this.program);
      this.group.setPosition(this.originalPosition.x, this.originalPosition.y);
      this.setupNoise(this.program);
      this.program.setUniformf("u_random", this.random);
      this.program.setUniformf("u_alpha", this.getColor().a);
      this.group.draw(batch, 1.0F);
      batch.setShader(previous);
   }
}
