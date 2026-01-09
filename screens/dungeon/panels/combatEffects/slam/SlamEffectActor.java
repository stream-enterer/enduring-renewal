package com.tann.dice.screens.dungeon.panels.combatEffects.slam;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;

public class SlamEffectActor extends CombatEffectActor {
   private static final TextureRegion crack = ImageUtils.loadExt("combatEffects/misc/crack");
   private final Actor entPanel;
   private static final float moveUp = 0.4F;
   private static final float moveDown = 0.15F;
   private static final float wait = 0.35F;

   public SlamEffectActor(Ent target) {
      this.entPanel = target.getEntPanel();
   }

   @Override
   protected void start(FightLog fightLog) {
      int moveBy = 20;
      this.entPanel.toFront();
      this.entPanel
         .addAction(
            Actions.sequence(
               Actions.moveBy(0.0F, moveBy, 0.4F * OptionUtils.unkAnim(), Interpolation.pow2Out),
               Actions.moveBy(0.0F, -moveBy, 0.15F * OptionUtils.unkAnim(), Interpolation.pow3In),
               Actions.run(new Runnable() {
                  @Override
                  public void run() {
                     SlamEffectActor.this.impact();
                  }
               })
            )
         );
   }

   private void impact() {
      Sounds.playSound(Sounds.slam);
      Image image = new Image(crack);
      DungeonScreen.get().addActor(image);
      image.addAction(Actions.sequence(Actions.fadeOut(1.1F * OptionUtils.unkAnim()), Actions.removeActor()));
      Vector2 panelPos = Tann.getAbsoluteCoordinates(this.entPanel);
      image.setPosition(
         panelPos.x - this.entPanel.getWidth() * 0.7F + image.getWidth() / 2.0F, panelPos.y + this.entPanel.getHeight() / 2.0F - image.getHeight() / 2.0F
      );
      image.toBack();
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.55F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.35F;
   }
}
