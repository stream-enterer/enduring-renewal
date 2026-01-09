package com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile.arrow;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile.SimpleAbstractProjectile;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;

public class ArrowActor extends SimpleAbstractProjectile {
   private static final TextureRegion arrow = ImageUtils.loadExt("combatEffects/projectile/arrow");
   float magnitude;
   public static final float WIGGLE_FREQ = 42.0F;
   public static final float START_MAGNITUDE = 28.0F;
   public static final float MAGNITUDE_DECAY = 82.0F;
   boolean impacted;
   boolean nowDead;
   Vector2 endPos;
   private static Vector2 tmp = new Vector2();

   public ArrowActor(Ent source, Ent target, int damage, boolean nowDead) {
      super(source, target, damage, 0.24F);
      this.nowDead = nowDead;
      this.setTouchable(Touchable.disabled);
   }

   @Override
   protected void internalStart() {
      Sounds.playSound(Sounds.arrowFly, 1.0F, 1.0F);
   }

   @Override
   protected TextureRegion getImage() {
      return arrow;
   }

   @Override
   protected void arrowImpact() {
      if (!this.dodged) {
         if (this.blocked) {
            Sounds.playSound(Sounds.clangs);
         } else {
            Sounds.playSound(Sounds.arrowWobble);
         }
      }

      this.magnitude = 28.0F;
      this.target.getEntPanel().addActor(this);
      DungeonScreen.get().addCleanupActor(this);
      Vector2 onPanelLocation = this.getOnPanelLocation();
      this.setPosition(onPanelLocation.x, onPanelLocation.y);
      this.impacted = true;
      if (this.nowDead) {
         Tann.addBlood(
            com.tann.dice.Main.getCurrentScreen(),
            this.target.getSize().getNumBlood(),
            (int)(this.endPos.x + arrow.getRegionWidth()),
            3.0F,
            (int)this.endPos.y,
            5.0F,
            -12.5F,
            7.5F,
            0.0F,
            9.0F
         );
      }
   }

   @Override
   protected void setupTargetVector(EntPanelCombat targetPan) {
      super.setupTargetVector(targetPan);
      if (this.nowDead) {
         this.endPos = Tann.getAbsoluteCoordinates(targetPan).add(targetPan.getPortraitCenter()).sub(arrow.getRegionWidth(), 0.0F).cpy();
         this.endX = (int)this.endPos.x;
         this.endY = (int)this.endPos.y;
      } else {
         int tries = 4;
         float bestDist = 0.0F;
         int bestX = 0;
         int bestY = 0;

         for (int i = 0; i < tries; i++) {
            super.setupTargetVector(targetPan);
            Vector2 onPanelLocation = this.getOnPanelLocation();
            float worstDist = 9999.0F;
            ArrayIterator var9 = targetPan.getChildren().iterator();

            while (var9.hasNext()) {
               Actor a = (Actor)var9.next();
               if (a instanceof ArrowActor) {
                  float dist = onPanelLocation.dst(a.getX(), a.getY());
                  if (dist < worstDist) {
                     worstDist = dist;
                  }
               }
            }

            if (worstDist > bestDist) {
               bestDist = worstDist;
               bestX = this.endX;
               bestY = this.endY;
            }
         }

         this.endX = bestX;
         this.endY = bestY;
      }
   }

   public Vector2 getOnPanelLocation() {
      Vector2 panelPos = Tann.getAbsoluteCoordinates(this.target.getEntPanel());
      tmp.set(this.endX - panelPos.x, this.endY - panelPos.y);
      return tmp;
   }

   @Override
   public void act(float delta) {
      super.act(delta);
      if (this.impacted) {
         if (this.dodged) {
            this.remove();
         }

         this.magnitude -= 82.0F * delta;
         if (this.magnitude < 0.0F) {
            this.magnitude = 0.0F;
            if (this.blocked || com.tann.dice.Main.isPortrait()) {
               this.remove();
            }
         }
      }
   }

   @Override
   protected float getImpactDurationInternal() {
      return super.getImpactDurationInternal();
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.34146342F;
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      float rotation;
      if (this.currentTravel < this.travelTime) {
         rotation = this.travelRotation;
      } else {
         rotation = (float)(Math.sin(com.tann.dice.Main.secs * 42.0F) * this.magnitude) + (this.player ? 0 : 180);
      }

      batch.setColor(this.getColor());
      batch.draw(
         this.getImage(),
         (int)this.getX(),
         (int)this.getY(),
         this.getImage().getRegionWidth(),
         this.getImage().getRegionHeight() / 2.0F,
         this.getImage().getRegionWidth(),
         this.getImage().getRegionHeight(),
         1.0F,
         1.0F,
         rotation
      );
   }
}
