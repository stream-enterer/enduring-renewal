package com.tann.dice.screens.dungeon.panels.combatEffects.dragonBreath;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.List;

public class DragonBreathController extends CombatEffectController {
   Ent source;
   Ent target;
   Targetable targetable;
   Color[] cols;
   int numParticles;
   static final float SPAWNING_DURATION = 0.2F;
   static final float PARTICLE_DURATION = 0.7F;
   static final float HIT_RATIO = 0.6F;
   static final float SPEED_VARIANCE = 0.5F;
   static final float DIST_VARIANCE = 10.0F;

   public DragonBreathController(Ent source, Ent target, Targetable targetable, Color[] cols, int numParticles) {
      this.source = source;
      this.target = target;
      this.targetable = targetable;
      this.cols = cols;
      this.numParticles = numParticles;
   }

   @Override
   protected void start() {
      if (this.cols[0] == Colours.light) {
         Sounds.playSound(Sounds.fireBreath);
      } else {
         Sounds.playSound(Sounds.poisonBreath);
      }

      List<EntState> targets = this.source
         .getFightLog()
         .getSnapshot(FightLog.Temporality.Present)
         .getActualTargets(this.target, this.targetable.getBaseEffect(), this.source);
      float top = CombatEffectActor.getTopMost(targets);
      float bot = CombatEffectActor.getBotMost(targets);
      Vector2 panelPos = Tann.getAbsoluteCoordinates(this.source.getEntPanel()).cpy().add(0.0F, this.source.getEntPanel().getHeight() / 2.0F);
      double topAngle = Math.atan2(50.0F - panelPos.x, top - panelPos.y);
      double botAngle = Math.atan2(50.0F - panelPos.x, bot - panelPos.y);
      float dist = panelPos.x - DungeonScreen.get().hero.getX() - 16.800001F;

      for (int i = 0; i < this.numParticles; i++) {
         DragonBreathParticle dbp = new DragonBreathParticle(this.cols);
         DungeonScreen.get().addActor(dbp);
         dbp.setSize(10.0F, 10.0F);
         dbp.setPosition(panelPos.x, panelPos.y);
         dbp.setColor(1.0F, 1.0F, 1.0F, 0.0F);
         dbp.setScale(0.8F);
         double angle = Tann.random(botAngle, topAngle);
         float thisDist = dist + Tann.random(-10.0F, 10.0F);
         float targetX = (float)(Math.sin(angle) * thisDist);
         float targetY = (float)(Math.cos(angle) * thisDist);
         float speed = 0.7F + Tann.random(0.5F);
         dbp.addAction(
            Actions.sequence(
               Actions.delay((float)i / this.numParticles * 0.2F),
               Actions.alpha(1.0F),
               Actions.parallel(Actions.moveBy(targetX, targetY, speed, Interpolation.pow2Out), Actions.fadeOut(speed), Actions.scaleTo(1.0F, 1.0F, speed)),
               Actions.removeActor()
            )
         );
      }
   }

   @Override
   protected float getImpactDuration() {
      return 0.54F * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return 0.35999995F * OptionUtils.unkAnim();
   }
}
