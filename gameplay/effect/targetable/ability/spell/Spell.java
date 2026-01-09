package com.tann.dice.gameplay.effect.targetable.ability.spell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spell extends Ability {
   final int cost;

   public Spell(Eff effect, String title, TextureRegion image, int cost, float pw) {
      super(effect, title, image, pw);
      this.cost = cost;
   }

   public Spell(Eff effect, String title, TextureRegion image, int cost) {
      this(effect, title, image, cost, 1.0F);
   }

   @Override
   public Eff getBaseEffect() {
      return this.effect;
   }

   @Override
   public Eff getDerivedEffects() {
      DungeonScreen ds = DungeonScreen.get();
      return ds != null ? this.getDerivedEffects(ds.getFightLog().getSnapshot(FightLog.Temporality.Present)) : this.getBaseEffect();
   }

   @Override
   public Eff getDerivedEffects(List<Global> globals) {
      Eff result = this.getBaseEffect().copy();

      for (Global gt : globals) {
         gt.affectSpell(this.title, result);
      }

      return result;
   }

   @Override
   public List<Actor> getCostActors(Snapshot presentSnapshot, int totalWidth) {
      int MANA_WIDTH = 6;
      List<Actor> result = new ArrayList<>();
      int snapshotMana = 5000;
      int numCost = this.getBaseCost();
      if (presentSnapshot != null) {
         snapshotMana = presentSnapshot.getTotalMana();
         numCost = presentSnapshot.getSpellCost(this);
      }

      if (numCost == 0) {
         return Arrays.asList(new TextWriter("[blue]free"));
      } else {
         if (numCost * 6 <= totalWidth - 2) {
            for (int i = 0; i < numCost; i++) {
               boolean hasMana = snapshotMana > i;
               TextureRegion tr = hasMana ? Images.mana : Images.manaBorder;
               ImageActor ia = new ImageActor(tr);
               if (!hasMana) {
                  ia.setColor(Colours.blue);
               }

               result.add(ia);
            }
         } else {
            Actor a = SpellUtils.makeSpellCostActor(snapshotMana >= numCost, true, numCost);
            result.add(a);
         }

         return result;
      }
   }

   @Override
   public boolean isUsable(Snapshot snapshot) {
      return this.canCast(snapshot);
   }

   @Override
   public Ent getSource() {
      return null;
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   public boolean canCast(Snapshot snapshot) {
      return snapshot.getSpellCost(this) <= snapshot.getTotalMana();
   }

   public int getBaseCost() {
      return this.cost;
   }

   @Override
   public String describe() {
      return this.getDerivedEffects().describe();
   }

   @Override
   public Color getIdCol() {
      return Colours.blue;
   }

   @Override
   public String toString() {
      return this.title + ":" + this.cost;
   }

   @Override
   public float getCostFactorInActual(int heroTier) {
      return ESB.mana.val(this.cost).getEffectTier(HeroTypeUtils.defaultHero(heroTier));
   }

   @Override
   public void beforeUse(Snapshot snapshot, Eff preDerivedEffect, List<Integer> extraData) {
      snapshot.spendManaCost(this);
   }
}
