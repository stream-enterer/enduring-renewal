package com.tann.dice.gameplay.trigger.personal.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Pixl;
import java.util.List;

public class MultiDifferentPersonal extends Personal {
   public final List<Personal> personals;

   public MultiDifferentPersonal(List<Personal> personals) {
      this.personals = personals;
   }

   @Override
   public boolean showInEntPanelInternal() {
      for (Personal personal : this.personals) {
         if (personal.showInEntPanel()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String describeForSelfBuff() {
      String result = "";

      for (int i = 0; i < this.personals.size(); i++) {
         String s = this.personals.get(i).describeForSelfBuff();
         if (s != null && !s.isEmpty()) {
            result = result + s;
         }

         if (i < this.personals.size() - 1) {
            result = result + "[n]and[n]";
         }
      }

      return result;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl();

      for (int i = 0; i < this.personals.size(); i++) {
         Actor a = this.personals.get(i).makePanelActor(big);
         p.actor(a);
         if (i < this.personals.size() - 1) {
            p.gap(2);
         }
      }

      return p.pix();
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      return this.getLinkedPersonalsNoSnapshot(entState);
   }

   @Override
   public List<Personal> getLinkedPersonalsNoSnapshot(EntState entState) {
      return this.personals;
   }
}
