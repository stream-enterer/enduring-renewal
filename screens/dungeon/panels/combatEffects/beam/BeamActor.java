package com.tann.dice.screens.dungeon.panels.combatEffects.beam;

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
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.PixelParticle;
import com.tann.dice.util.RainbowAction;
import com.tann.dice.util.Tann;

public class BeamActor extends CombatEffectActor {
   private static final float IN = 0.05F;
   private static final float HOLD = 0.15F;
   private static final float OUT = 0.2F;
   private static final float START_SIZE = 2.0F;
   private static final float MAX_SIZE = 8.9F;
   private static final float PARTICLE_SPAWN_TIME = 0.002F;
   final Ent source;
   final Ent target;
   float particleTimer;
   Vector2 start;
   Vector2 end;
   final Color col;

   public BeamActor(Ent source, Ent target, Color col) {
      this.source = source;
      this.target = target;
      this.col = col;
   }

   @Override
   protected void start(FightLog fightLog) {
      Sounds.playSound(Sounds.beam);
      DungeonScreen.get().addActor(this);
      if (this.source == null) {
         this.start = new Vector2(-20.0F, com.tann.dice.Main.height / 2);
      } else {
         this.start = Tann.getAbsoluteCoordinates(this.source.getEntPanel(), Tann.TannPosition.Left).cpy();
      }

      EntPanelCombat targetPanel = this.target.getEntPanel();
      this.end = Tann.getAbsoluteCoordinates(targetPanel, Tann.TannPosition.Center).add(0.0F, -4.45F).cpy();
      this.setColor(this.col);
      this.setScaleX(2.0F);
      this.addAction(
         Actions.sequence(
            Actions.parallel(
               Actions.sequence(
                  Actions.scaleTo(8.9F, 0.0F, 0.05F, Interpolation.pow2In), Actions.delay(0.15F), Actions.scaleTo(0.0F, 0.0F, 0.2F, Interpolation.pow2Out)
               ),
               new RainbowAction(0.4F, Colours.purple, this.col, Colours.purple)
            ),
            Actions.removeActor()
         )
      );
   }

   public void act(float delta) {
      super.act(delta);
      this.particleTimer -= delta;
      float moveAmount = this.getScaleX() * 2.0F;
      float MOVE_TIME = 0.3F;
      if (this.getScaleX() > 2.0F) {
         while (this.particleTimer < 0.0F) {
            this.particleTimer += 0.002F;
            PixelParticle pixelParticle = new PixelParticle(Tann.half() ? Colours.light : this.col);
            DungeonScreen.get().addActor(pixelParticle);
            float dist = Tann.random();
            pixelParticle.setPosition(this.start.x + (this.end.x - this.start.x) * dist, this.start.y + (this.end.y - this.start.y) * dist);
            pixelParticle.addAction(
               Actions.parallel(
                  Actions.moveBy(Tann.random(-moveAmount, moveAmount), Tann.random(-moveAmount, moveAmount), MOVE_TIME, Interpolation.pow2Out),
                  Tann.fadeAndRemove(MOVE_TIME)
               )
            );
         }
      }
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.125F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.275F;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.drawLine(batch, this.start, this.end, (int)this.getScaleX());
      super.draw(batch, parentAlpha);
   }
}
