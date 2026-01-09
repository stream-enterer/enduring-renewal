package com.tann.dice.screens.dungeon.panels.combatEffects.slice;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import java.util.List;

public class Slice extends CombatEffectActor {
   public static final float FADE_DURATION = 0.2F;
   float time;
   float sliceDuration;
   Eff eff;
   Ent target;
   TextureRegion image;
   int numScars;
   Vector2 xRange;
   Vector2 yRange;
   Color[] cols;
   Targetable targetable;
   boolean player;

   public Slice(Eff eff, Ent target, TextureRegion image, int numScars, float sliceDuration, Targetable targetable, Color... colors) {
      this.eff = eff;
      this.target = target;
      this.image = image;
      this.numScars = numScars;
      this.sliceDuration = sliceDuration;
      this.cols = colors;
      this.targetable = targetable;
      Ent source = targetable.getSource();
      this.player = source == null || source.isPlayer();
   }

   @Override
   protected void start(FightLog fightLog) {
      this.realStart(fightLog);
   }

   private void realStart(FightLog fightLog) {
      Sounds.playSound(Sounds.slice);
      List<EntState> actualTargets = fightLog.getSnapshot(FightLog.Temporality.Visual).getActualTargets(this.target, this.eff, this.targetable.getSource());
      this.xRange = new Vector2(1000.0F, 0.0F);
      this.yRange = new Vector2(1000.0F, 0.0F);

      for (EntState es : actualTargets) {
         EntPanelCombat panel = es.getEnt().getEntPanel();
         Vector2 pos = Tann.getAbsoluteCoordinates(panel);
         float l = pos.x;
         float r = pos.x + panel.getWidth();
         float b = pos.y;
         float t = pos.y + panel.getHeight();
         if (l < this.xRange.x) {
            this.xRange.x = l;
         }

         if (r > this.xRange.y) {
            this.xRange.y = r;
         }

         if (b < this.yRange.x) {
            this.yRange.x = b;
         }

         if (t > this.yRange.y) {
            this.yRange.y = t;
         }
      }

      DungeonScreen.get().addActor(this);
   }

   @Override
   protected float getImpactDurationInternal() {
      return this.sliceDuration / 2.0F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return this.sliceDuration / 2.0F + 0.2F;
   }

   public void act(float delta) {
      this.time += delta;
      if (this.time > this.getImpactDuration() + this.getExtraDurationInternal()) {
         this.remove();
      }

      super.act(delta);
   }

   public void draw(Batch batch, float parentAlpha) {
      float sliceRatio = Interpolation.pow2Out.apply(Math.min(1.0F, this.time / this.sliceDuration));
      float fadeRatio = Math.max(0.0F, (this.time - this.sliceDuration) / 0.2F);
      float x1 = this.xRange.y;
      float y1 = this.yRange.y;
      float x2 = this.xRange.y - (this.xRange.y - this.xRange.x) * sliceRatio;
      float y2 = this.yRange.y - (this.yRange.y - this.yRange.x) * sliceRatio;
      if (this.player) {
         x1 = this.xRange.x;
         x2 = this.xRange.x - (this.xRange.x - this.xRange.y) * sliceRatio;
      }

      for (int i = 0; i < this.numScars; i++) {
         float add = (i - (this.numScars - 1) / 2.0F) * 3.0F;
         double angle = Math.atan2(y2 - y1, x2 - x1);
         double addX = Math.cos(++angle) * add;
         float drawX1 = (float)(x1 + addX);
         float drawX2 = (float)(x2 + addX);
         double addY = Math.sin(angle) * add;
         float drawY1 = (float)(y1 + addY);
         float drawY2 = (float)(y2 + addY);

         for (int j = 0; j < this.cols.length; j++) {
            batch.setColor(Colours.withAlpha(this.cols[j], 1.0F - fadeRatio));
            Draw.drawLine(batch, drawX1 + j, drawY1, drawX2 + j, drawY2, 1.0F);
         }
      }

      super.draw(batch, parentAlpha);
   }
}
