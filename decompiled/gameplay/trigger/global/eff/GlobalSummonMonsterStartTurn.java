package com.tann.dice.gameplay.trigger.global.eff;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeModAddMonsterMeta;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementAll;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementEveryN;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class GlobalSummonMonsterStartTurn extends GlobalTurnRequirement {
   final MonsterType type;

   public GlobalSummonMonsterStartTurn(TurnRequirement turnRequirement, MonsterType type) {
      super(turnRequirement, new GlobalStartTurnEff(new EffBill().summon(type.getName(false), 1).bEff()));
      this.type = type;
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t(this.describeSummon()) + " " + com.tann.dice.Main.t(this.requirement.describe()).toLowerCase();
   }

   private String describeSummon() {
      return com.tann.dice.Main.self().translator.shouldTranslate()
         ? "Summon " + this.type.getName(true).toLowerCase()
         : "Summon " + Words.fullPlural(this.type.getName(true).toLowerCase(), 1);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      if (big) {
         Pixl p = new Pixl(0);
         p.actor(new EntPanelInventory(this.type.makeEnt()).withoutDice().getFullActor(), com.tann.dice.Main.width / 2);
         return p.pix();
      } else {
         return DipPanel.makeSidePanelGroup(
            this.requirement.makePanelActor(), new Pixl().text("[pink][plus]").gap(2).image(this.type.portrait, true).pix(), GlobalTurnRequirement.TURN_COL
         );
      }
   }

   public static Modifier makeGenerated(MonsterType mt, boolean eachTurn) {
      if (mt.getName().equalsIgnoreCase("chest")) {
         return null;
      } else if (mt.isUnique()) {
         return null;
      } else {
         TurnRequirement rq = (TurnRequirement)(eachTurn ? new TurnRequirementAll() : new TurnRequirementEveryN(3));
         int maxTier = eachTurn ? 60 : 10;
         float aprroxStr = mt.getSummonValueForModifier();
         float tier = aprroxStr * (eachTurn ? 2.2F : 0.54F);
         String pref = (eachTurn ? "Summon" : "3rd") + ".";
         String name = pref + (PipeModAddMonsterMeta.ALLOW_PLURAL_NAME ? Words.plural(mt.getName(false)) : mt.getName(false));
         if (!eachTurn) {
            float softMax = maxTier * 0.8F;
            tier = Tann.softLimit(tier, softMax, maxTier);
         }

         return new Modifier(-tier, name, new GlobalSummonMonsterStartTurn(rq, mt));
      }
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }
}
