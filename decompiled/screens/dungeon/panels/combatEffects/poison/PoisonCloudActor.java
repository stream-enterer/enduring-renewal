package com.tann.dice.screens.dungeon.panels.combatEffects.poison;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Noise;
import com.tann.dice.util.Tann;
import java.util.List;

public class PoisonCloudActor extends CombatEffectActor {
   List<EntState> targets;
   private static final float START_DURATION = 0.8F;
   private static final float IMPACT_RATIO = 0.2F;

   public PoisonCloudActor(List<EntState> targets) {
      this.targets = targets;
   }

   @Override
   protected void start(FightLog fightLog) {
      Sounds.playSound(Sounds.poison);
      this.setY(getBotMost(this.targets));
      this.setHeight(getTopMost(this.targets) - this.getY());
      EntPanelCombat first = this.targets.get(0).getEnt().getEntPanel();
      float widthStart = 0.0F;
      this.setX(Tann.getAbsoluteCoordinates(first).x + first.getWidth() * widthStart);
      this.setWidth(first.getWidth() * (1.0F - widthStart));
      DungeonScreen.get().addActor(this);
      this.addAction(Actions.sequence(Actions.fadeOut(0.8F), Actions.removeActor()));
      int totalSkulls = this.targets.size() * 2;

      for (int i = 0; i < totalSkulls; i++) {
         float ratio = 0.64000005F / totalSkulls * i;
         this.addAction(Actions.delay(ratio, Actions.run(new Runnable() {
            @Override
            public void run() {
               PoisonCloudActor.this.addSkull();
            }
         })));
      }
   }

   private void addSkull() {
      ImageActor ia = new ImageActor(Images.combatEffectPoisonSkull);
      ia.setColor(Colours.withAlpha(Colours.green, 1.0F));
      DungeonScreen.get().addActor(ia);
      ia.setPosition(this.getX() + Tann.random(this.getWidth() - ia.getWidth()), this.getY() + Tann.random(this.getHeight() - ia.getHeight()));
      float fadeDuration = 0.64000005F;
      int moveAmount = 4;
      ia.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0.0F, moveAmount, fadeDuration), Actions.fadeOut(fadeDuration)), Actions.removeActor()));
      this.toFront();
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.16000001F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.64000005F;
   }

   public void draw(Batch batch, float parentAlpha) {
      int pixelSize = 1;
      float freq = 0.08F;
      float upFreq = 0.5F;
      float halfHeight = this.getHeight() / 2.0F;
      float halfWidth = this.getWidth() / 2.0F;
      float distRatio = (halfWidth + halfHeight) / 2.0F;

      for (int x = 0; x < this.getWidth(); x += pixelSize) {
         for (int y = 0; y < this.getHeight(); y += pixelSize) {
            float perlin = (float)Noise.noise((double)(x * freq), (double)(y * freq), (double)(com.tann.dice.Main.secs * upFreq), 1);
            float perlinRatio = (perlin + 1.0F) / 2.0F;
            float xDist = halfWidth - x;
            float yDist = halfHeight - y;
            float dist = (float)(Math.sqrt(xDist * xDist + yDist * yDist) / distRatio);
            float alpha = (float)(perlinRatio * Math.pow(1.0F - dist, 1.5));
            alpha = Interpolation.pow3Out.apply(alpha);
            alpha = alpha > 0.3 ? 0.8F : 0.0F;
            alpha *= this.getColor().a;
            Color sourceCol = perlinRatio > 0.6 ? Colours.purple : Colours.green;
            batch.setColor(Colours.withAlpha(sourceCol, alpha));
            Draw.fillRectangle(batch, this.getX() + x, this.getY() + y, pixelSize, pixelSize);
         }
      }

      super.draw(batch, parentAlpha);
   }
}
