package com.tann.dice.gameplay.effect.targetable.ability.spell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.eff.keyword.KeywordAllowType;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.List;

public class SpellUtils {
   public static boolean allowAddingKeyword(Keyword k) {
      KeywordAllowType kat = k.getAllowType();
      switch (kat) {
         case SPELL:
         case TARGET_ONLY:
         case UNKIND_TARG:
            return true;
         case YES:
         case NO:
         case ALLY_TARG:
         case PIPS_TOUCHUSABLE:
         case NONBLANK_TOUCHUSABLE:
         case NONBLANK:
         case DEATHCHECK:
         default:
            if (KUtils.isMeta(k)) {
               return false;
            } else if (k.getInflict() != null) {
               return true;
            } else {
               switch (k) {
                  case ranged:
                  case poison:
                  case regen:
                  case engage:
                  case cruel:
                  case weaken:
                  case boost:
                  case smith:
                  case serrated:
                  case cleave:
                  case cleanse:
                  case vulnerable:
                  case repel:
                  case eliminate:
                  case heavy:
                  case dispel:
                  case vitality:
                  case focus:
                  case descend:
                  case wither:
                  case deplete:
                  case spellRescue:
                  case manaGain:
                  case plague:
                  case charged:
                  case heal:
                  case shield:
                  case damage:
                  case threesy:
                  case hyperBoned:
                  case terminal:
                  case permaBoost:
                  case wham:
                  case squish:
                  case uppercut:
                  case century:
                  case petrify:
                  case hypnotise:
                  case fierce:
                  case tall:
                     return true;
                  default:
                     return false;
               }
            }
      }
   }

   public static List<Ability> getOnlyLivingSpells(Snapshot snapshot) {
      List<Ability> result = new ArrayList<>();

      for (TP<Ability, Boolean> av : getAvailableSpells(snapshot)) {
         if (av.b) {
            result.add(av.a);
         }
      }

      return result;
   }

   public static List<TP<Ability, Boolean>> getAvailableSpells(Snapshot snapshot) {
      List<TP<Ability, Boolean>> result = new ArrayList<>();
      boolean deadSpellsAllowed = false;

      for (Global gt : snapshot.getGlobals()) {
         deadSpellsAllowed |= gt.allowDeadAbilities();
         Ability s = gt.getGlobalSpell();
         if (s != null) {
            result.add(new TP<>(s, true));
         }
      }

      if (snapshot.getFightLog().getContext().getParty().allowBurst() || snapshot.getTotalMana() > 0) {
         result.add(new TP<>(SpellLib.BURST, true));
      }

      for (Ent h : snapshot.getEntities(true, null)) {
         for (Personal t : snapshot.getState(h).getActivePersonals()) {
            Ability s = t.getAbility();
            if (s != null) {
               boolean living = !snapshot.getState(h).isDead();
               boolean markedLiving = living | deadSpellsAllowed;
               boolean hasAlready = false;

               for (TP<Ability, Boolean> r : result) {
                  if (r.a == s) {
                     hasAlready = true;
                     r.b = r.b | markedLiving;
                  }
               }

               if (!hasAlready) {
                  s.setCol(h.getColour());
                  result.add(new TP<>(s, markedLiving));
               }
            }
         }
      }

      for (int i = result.size() - 1; i >= 0; i--) {
         TP<Ability, Boolean> s = result.get(i);
         if (!snapshot.isAbilityAvailable(s.a)) {
            result.remove(s);
         }
      }

      return result;
   }

   public static Actor makeAbilityCostActor(Ability ability) {
      if (ability instanceof Spell) {
         return makeSpellCostActor(true, false, ((Spell)ability).cost);
      } else if (ability instanceof Tactic) {
         Tactic t = (Tactic)ability;
         Snapshot s = null;
         if (DungeonScreen.get() != null) {
            s = DungeonScreen.get().getFightLog().getSnapshot(FightLog.Temporality.Present);
         }

         List<Actor> acs = t.getCostActors(s, 0);
         return new Pixl().listActor(acs, 2).pix();
      } else {
         return new Rectactor(10, 10, Colours.pink);
      }
   }

   public static Actor makeSpellCostActor(boolean afford, boolean small, int cost) {
      if (cost > 4) {
         small = true;
      }

      if (small) {
         return new Pixl(1).actor(makeCostActor(afford)).text("[blue]x" + cost).pix();
      } else {
         Pixl p = new Pixl(1);

         for (int i = 0; i < cost; i++) {
            p.actor(makeCostActor(afford));
         }

         return p.pix();
      }
   }

   private static Actor makeCostActor(boolean afford) {
      if (afford) {
         return new ImageActor(Images.mana);
      } else {
         Actor manaActor = new ImageActor(Images.manaBorder);
         manaActor.setColor(Colours.blue);
         return manaActor;
      }
   }
}
