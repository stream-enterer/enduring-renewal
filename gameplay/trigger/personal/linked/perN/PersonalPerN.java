package com.tann.dice.gameplay.trigger.personal.linked.perN;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.LinkedPersonal;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerN;
import com.tann.dice.gameplay.trigger.personal.linked.perN.ns.PerNFlat;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonalPerN extends LinkedPersonal {
   final PerN pc;
   final List<Personal> personals;

   public PersonalPerN(Personal link, PerN pc) {
      this(Arrays.asList(link), pc);
   }

   public PersonalPerN(List<Personal> link, PerN pc) {
      super(link.get(0));
      this.pc = pc;
      this.personals = link;
   }

   @Override
   public String describeForSelfBuff() {
      List<String> strings = new ArrayList<>();

      for (int i = 0; i < this.personals.size(); i++) {
         strings.add(com.tann.dice.Main.t(this.personals.get(i).describeForSelfBuff()));
      }

      return "[notranslate]" + Tann.commaList(strings) + " " + this.pc.describePer();
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      List<Actor> actors = new ArrayList<>();

      for (int i = 0; i < this.personals.size(); i++) {
         actors.add(this.personals.get(i).makePanelActor(big));
      }

      Actor a = Tann.layoutMinArea(actors, 3, 999, 999);
      return DipPanel.makeSidePanelGroup(new Pixl(2).text("x").actor(this.pc.makePanelActor()).pix(), a, Colours.grey);
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      int amt = this.pc.getAmt(snapshot, entState);
      List<Personal> result = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         result.addAll(this.personals);
      }

      return result;
   }

   @Override
   public float getPriority() {
      return this.personals.get(0).getPriority();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long result = this.pc.getCollisionBits(player) | super.getCollisionBits(player);

      for (int i = 0; i < this.personals.size(); i++) {
         result |= this.personals.get(i).getCollisionBits(player);
      }

      return result;
   }

   @Override
   public boolean isMultiplable() {
      return false;
   }

   @Override
   public Personal splice(Personal p) {
      return new PersonalPerN(p, this.pc);
   }

   public static Personal basicMultiple(int n, Personal p) {
      return new PersonalPerN(p, new PerNFlat(n));
   }

   @Override
   public Personal genMult(int mult) {
      List<Personal> ps = new ArrayList<>();
      boolean good = false;

      for (Personal personal : this.personals) {
         Personal gm = personal.genMult(mult);
         if (gm == null) {
            ps.add(personal);
         } else {
            ps.add(gm);
            good = true;
         }
      }

      return (Personal)(!good ? super.genMult(mult) : new PersonalPerN(ps, this.pc));
   }
}
