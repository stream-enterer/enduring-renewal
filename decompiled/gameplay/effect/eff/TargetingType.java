package com.tann.dice.gameplay.effect.eff;

public enum TargetingType {
   Single(true),
   Self(false),
   Top(false),
   Bot(false),
   Mid(false),
   TopAndBot(false),
   Group(false),
   ALL(false),
   SpellSource(false),
   Untargeted(false);

   public final boolean requiresTarget;

   private TargetingType(boolean requiresTarget) {
      this.requiresTarget = requiresTarget;
   }

   public float affectValue(Eff e, boolean player, float value) {
      switch (this) {
         case Group:
            if (!player && !e.isFriendly()) {
               return value * 4.2F;
            } else if (e.isFriendly()) {
               float var5;
               return var5 = value * 2.9F;
            } else {
               if (e.getType() == EffType.Damage) {
                  return value * 2.75F;
               }

               float var4;
               return var4 = value * 2.9F;
            }
         case TopAndBot:
            if (e.isFriendly()) {
               return value * 1.15F;
            }

            return value * 1.5F;
         default:
            return value;
      }
   }
}
