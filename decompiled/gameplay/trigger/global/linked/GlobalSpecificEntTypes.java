package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class GlobalSpecificEntTypes extends GlobalLinked {
   final List<EntType> types;
   final Personal personal;

   public GlobalSpecificEntTypes(Personal personal, EntType... types) {
      super(personal);
      this.personal = personal;
      this.types = Arrays.asList(types);
   }

   public GlobalSpecificEntTypes(EntType type, Personal personal) {
      this(personal, type);
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.translatedCommaList(this.types) + ":[n]" + this.personal.describeForSelfBuff();
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      Ent de = entState.getEnt();
      return this.types.contains(de.entType) ? this.personal : super.getLinkedPersonal(entState);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl exception = new Pixl(0);

      for (EntType t : this.types) {
         exception.image(t.portrait, t instanceof MonsterType).gap(1);
      }

      exception.cancelRowGap();
      return DipPanel.makeSidePanelGroup(big, exception.pix(), this.personal, Colours.red);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.personal.getCollisionBits(false);
   }

   @Override
   public GlobalLinked splice(Personal newCenter) {
      return new GlobalSpecificEntTypes(newCenter, this.types.toArray(new EntType[0]));
   }
}
