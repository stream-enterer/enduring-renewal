package com.tann.dice.screens.dungeon.panels.combatEffects.simpleThwack;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import java.util.List;

public class SimpleThwack extends CombatEffectActor {
   TextureRegion image;
   float angleDiff;
   float swingTime;
   float windupTime;
   private static final float FADE_OUT_TIME = 0.2F;
   private static final float SWING_HIT_RATIO = 0.8F;
   List<EntState> targets;
   boolean player;
   String[] sound = Sounds.thwack;

   public SimpleThwack(boolean player, List<EntState> targets, TextureRegion image, float angleDiff, float swingTime, float windupTime) {
      this.player = player;
      this.image = image;
      this.angleDiff = angleDiff * OptionUtils.unkAnim();
      this.swingTime = swingTime * OptionUtils.unkAnim();
      this.windupTime = windupTime * OptionUtils.unkAnim();
      this.targets = targets;
   }

   @Override
   protected void start(FightLog fightLog) {
      if (this.sound != null) {
         Sounds.playSoundDelayed(this.sound, 1.0F, 1.0F, this.getImpactDuration());
      }

      this.setSize(this.image.getRegionWidth(), this.image.getRegionHeight());
      DungeonScreen.get().addActor(this);
      int topMost = 0;
      int bottomMost = 9999;

      for (EntState state : this.targets) {
         EntPanelCombat panel = state.getEnt().getEntPanel();
         topMost = (int)Math.max((float)topMost, panel.getY() + panel.getHeight());
         bottomMost = (int)Math.min((float)bottomMost, panel.getY());
      }

      Vector2 containerLoc = Tann.getAbsoluteCoordinates(DungeonScreen.get().getContainer(!this.player));
      topMost = (int)(topMost + containerLoc.y);
      bottomMost = (int)(bottomMost + containerLoc.y);
      int upwardsExtent = (int)(Math.tan(this.angleDiff) * this.image.getRegionWidth() / 2.0);
      int startY = topMost - upwardsExtent;
      int endY = bottomMost + upwardsExtent;
      EntPanelCombat panel = this.targets.get(0).getEnt().getEntPanel();
      float var10001 = this.player ? 0.0F : panel.getWidth();
      int right = (int)(Tann.getAbsoluteCoordinates(panel).x + var10001 - this.image.getRegionWidth() * 0.5F);
      right = (int)(right + this.image.getRegionWidth() * (this.player ? -0.8F : 0.8F));
      this.setPosition(right, startY);
      this.setRotation((float)(-this.angleDiff * (this.player ? -1 : 1) + (this.player ? 0.0 : Math.PI)));
      float windupAngle = this.angleDiff * 1.5F * (this.player ? -1 : 1);
      int windupDist = 5;
      this.addAction(
         Actions.sequence(
            Actions.parallel(
               Actions.rotateBy(-windupAngle, this.windupTime, Interpolation.pow2Out), Actions.moveBy(0.0F, windupDist, this.windupTime, Interpolation.pow2Out)
            ),
            Actions.parallel(
               Actions.moveTo(this.getX(), endY, this.swingTime, Interpolation.pow3In),
               Actions.rotateBy(this.angleDiff * (this.player ? -2 : 2) + windupAngle, this.swingTime, Interpolation.pow3In)
            ),
            Actions.fadeOut(0.2F),
            Actions.removeActor()
         )
      );
   }

   public void setSound(String[] sound) {
      this.sound = sound;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      int originX = (int)(this.image.getRegionWidth() * 0.2F);
      int originY = (int)(this.image.getRegionHeight() / 2.0F);
      batch.draw(
         this.image,
         this.getX(),
         this.getY(),
         originX,
         originY,
         this.image.getRegionWidth(),
         this.image.getRegionHeight(),
         1.0F,
         1.0F,
         (float)Math.toDegrees(this.getRotation())
      );
      super.draw(batch, parentAlpha);
   }

   @Override
   protected float getImpactDurationInternal() {
      return (this.windupTime + this.swingTime * 0.8F) / OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDurationInternal() {
      return (this.swingTime * 0.19999999F + 0.2F) / OptionUtils.unkAnim();
   }
}
