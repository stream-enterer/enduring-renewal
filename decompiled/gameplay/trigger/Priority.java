package com.tann.dice.gameplay.trigger;

public abstract class Priority {
   public static final float VULNERABLE = -32.0F;
   public static final float GNOLL_ARMOUR = -31.0F;
   public static final float TOUGH_SPECIAL_HP = -30.0F;
   public static final float MIMIC_PRIORITY = -11.0F;
   public static final float HERO_TRAITS = -11.0F;
   public static final float ITEM_PRIORITY = -10.0F;
   public static final float ENEMY_TRAITS = -9.0F;
   public static final float TRIGGER_PIP = -5.0F;
   public static final float TRIGGER_PIP_LATE = -4.0F;
   public static final float BUFF_PRIORITY = 0.0F;
   public static final float SUMMON_PRIORITY = 20.0F;
}
