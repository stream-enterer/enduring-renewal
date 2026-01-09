package com.tann.dice.screens.dungeon.panels.combatEffects.crush;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;

public class CrushActor extends CombatEffectActor {
   final boolean top;
   final Ent target;
   final TextureRegion img;

   public CrushActor(boolean top, Ent target, TextureRegion img) {
      this.top = top;
      this.target = target;
      this.img = img;
      this.setSize(img.getRegionWidth(), img.getRegionHeight());
   }

   @Override
   protected void start(FightLog fightLog) {
      EntPanelCombat panel = this.target.getEntPanel();
      Vector2 panelLoc = Tann.getAbsoluteCoordinates(panel);
      this.setX((int)(panelLoc.x + panel.getWidth() / 2.0F - this.getWidth() / 2.0F));
      int targetY = (int)(panelLoc.y - this.getHeight() * 0.5F);
      if (this.top) {
         targetY = (int)(targetY + panel.getHeight());
      }

      int dist = 45;
      this.setY(targetY + dist * (this.top ? 1 : -1));
      this.addAction(
         Actions.sequence(Actions.moveTo(this.getX(), targetY, this.getImpactDuration(), Interpolation.pow2In), Actions.parallel(Actions.run(new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.clangs);
            }
         }), Actions.moveBy(0.0F, 5 * (this.top ? 1 : -1), this.getExtraDurationInternal()), Actions.fadeOut(this.getExtraDurationInternal())))
      );
      DungeonScreen.get().addActor(this);
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.drawScaled(batch, this.img, this.getX(), this.getY() + (this.top ? 0.0F : this.getHeight()), 1.0F, this.top ? 1.0F : -1.0F);
      super.draw(batch, parentAlpha);
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.35F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.3F;
   }
}
