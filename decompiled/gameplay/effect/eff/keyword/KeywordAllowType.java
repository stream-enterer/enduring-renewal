package com.tann.dice.gameplay.effect.eff.keyword;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;

public enum KeywordAllowType {
   YES,
   NO,
   SPELL,
   TARG_PIPS(null, true, true, null, null),
   PIPS_ONLY(null, true, null, null, null),
   TARGET_ONLY(null, null, true, null, false),
   TARGET_ONLY_NOT_SELF(null, null, true, null, false),
   ENEMY_TARG(false, null, true, null, null),
   ENEMY_TARG_PIPS(false, true, true, null, null),
   ALLY_TARG(true, null, true, null, null),
   ALLY_TARG_PIPS(true, true, true, null, null),
   KIND_TARG_PIPS(null, true, true, null, null),
   UNKIND_TARG_PIPS(null, true, true, null, null),
   UNKIND_TARG(null, null, true, null, null),
   PIPS_TOUCHUSABLE(null, true, null, true, null),
   NONBLANK_TOUCHUSABLE(null, null, null, true, false),
   NONBLANK(null, null, null, null, false),
   CANTRIP(null, null, null, null, false),
   SINGLE_TARGET,
   DEATHCHECK;

   final Boolean player;
   final Boolean pips;
   final Boolean target;
   final Boolean touchUsable;
   final Boolean blank;

   private KeywordAllowType() {
      this(null, null, null, null, null);
   }

   private KeywordAllowType(Boolean player, Boolean pips, Boolean target, Boolean touchUsable, Boolean blank) {
      this.player = player;
      this.pips = pips;
      this.target = target;
      this.touchUsable = touchUsable;
      this.blank = blank;
   }

   public boolean check(Eff e) {
      EffType et = e.getType();
      TargetingType tt = e.getTargetingType();
      if (this.player != null && this.player != e.isFriendly()) {
         return false;
      } else if (this.pips != null && this.pips != e.hasValue()) {
         return false;
      } else if (this.target != null && this.target == (tt == TargetingType.Untargeted)) {
         return false;
      } else if (this.touchUsable != null && this.touchUsable && et == EffType.Reroll) {
         return false;
      } else if (this.blank != null && this.blank != (et == EffType.Blank)) {
         return false;
      } else {
         switch (this) {
            case YES:
               return true;
            case SPELL:
               return true;
            case NO:
               return false;
            case CANTRIP:
               return e.getType() != EffType.Resurrect && e.getType() != EffType.Summon;
            case DEATHCHECK:
               return et != EffType.Mana && et != EffType.Recharge && et != EffType.Reroll && !e.isSpecialAddKeyword() && et != EffType.Blank;
            case SINGLE_TARGET:
               return tt == TargetingType.Single;
            case TARGET_ONLY_NOT_SELF:
               return tt != TargetingType.Self;
            default:
               return true;
         }
      }
   }
}
