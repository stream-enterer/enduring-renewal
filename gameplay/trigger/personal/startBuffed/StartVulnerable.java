package com.tann.dice.gameplay.trigger.personal.startBuffed;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.merge.Vulnerable;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;

public class StartVulnerable extends StartBuffed {
   final int vulnAmt;

   public StartVulnerable(int vulnAmt) {
      this.vulnAmt = vulnAmt;
   }

   @Override
   public String describeForSelfBuff() {
      return "Take " + Tann.delta(this.vulnAmt) + " damage from dice and abilities";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl();
      TextureRegion tr = new Vulnerable(1).getImage();
      if (this.vulnAmt != 1) {
         p.text("[text]" + this.vulnAmt + "x ");
      }

      p.image(tr);
      return p.pix();
   }

   @Override
   public void startOfCombat(Snapshot snapshot, EntState entState) {
      entState.addBuff(new Buff(1, new Vulnerable(this.vulnAmt)));
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.DEBUFF;
   }

   @Override
   public String hyphenTag() {
      return this.vulnAmt + "";
   }
}
