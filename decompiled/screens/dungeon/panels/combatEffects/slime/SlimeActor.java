package com.tann.dice.screens.dungeon.panels.combatEffects.slime;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class SlimeActor extends CombatEffectActor {
   Vector2 startPos;
   Vector2 endPos;
   int damage;
   int size;
   Ent target;
   EntSize entSize;
   boolean singleTarget;
   TextureRegion tr;

   public SlimeActor(Vector2 startPos, Vector2 endPos, Ent target, int damage, EntSize size, boolean singleTarget) {
      this.singleTarget = singleTarget;
      this.entSize = size;
      this.startPos = startPos;
      this.endPos = endPos;
      this.damage = size.getPixels();
      this.target = target;
      this.damage = damage;
      switch (size) {
         case small:
            this.size = 8;
            this.tr = Images.combatEffectSlimesmall;
            break;
         case reg:
            this.size = 11;
            break;
         case big:
            this.size = 14;
            break;
         case huge:
            this.size = 18;
      }

      this.setPosition(startPos.x, startPos.y);
   }

   @Override
   protected void start(FightLog fightLog) {
      if (this.singleTarget && isBlocked(this.damage, this.target)) {
         this.endPos.x = this.endPos.x + 27.0F * (com.tann.dice.Main.isPortrait() ? 0.5F : 1.0F);
      }

      this.addAction(
         Actions.sequence(Actions.moveTo(this.endPos.x, this.endPos.y, this.getImpactDuration(), Interpolation.linear), Actions.run(new Runnable() {
            @Override
            public void run() {
               for (int i = 0; i < SlimeActor.this.size; i++) {
                  SlimeDrop slimeDrop = new SlimeDrop(SlimeActor.this.getX(), SlimeActor.this.getY(), SlimeActor.this.size);
                  DungeonScreen.get().addActor(slimeDrop);
               }
            }
         }), Actions.removeActor())
      );
   }

   public static float GET_IMPACT_DURATION(EntSize size) {
      switch (size) {
         case small:
            return 0.3F;
         case reg:
            return 0.4F;
         case big:
            return 0.5F;
         case huge:
            return 0.6F;
         default:
            return 0.0F;
      }
   }

   public static float GET_EXTRA_DURATION(EntSize size) {
      return GET_IMPACT_DURATION(size) * 0.35F;
   }

   @Override
   protected float getImpactDurationInternal() {
      return GET_IMPACT_DURATION(this.entSize);
   }

   @Override
   protected float getExtraDurationInternal() {
      return GET_EXTRA_DURATION(this.entSize);
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.tr != null) {
         batch.setColor(Colours.z_white);
         batch.draw(this.tr, this.getX() - this.tr.getRegionWidth() / 2, this.getY() - this.tr.getRegionHeight() / 2);
      } else {
         batch.setColor(Colours.green);
         Draw.fillEllipse(batch, this.getX(), this.getY(), this.size, this.size);
      }

      super.draw(batch, parentAlpha);
   }
}
