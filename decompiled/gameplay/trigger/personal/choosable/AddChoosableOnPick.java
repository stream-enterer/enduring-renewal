package com.tann.dice.gameplay.trigger.personal.choosable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class AddChoosableOnPick extends Personal {
   final Choosable[] choosables;

   public AddChoosableOnPick(Choosable... choosables) {
      this.choosables = choosables;
   }

   @Override
   public boolean isOnPick() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      List<String> chs = new ArrayList<>();

      for (Choosable choosable : this.choosables) {
         if (choosable instanceof Modifier) {
            chs.add(((Modifier)choosable).getName(true));
         } else {
            chs.add(choosable.describe());
         }
      }

      return "On pick, gain " + Tann.commaList(chs);
   }

   @Override
   public void onChoose(DungeonContext dc, Choosable source) {
      int items = dc.getParty().getItems(null).size();
      ChoosableUtils.checkedOnChoose(this.choosables, dc, "on-pick");
      if (items != dc.getParty().getItems(null).size()) {
         GlobalStartWithItem.addLevelEndIfNotAlready();
      }

      super.onChoose(dc, source);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      float result = total;

      for (Choosable choosable : this.choosables) {
         result += TierUtils.modTierToHeroEffectTier(choosable.getModTier(), type instanceof HeroType ? ((HeroType)type).level : 1);
      }

      return result;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public Actor getTraitActor() {
      TextureRegion tr = ImageUtils.loadExt("trigger/trait/choosable");
      return new ImageActor(tr, this.getCol());
   }

   private Color getCol() {
      return this.choosables[0].getColour();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long result = 0L;

      for (Choosable choosable : this.choosables) {
         if (choosable.getType() == ChoosableType.Random) {
            RandomTieredChoosable rtc = (RandomTieredChoosable)choosable;
            if (rtc.ty == ChoosableType.Modifier) {
               result |= Collision.MODIFIER;
            }
         }
      }

      return result;
   }
}
