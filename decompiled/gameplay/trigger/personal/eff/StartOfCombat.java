package com.tann.dice.gameplay.trigger.personal.eff;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.Pixl;
import java.util.ArrayList;
import java.util.List;

public class StartOfCombat extends PersonalEffContainer {
   public final Eff eff;

   public StartOfCombat(Eff eff) {
      super(eff);
      this.eff = eff;
   }

   @Override
   public void startOfCombat(Snapshot snapshot, EntState entState) {
      snapshot.target(null, new SimpleTargetable(entState.getEnt(), this.eff), false);
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("Start of turn 1") + ": " + this.eff.describe();
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      List<Keyword> referenced = new ArrayList<>(this.eff.getKeywords());
      referenced.remove(Keyword.boost);
      return referenced;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor effImage = this.eff.getBasicImage();
      return new Pixl().text("[notranslate][text]t1: ").actor(effImage).pix();
   }
}
