package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class GlobalAllMonstersExcept extends GlobalLinked {
   public final Personal personal;
   final MonsterType except;

   public GlobalAllMonstersExcept(MonsterType except, Personal personal) {
      super(personal);
      this.except = except;
      this.personal = personal;
   }

   @Override
   public String describeForSelfBuff() {
      String start = "";
      start = "[red]All monsters except " + this.except.getName(true).toLowerCase() + ":[cu][n]";
      return start + Words.capitaliseFirst(this.personal.describeForSelfBuff().toLowerCase());
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      return !entState.getEnt().isPlayer() && entState.getEnt().entType != this.except ? this.personal : super.getLinkedPersonal(entState);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor exception = Tann.combineActors(new ImageActor(this.except.portrait, true), new ImageActor(Images.ui_cross, Colours.red));
      return DipPanel.makeSidePanelGroup(big, exception, this.personal, Colours.red);
   }

   public static GlobalAllMonstersExcept summonOnDeath(MonsterType mt) {
      return new GlobalAllMonstersExcept(mt, new OnDeathEffect(new EffBill().summon(mt.getName(false), 1).bEff(), new SoundSnapshotEvent(Sounds.summonBones)));
   }

   @Override
   public GlobalLinked splice(Personal newCenter) {
      return new GlobalAllMonstersExcept(this.except, newCenter);
   }
}
