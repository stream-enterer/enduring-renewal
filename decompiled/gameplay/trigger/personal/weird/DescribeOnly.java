package com.tann.dice.gameplay.trigger.personal.weird;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.trigger.global.GlobalDescribeOnly;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class DescribeOnly extends Personal {
   final String text;

   public DescribeOnly(String text) {
      this.text = text;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new GlobalDescribeOnly(this.text).makePanelActorI(big);
   }

   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }
}
