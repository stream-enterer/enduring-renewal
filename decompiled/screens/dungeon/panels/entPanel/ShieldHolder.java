package com.tann.dice.screens.dungeon.panels.entPanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.ShakeAction;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;

public class ShieldHolder extends Actor {
   private static final TextureRegion shieldPortraitFlat = ImageUtils.loadExt("combatEffects/shield/shieldPortraitFlat");
   private static final TextureRegion shieldPortrait = ImageUtils.loadExt("combatEffects/shield/shieldPortrait");
   private static final TextureRegion shieldImage = ImageUtils.loadExt("combatEffects/shield/shield");
   private static final TextureRegion shieldFlat = ImageUtils.loadExt("combatEffects/shield/shieldFlat");
   private static final TextureRegion[] brokenShield = new TextureRegion[]{
      ImageUtils.loadExt("combatEffects/shield/shieldBrokenTop"), ImageUtils.loadExt("combatEffects/shield/shieldBrokenBot")
   };
   Ent ent;

   private static TextureRegion getRegion() {
      return getRegion(false);
   }

   private static TextureRegion getRegion(boolean flat) {
      boolean portrait = useSmallShieldHolder();
      if (flat) {
         return portrait ? shieldPortraitFlat : shieldFlat;
      } else {
         return portrait ? shieldPortrait : shieldImage;
      }
   }

   public static boolean useSmallShieldHolder() {
      return com.tann.dice.Main.width < 216;
   }

   public ShieldHolder(Ent ent) {
      this.ent = ent;
      this.setSize(getRegion().getRegionWidth(), getRegion().getRegionHeight());
      this.setTouchable(Touchable.disabled);
      this.setColor(0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void crack() {
      Vector2 pos = Tann.getAbsoluteCoordinates(this);

      for (int i = 0; i < 2; i++) {
         ImageActor ia = new ImageActor(brokenShield[i]);
         DungeonScreen.get().addActor(ia);
         ia.setPosition(pos.x, pos.y);
         int moveDist = 6;
         float moveDuration = 0.3F;
         ia.addAction(
            Actions.sequence(
               Actions.parallel(Actions.moveBy(0.0F, (i * 2 - 1) * -moveDist, moveDuration, Interpolation.pow2Out), Actions.fadeOut(moveDuration)),
               Actions.removeActor()
            )
         );
      }

      this.setVisible(false);
   }

   public void addAction(Action action) {
      super.addAction(action);
   }

   public void setColor(Color color) {
      super.setColor(color);
   }

   public void reset() {
      this.setVisible(true);
      this.toFront();
   }

   public void shake() {
      this.addAction(new ShakeAction(3.0F, 15.0F, 0.3F, Interpolation.linear));
   }

   public void draw(Batch batch, float parentAlpha) {
      int visualShields = this.ent.getState(FightLog.Temporality.Visual).getShields();
      if (visualShields > 0) {
         batch.setColor(Colours.z_white);
         batch.draw(getRegion(false), (int)this.getX(), (int)this.getY());
         batch.setColor(Colours.withAlpha(Colours.light, this.getColor().r));
         batch.draw(getRegion(true), (int)this.getX(), (int)this.getY());
         batch.setColor(Colours.light);
         TannFont.font
            .drawString(batch, "" + visualShields, (int)(this.getX() + (int)(this.getWidth() / 2.0F)), (int)(this.getY() + (int)(this.getHeight() / 2.0F)), 1);
      }

      super.draw(batch, parentAlpha);
   }

   public void flash() {
      this.setColor(Colours.z_white);
      this.addAction(Actions.color(Colours.z_black, 0.3F));
   }
}
