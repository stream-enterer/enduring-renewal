package com.tann.dice.screens.dungeon.panels.combatEffects.ice;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.PixelParticle;
import com.tann.dice.util.Tann;

public class FrostActor extends CombatEffectActor {
   private static final float ALPHA = 0.7F;
   private static final float FADE_IN = 0.1F;
   private static final float HOLD = 0.1F;
   private static final float PARTICLE_SPAWN = 0.1F;
   private static final float PARTICLE_LIFE = 0.4F;
   private static final int SHATTER_PARTICLES = 40;
   EntPanelCombat targetPanel;

   public FrostActor(Ent target) {
      this.targetPanel = target.getEntPanel();
   }

   @Override
   protected void start(FightLog fightLog) {
      this.setSize(20.0F, 10.0F);
      Vector2 pos = Tann.getAbsoluteCoordinates(this.targetPanel).cpy();
      DungeonScreen.get().addActor(this);
      this.setPosition(
         pos.x + this.targetPanel.getWidth() / 2.0F - this.getWidth() / 2.0F, pos.y + this.targetPanel.getHeight() / 2.0F - this.getHeight() / 2.0F
      );
      Color col = Colours.shiftedTowards(Colours.light, Colours.blue, 0.5F);
      this.setColor(Colours.withAlpha(col, 0.0F));
      this.addAction(Actions.sequence(Actions.alpha(0.7F, 0.1F), Actions.delay(0.1F), Actions.run(new Runnable() {
         @Override
         public void run() {
            FrostActor.this.shatter();
         }
      }), Tann.fadeAndRemove(0.1F)));
   }

   private void shatter() {
      Sounds.playSound(Sounds.iceExplode);

      for (int i = 0; i < 40; i++) {
         PixelParticle pixelParticle = new PixelParticle(Tann.half() ? Colours.light : Colours.blue);
         Tann.setPosition(pixelParticle, Tann.getAbsoluteCoordinates(this).add(Tann.random(this.getWidth()), Tann.random(this.getHeight())));
         pixelParticle.setScale(2.0F);
         DungeonScreen.get().addActor(pixelParticle);
         pixelParticle.addAction(
            Actions.sequence(
               Actions.alpha(0.0F),
               Actions.delay(i / 40.0F * 0.1F),
               Actions.alpha(0.7F),
               Actions.parallel(Tann.moveBy(Tann.randomRadial(16.0F), 0.4F, Interpolation.pow2Out), Actions.scaleTo(1.0F, 1.0F, 0.4F), Tann.fadeAndRemove(0.4F))
            )
         );
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Images.icePatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.2F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.5F;
   }
}
