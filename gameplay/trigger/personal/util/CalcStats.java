package com.tann.dice.gameplay.trigger.personal.util;

public class CalcStats {
   final float damage;
   final float hp;

   public CalcStats() {
      this(999.0F, 999.0F);
   }

   public CalcStats(float damage, float hp) {
      this.damage = damage;
      this.hp = hp;
   }

   public float getDamage() {
      return this.damage;
   }

   public float getHp() {
      return this.hp;
   }

   public CalcStats multipliedBy(float mult) {
      return new CalcStats(this.damage * mult, this.hp * mult);
   }
}
