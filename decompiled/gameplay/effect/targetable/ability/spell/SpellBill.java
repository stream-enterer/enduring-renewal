package com.tann.dice.gameplay.effect.targetable.ability.spell;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Tann;

public class SpellBill {
   Eff effects;
   String title;
   TextureRegion image;
   int cost;
   float pwMult = 1.0F;

   public Spell bSpell() {
      return new Spell(this.effects, this.title, this.image, this.cost, this.pwMult);
   }

   public SpellBill eff(Eff effs) {
      Tann.assertTrue(this.effects == null);
      this.effects = effs;
      return this;
   }

   public SpellBill eff(EffBill bills) {
      return this.eff(bills.bEff());
   }

   public SpellBill title(String title) {
      this.title = title.substring(0, 1).toUpperCase() + title.substring(1);
      this.image = ImageUtils.loadExtNull("ability/spell/" + title.toLowerCase().replaceAll(" ", "_"));
      if (this.image == null) {
         this.image = ImageUtils.loadExt("ability/spell/special/placeholder");
      }

      return this;
   }

   public SpellBill overrideImage(TextureRegion tr) {
      this.image = tr;
      return this;
   }

   public SpellBill cost(int mana) {
      this.cost = mana;
      return this;
   }

   public SpellBill debug() {
      return this.title("debug").cost(1);
   }

   public SpellBill power(float pw) {
      this.pwMult = pw;
      return this;
   }
}
