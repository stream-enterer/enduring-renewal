package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import java.util.List;

public class ChangeArt extends AffectSideEffect {
   public final EntSide artSrc;

   public ChangeArt(EntSide artSrc) {
      this.artSrc = artSrc;
   }

   public static Ent genericSource() {
      return null;
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            super.draw(batch, x, y);
            TextureRegion src = ChangeArt.this.artSrc.getTexture2D();
            int offset = 0;
            int drawY = offset + y;
            ChangeArt.this.artSrc.draw(batch, ChangeArt.genericSource(), x, drawY, null, null);
         }
      };
   }

   @Override
   public String describe() {
      return "change art";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      ReplaceWith.replaceSide(sideState, FacadeUtils.copyTex(e, this.artSrc, this.artSrc.getHSL()));
   }
}
