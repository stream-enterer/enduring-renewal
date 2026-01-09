package com.tann.dice.gameplay.trigger.personal.specialPips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.screens.shaderFx.DeathType;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;

public class DeathSpecialHp extends SpecialHp {
   public DeathSpecialHp(PipLoc loc) {
      super(loc);
   }

   @Override
   public TP<TextureRegion, Color> getPipTannple(boolean big) {
      return new TP<>(big ? Images.hp_cross : Images.hp_small, Colours.pink);
   }

   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      int[] pipLocations = this.getPips(self.getMaxHp());
      if (Tann.contains(pipLocations, self.getHp())) {
         self.kill(DeathType.CutDiagonal);
      }
   }

   @Override
   protected String describe() {
      return "I die if this is removed and no further";
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      int[] pipLocations = this.getPips(entType);
      if (pipLocations.length == 0) {
         return hp;
      } else {
         int pipLoc = pipLocations[pipLocations.length - 1];
         float dmgRequired = hp - pipLoc;
         return (dmgRequired + hp) / 2.0F;
      }
   }
}
