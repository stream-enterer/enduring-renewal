package com.tann.dice.screens.dungeon.panels.combatEffects.simplePanelImage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;

public class SimplePanelImage extends CombatEffectActor {
   final Ent target;
   final TextureRegion image;
   final Eff eff;
   final String[] overrideSound;

   public SimplePanelImage(Ent target, TextureRegion image, Eff eff) {
      this(target, image, eff, null);
   }

   public SimplePanelImage(Ent target, TextureRegion image, Eff eff, String[] overrideSound) {
      this.target = target;
      this.image = image;
      this.eff = eff;
      this.overrideSound = overrideSound;
      this.setSize(image.getRegionWidth(), image.getRegionHeight());
   }

   @Override
   protected void start(FightLog fightLog) {
      if (this.overrideSound != null) {
         Sounds.playSound(this.overrideSound);
      } else {
         this.eff.playSound();
      }

      EntPanelCombat panel = this.target.getEntPanel();
      panel.addActor(this);
      this.setPosition((int)(panel.HPHolder.getX(1) - this.getWidth() / 2.0F), (int)(panel.HPHolder.getY(1) - this.getHeight() / 2.0F));
      this.addAction(Actions.sequence(Actions.fadeOut(this.getExtraDurationInternal(), Interpolation.pow2In), Actions.removeActor()));
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      batch.draw(this.image, this.getX(), this.getY());
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.0F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.4F;
   }
}
