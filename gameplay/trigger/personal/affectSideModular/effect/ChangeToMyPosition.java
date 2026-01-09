package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class ChangeToMyPosition extends AffectSideEffect {
   final SpecificSidesType sst;

   public ChangeToMyPosition(SpecificSidesType sst) {
      if (sst.sideIndices.length != 1) {
         throw new RuntimeException("sst: " + sst.name());
      } else {
         this.sst = sst;
      }
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String result = "Replace ";
      boolean plural = true;

      for (AffectSideCondition asc : conditions) {
         result = result + asc.describe() + " ";
         if (!asc.isPlural()) {
            plural = false;
         }
      }

      if (conditions.size() == 0) {
         result = result + "all sides ";
      } else if (!result.contains("side")) {
         result = result + Words.plural("side", plural) + " ";
      }

      return result + "with my " + this.sst.getLowercaseName() + " side";
   }

   @Override
   public String describe() {
      return "errr";
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      ReplaceWith.replaceSide(sideState, new EntSideState(owner, owner.getEnt().getSides()[this.sst.sideIndices[0]], sourceIndex));
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      for (AffectSideCondition asc : conditions) {
         if (asc instanceof TypeCondition) {
            return this.makeActorWithArrow((TypeCondition)asc);
         }
      }

      return super.getOverrideActor(conditions);
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            TextureRegion tr = ChangeToMyPosition.this.sst.getArrowImage();
            batch.setColor(Colours.light);
            batch.draw(tr, (int)(x + 8 - tr.getRegionWidth() / 2.0F), (int)(y + 8 - tr.getRegionHeight() / 2.0F));
         }
      };
   }

   private Actor makeActorWithArrow(TypeCondition asc) {
      Pixl p = new Pixl(0);
      int gap = 2;
      GenericView gv = asc.getActor();
      gv.addDraw(asc.getAddDraw());
      p.actor(gv).gap(2).image(Images.arrowRight, Colours.light).gap(2).image(this.sst.templateImage, Colours.text);
      return p.pix();
   }
}
