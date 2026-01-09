package com.tann.dice.gameplay.effect.targetable.ability.tactic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.generation.TacticGeneration;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tactic extends Ability {
   final TacticCost tacticCost;
   TextureRegion override;

   public Tactic(String title, TacticCost tacticCost, EffBill eb) {
      this(title, tacticCost, eb.bEff());
   }

   public Tactic(String title, TacticCost tacticCost, Eff effect, float pw) {
      super(effect, title, makeTacticImage(title), pw);
      this.tacticCost = tacticCost;
   }

   public Tactic(String title, TacticCost tacticCost, Eff effect) {
      this(title, tacticCost, effect, 1.0F);
   }

   public Tactic(Spell s, TacticCost tc) {
      super(s.getBaseEffect(), s.getTitle(), makeTacticImage(s.getTitle()));
      this.tacticCost = tc;
   }

   public Tactic(Spell s, TacticCost tc, TextureRegion imageOverride) {
      super(s.getBaseEffect(), s.getTitle(), imageOverride);
      this.override = imageOverride;
      this.tacticCost = tc;
   }

   private static TextureRegion makeTacticImage(String title) {
      return ImageUtils.loadExt("ability/tactic/special/placeholder");
   }

   @Override
   public Eff getBaseEffect() {
      return this.effect;
   }

   @Override
   public Eff getDerivedEffects() {
      return this.getBaseEffect();
   }

   @Override
   public Eff getDerivedEffects(List<Global> globals) {
      return this.getBaseEffect();
   }

   @Override
   public List<Actor> getCostActors(Snapshot presentSnapshot, int totalWidth) {
      List<Actor> acs = this.tacticCost.makeActors(presentSnapshot);
      return acs.isEmpty() ? Arrays.asList(new TextWriter("[green]free")) : acs;
   }

   @Override
   public String describe() {
      String cd = this.describeCost();
      String ed = this.effect.describe(true);
      return cd == null ? ed : ed + "[grey] (" + cd + ")";
   }

   public String describeCost() {
      return this.tacticCost.describe();
   }

   @Override
   public Color getIdCol() {
      return Colours.yellow;
   }

   @Override
   public boolean isUsable(Snapshot snapshot) {
      return this.tacticCost.isUsable(snapshot);
   }

   public List<Integer> getUsedHeroIndices(Snapshot snapshot) {
      List<EntState> useds = this.tacticCost.getHeroesWhoWantToBeUsedForTactic(snapshot);
      List<EntState> states = snapshot.getStates(true, false);
      List<Integer> indices = new ArrayList<>();

      for (EntState used : useds) {
         indices.add(states.indexOf(used));
      }

      return indices;
   }

   @Override
   public Ent getSource() {
      return null;
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public void beforeUse(Snapshot snapshot, Eff preDerivedEffect, List<Integer> extraData) {
      List<EntState> states = snapshot.getStates(true, false);

      for (Integer extraDatum : extraData) {
         states.get(extraDatum).useDie();
      }
   }

   public int getNumCost() {
      return this.tacticCost.costs.size();
   }

   public TacticCost debugGetTacticCost() {
      return this.tacticCost;
   }

   @Override
   public boolean useImage() {
      return this.override != null;
   }

   @Override
   public float getCostFactorInActual(int heroTier) {
      return TacticGeneration.getPowerOfCostInOnePipSides(this.tacticCost, heroTier) * HeroTypeUtils.getEffectTierFor(heroTier);
   }
}
