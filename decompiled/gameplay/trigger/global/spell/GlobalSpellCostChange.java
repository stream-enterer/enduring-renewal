package com.tann.dice.gameplay.trigger.global.spell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.spell.change.AddChange;
import com.tann.dice.gameplay.trigger.global.spell.change.IntegerChange;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class GlobalSpellCostChange extends Global {
   final IntegerChange change;
   final Integer nth;

   public GlobalSpellCostChange(int delta) {
      this(delta, null);
   }

   public GlobalSpellCostChange(IntegerChange change) {
      this(change, null);
   }

   public GlobalSpellCostChange(int delta, Integer nth) {
      this(new AddChange(delta), nth);
   }

   public GlobalSpellCostChange(IntegerChange change, Integer nth) {
      this.change = change;
      this.nth = nth;
   }

   @Override
   public String describeForSelfBuff() {
      String result = "";
      if (this.nth != null) {
         result = "The " + Words.ordinal(this.nth) + " spell you cast each fight costs ";
      } else {
         result = "Spells cost ";
      }

      return result + this.change.describe() + " " + Words.manaString();
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor ia = new ImageActor(Images.tr_manaSpellBottom, Colours.blue);
      if (this.nth != null) {
         ia = new Pixl().text("[blue]" + Words.ordinalShort(this.nth)).row(2).actor(ia).pix();
      }

      Group g = Tann.makeGroup(ia);
      Actor tw = this.change.makeActor(Images.mana);
      g.addActor(tw);
      tw.setPosition((int)(g.getWidth() / 2.0F - tw.getWidth() / 2.0F), (int)(8.0F - tw.getHeight() / 2.0F));
      return g;
   }

   @Override
   public int affectSpellCost(Spell s, int cost, Snapshot snapshot) {
      return this.nth != null && snapshot.getTotalSpellsUsedThisFight() + 1 != this.nth ? cost : this.change.affect(cost);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public boolean allTurnsOnly() {
      return this.nth != null;
   }

   @Override
   public String hyphenTag() {
      return this.change.describe().replaceAll("\\+", "").replaceAll("x", "") + "/" + this.nth;
   }
}
