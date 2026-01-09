package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.util.Tann;

public class FXCut extends FXContainer {
   float total;
   final float startHeight;
   final float endHeight;
   final int dir;
   Vector2 leftRightCutRatio = new Vector2(0.5F, 0.5F);

   public FXCut(Actor actor, int dir, float cutTime, float separateTime, float separateDist, float startHeight, float endHeight) {
      super(actor);
      this.setCutRatio(startHeight, endHeight);
      this.loadShader(ShaderFolder.cut);
      this.total = cutTime + separateTime;
      this.dir = dir;
      this.startHeight = startHeight;
      this.endHeight = endHeight;
      this.animate(cutTime, separateTime, separateDist);
   }

   private void randomiseCutRatio() {
      float rand = Tann.random();
      this.setCutRatio(rand, 1.0F - rand);
   }

   public void setCutRatio(float left, float right) {
      this.leftRightCutRatio.set(left, right);
   }

   public void draw(Batch batch, float parentAlpha) {
      ShaderProgram previous = batch.getShader();
      batch.setShader(this.program);

      for (int i = 0; i < 2; i++) {
         int drawX = (int)this.originalPosition.x;
         int drawY = (int)(this.originalPosition.y + this.getHeight() * (i * 2 - 1) * this.dir);
         int h = (int)this.group.getHeight();
         int w = (int)this.group.getWidth();
         float cutDiff = this.leftRightCutRatio.y - this.leftRightCutRatio.x;
         this.program.setUniformi("u_cutSide", i * 2 - 1);
         this.program.setUniformi("u_disappear", (int)this.getScaleY());
         this.program.setUniformf("u_cutAlpha", this.getWidth());
         this.program.setUniformf("u_intersect", this.dir == 1 ? drawX : drawX + w, drawY + this.leftRightCutRatio.x * h, this.dir == 1 ? w : -w, cutDiff * h);
         this.group.setPosition(drawX, drawY);
         this.group.draw(batch, 1.0F);
         batch.flush();
      }

      batch.setShader(previous);
   }

   private void animate(float cutTime, float separateTime, float separateDist) {
      this.clearActions();
      this.setScaleY(0.0F);
      this.setSize(0.0F, 0.0F);
      this.addAction(
         Actions.sequence(
            Actions.sizeTo(1.0F, 0.0F, cutTime, Interpolation.pow2Out),
            Actions.parallel(
               Actions.sizeTo(1.0F, separateDist, separateTime, Interpolation.pow2Out),
               Actions.scaleTo(0.0F, this.group.getHeight(), separateTime, Interpolation.pow2In)
            ),
            Actions.removeActor()
         )
      );
   }

   @Override
   public float getDuration() {
      return this.total;
   }
}
