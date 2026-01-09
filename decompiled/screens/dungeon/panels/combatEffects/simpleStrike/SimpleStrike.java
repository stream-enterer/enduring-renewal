package com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;

public class SimpleStrike extends CombatEffectActor {
   private final float SWORD_IMPACT_TIME;
   private final float SWORD_STICK_TIME;
   private final float SWORD_RETRACT_TIME;
   boolean flipped;
   TextureRegion image;
   EntPanelCombat targetPanel;
   Ent target;
   int damage;
   boolean blocked;
   String[] sounds;

   public SimpleStrike(Ent target, int damage, TextureRegion image, float impactTime, float stickTime, float retractTime, String[] sounds) {
      this.target = target;
      this.damage = damage;
      this.image = image;
      this.SWORD_IMPACT_TIME = impactTime * OptionUtils.unkAnim();
      this.SWORD_STICK_TIME = stickTime * OptionUtils.unkAnim();
      this.SWORD_RETRACT_TIME = retractTime * OptionUtils.unkAnim();
      this.sounds = sounds;
      this.setSize(image.getRegionWidth(), image.getRegionHeight());
   }

   public SimpleStrike(Ent target, int damage, TextureRegion image, float impactTime, float stickTime, float retractTime) {
      this(target, damage, image, impactTime, stickTime, retractTime, Sounds.punches);
   }

   @Override
   public void start(FightLog fightLog) {
      this.flipped = this.target.isPlayer();
      this.targetPanel = this.target.getEntPanel();
      int flipMult = this.flipped ? -1 : 1;
      Vector2 panelPos = Tann.getAbsoluteCoordinates(this.targetPanel);
      panelPos.y = panelPos.y + (this.targetPanel.getPreferredY() - this.targetPanel.getY());
      EntState state = this.target.getState(FightLog.Temporality.Visual);
      int shields = state.getShields();
      int startDist = 30;
      int endDist = -2;
      if (shields >= this.damage) {
         this.blocked = true;
         endDist = 10;
      }

      if (this.flipped) {
         this.setPosition(panelPos.x + this.targetPanel.getWidth() + startDist, panelPos.y + this.targetPanel.getHeight() / 2.0F - this.getHeight() / 2.0F);
      } else {
         this.setPosition(panelPos.x - this.getWidth() - startDist, panelPos.y + this.targetPanel.getHeight() / 2.0F - this.getHeight() / 2.0F);
      }

      Action swordAttackSequence = Actions.sequence(
         Actions.moveBy((startDist - endDist) * flipMult, 0.0F, this.SWORD_IMPACT_TIME, Interpolation.swingIn),
         Actions.run(new Runnable() {
            @Override
            public void run() {
               SimpleStrike.this.impact();
            }
         }),
         Actions.delay(this.SWORD_STICK_TIME),
         Actions.parallel(
            Actions.moveBy(this.SWORD_RETRACT_TIME == 0.0F ? 0.0F : -startDist / 3 * flipMult, 0.0F, this.SWORD_RETRACT_TIME, Interpolation.linear),
            Actions.fadeOut(this.SWORD_RETRACT_TIME)
         ),
         Actions.removeActor()
      );
      Sounds.playSoundDelayed(Sounds.slash, 1.0F, 1.0F, this.SWORD_IMPACT_TIME * 0.7F);
      this.addAction(swordAttackSequence);
      DungeonScreen.get().addActor(this);
   }

   protected void impact() {
      Sounds.playSound(this.blocked ? Sounds.clangs : this.sounds);
   }

   @Override
   protected float getImpactDurationInternal() {
      return this.SWORD_IMPACT_TIME / OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDurationInternal() {
      return (this.SWORD_STICK_TIME + this.SWORD_RETRACT_TIME) / OptionUtils.unkAnim();
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      batch.setColor(1.0F, 1.0F, 1.0F, this.getColor().a);
      Draw.drawScaled(
         batch, this.image, (float)((int)(this.getX() + (this.flipped ? this.getWidth() : 0.0F))), (float)((int)this.getY()), this.flipped ? -1.0F : 1.0F, 1.0F
      );
   }
}
