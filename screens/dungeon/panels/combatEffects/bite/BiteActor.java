package com.tann.dice.screens.dungeon.panels.combatEffects.bite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;

public class BiteActor extends CombatEffectActor {
   TextureRegion[] regions = new TextureRegion[2];
   Ent target;
   EntPanelCombat targetPanel;
   int damage;
   EntSize size;
   boolean shielded;

   public BiteActor(Ent target, int damage, String filename, EntSize size) {
      this.regions[0] = ImageUtils.loadExt("combatEffects/bite/" + filename + "/top");
      this.regions[1] = ImageUtils.loadExt("combatEffects/bite/" + filename + "/bot");
      this.damage = damage;
      this.setSize(this.regions[0].getRegionWidth(), 0.0F);
      this.target = target;
      this.targetPanel = target.getEntPanel();
      this.size = size;
   }

   @Override
   protected void start(FightLog fightLog) {
      this.shielded = isBlocked(this.damage, this.target);
      int height = 10;
      int moveAmount = 9;

      for (int i = 0; i < 2; i++) {
         int xPosition = (int)(this.targetPanel.dieHolder.getX() + this.targetPanel.dieHolder.getWidth() / 2.0F - this.regions[i].getRegionWidth() / 2);
         if (this.shielded) {
            xPosition += this.getShieldedBiteOffset();
         }

         int mul = i * 2 - 1;
         ImageActor teethActor = new ImageActor(this.regions[i]);
         DungeonScreen.get().addActor(teethActor);
         Vector2 panelVector = Tann.getAbsoluteCoordinates(this.targetPanel);
         teethActor.setPosition(
            xPosition + panelVector.x, (int)(this.targetPanel.getHeight() / 2.0F + -mul * height - teethActor.getHeight() / 2.0F) + panelVector.y
         );
         float openRatio = 0.5F;
         float closeRatio = 1.0F - openRatio;
         int openDist = 3;
         teethActor.addAction(
            Actions.sequence(
               Actions.moveBy(0.0F, -mul * openDist, this.getImpactDuration() * openRatio, Interpolation.linear),
               Actions.moveBy(0.0F, mul * (openDist + moveAmount), this.getImpactDuration() * closeRatio, Interpolation.pow5In),
               Actions.run(new Runnable() {
                  @Override
                  public void run() {
                     BiteActor.this.chomp();
                  }
               }),
               Actions.parallel(
                  Actions.fadeOut(this.getExtraDurationInternal()), Actions.moveBy(0.0F, -mul * moveAmount * 0.7F, this.getExtraDurationInternal())
               ),
               Actions.removeActor()
            )
         );
      }
   }

   private int getShieldedBiteOffset() {
      return com.tann.dice.Main.isPortrait() ? 10 : 21;
   }

   private void chomp() {
      if (this.shielded) {
         Sounds.playSound(Sounds.clangs);
      } else {
         switch (this.size) {
            case small:
               Sounds.playSound(Sounds.bitesmall);
               break;
            case reg:
               Sounds.playSound(Sounds.biteReg);
               break;
            case big:
               Sounds.playSound(Sounds.biteBig);
               break;
            case huge:
               Sounds.playSound(Sounds.biteHuge);
         }
      }
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
