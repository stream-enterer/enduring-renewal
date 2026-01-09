package com.tann.dice.gameplay.content.ent.die.side;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;
import com.tann.dice.statics.ImageUtils;

public class EnSiBi {
   private TextureRegion tr;
   private Eff effect;
   public EntSize size = EntSize.reg;

   public EnSiBi image(String image) {
      this.tr = ImageUtils.loadExt3d(this.size + "/face/" + image);
      return this;
   }

   public EnSiBi effect(EffBill eb) {
      return this.effect(eb.bEff());
   }

   public EnSiBi effect(Eff effect) {
      if (this.effect != null) {
         throw new RuntimeException("uhoh!! " + effect.describe());
      } else {
         this.effect = effect;
         return this;
      }
   }

   public EnSiBi size(EntSize size) {
      this.size = size;
      return this;
   }

   public EntSide noVal() {
      return this.val(-999);
   }

   public EntSide val(int value) {
      if (this.effect.hasValue()) {
         this.effect.setValue(value);
      }

      try {
         return new EntSide(this.tr, this.effect, this.size);
      } catch (Exception var3) {
         var3.printStackTrace();
         throw new RuntimeException("failed to make side " + this.effect);
      }
   }

   public EntSide sticker(Item src) {
      return this.image("special/sticker").effect(new EffBill().friendly().buff(new Buff(1, new AsIfHasItem(src)))).noVal();
   }

   public EntSide enchant(Modifier src) {
      return this.image("special/enchant").effect(new EffBill().enchant(src.getName())).noVal();
   }

   public EntSide cast(Ability src) {
      Eff base = src.getBaseEffect().copy();
      base.getKeywords().removeAll(KUtils.getAbilityOnlyKeywords());
      EnSiBi esb = this.image("special/cast").effect(base);
      return base.hasValue() ? esb.val(base.getValue()) : esb.noVal();
   }
}
