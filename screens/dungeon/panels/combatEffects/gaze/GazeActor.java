package com.tann.dice.screens.dungeon.panels.combatEffects.gaze;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import java.util.List;

public class GazeActor extends CombatEffectActor {
   Targetable targetable;
   Ent source;
   Ent target;
   float top;
   float bot;
   Vector2 sourcePos;

   public GazeActor(Targetable targetable, Ent source, Ent target) {
      this.targetable = targetable;
      this.source = source;
      this.target = target;
   }

   @Override
   protected void start(FightLog fightLog) {
      this.setColor(1.0F, 1.0F, 1.0F, 0.7F);
      Sounds.playSoundDelayed(Sounds.deboost, 1.0F, 1.0F, this.getImpactDuration() * 0.3F);
      List<EntState> targets = this.source
         .getFightLog()
         .getSnapshot(FightLog.Temporality.Present)
         .getActualTargets(this.target, this.targetable.getBaseEffect(), this.source);
      this.top = getTopMost(targets);
      this.bot = getBotMost(targets);
      DungeonScreen.get().addActor(this);
      this.sourcePos = Tann.getAbsoluteCoordinates(this.source.getEntPanel()).cpy();
      this.sourcePos.y = this.sourcePos.y + this.source.getEntPanel().getHeight() / 2.0F;
      this.addAction(Actions.sequence(Actions.delay(this.getImpactDuration()), Actions.fadeOut(this.getExtraDurationInternal()), Actions.removeActor()));
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.1F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.5F;
   }

   public void draw(Batch batch, float parentAlpha) {
      float mid = (this.top + this.bot) / 2.0F;
      float dist = (this.top - this.bot) / 2.0F * 0.9F;
      float realTop = mid + dist * Math.min(this.getColor().a, 0.5F) * 2.0F;
      float realBot = mid - dist * Math.min(this.getColor().a, 0.5F) * 2.0F;
      batch.setColor(Colours.withAlpha(Colours.light, this.getColor().a));
      Draw.drawTriangle(batch, this.sourcePos.x, this.sourcePos.y, 50.0F, realBot, 50.0F, realTop);
   }
}
