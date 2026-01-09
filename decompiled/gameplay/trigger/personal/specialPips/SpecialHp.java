package com.tann.dice.gameplay.trigger.personal.specialPips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;

public abstract class SpecialHp extends Personal {
   protected final PipLoc pipLoc;

   protected SpecialHp(PipLoc pipLoc) {
      this.pipLoc = pipLoc;
   }

   public abstract TP<TextureRegion, Color> getPipTannple(boolean var1);

   public int[] getPips(EntType type) {
      return this.pipLoc.getLocs(type.hp);
   }

   public int[] getPips(int maxHp) {
      return this.pipLoc.getLocs(maxHp);
   }

   @Override
   public boolean showInDiePanel() {
      return true;
   }

   @Override
   public final String describeForSelfBuff() {
      TP<TextureRegion, Color> pip = this.getPipTannple(true);
      return "[notranslate]"
         + TextWriter.getTag(pip.b)
         + TextWriter.getTag(pip.a)
         + "[cu] = "
         + com.tann.dice.Main.t(this.describe())
         + " [grey]("
         + com.tann.dice.Main.t(this.pipLoc.describe())
         + ")[cu]";
   }

   protected abstract String describe();

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   protected int getNextPipLocation(int minTriggerHp, int maxHP) {
      return this.pipLoc.getNextPipLocation(minTriggerHp, maxHP);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return this.pipLoc.makePanelActor(this.getPipTannple(true));
   }

   @Override
   public Actor getTraitActor() {
      return this.pipLoc.makeTraitPanelActor(this.getPipTannple(true));
   }

   @Override
   public TextureRegion getSpecialImage() {
      return this.getPipTannple(true).a;
   }

   @Override
   public float getPriority() {
      return 20.0F;
   }

   @Override
   public boolean canBeAddedTo(EntState entState) {
      return this.pipLoc.allowAddingToMaxHpEntity(entState.getMaxHp());
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.TRIGGER_PIP;
   }

   @Override
   public boolean showImageInDiePanelTitle() {
      return false;
   }

   @Override
   public String hyphenTag() {
      return this.pipLoc.hyphenTag();
   }
}
