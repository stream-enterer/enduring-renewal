package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FXSingularity extends FXContainer {
   public FXSingularity(Actor group) {
      super(group);
      this.loadShader(ShaderFolder.singularity);
      this.setColor(1.0F, 1.0F, 1.0F, 0.0F);
      this.setScale(0.0F);
      float yRatio = 0.7F;
      this.addAction(
         Actions.sequence(
            Actions.scaleTo(0.0F, 1.0F, this.getDuration() * yRatio), Actions.alpha(1.0F, this.getDuration() * (1.0F - yRatio)), Actions.removeActor()
         )
      );
   }

   public void draw(Batch batch, float parentAlpha) {
      ShaderProgram previous = batch.getShader();
      batch.setShader(this.program);
      this.group.setPosition(this.originalPosition.x, this.originalPosition.y);
      this.program.setUniformf("u_random", this.random);
      this.program.setUniformf("u_bounds", this.originalPosition.x, this.originalPosition.y, this.group.getWidth(), this.group.getHeight());
      this.program.setUniformf("u_alpha", this.getColor().a);
      this.program.setUniformf("u_scaleX", this.getScaleX());
      this.program.setUniformf("u_scaleY", this.getScaleY());
      this.group.draw(batch, 1.0F);
      batch.setShader(previous);
      super.draw(batch, parentAlpha);
   }

   @Override
   public float getDuration() {
      return 0.5F;
   }
}
