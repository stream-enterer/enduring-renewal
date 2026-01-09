package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.ManaGainEvent;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.List;

public class AbilityCommand extends TargetableCommand {
   Ability ability;
   List<Integer> extraData;

   public AbilityCommand(Ability ability, Ent target) {
      super(ability, target);
      this.ability = ability;
   }

   public AbilityCommand(SpellBill sb, Ent target) {
      this(sb.bSpell(), target);
   }

   public AbilityCommand(String saved, Snapshot snapshot) {
      this(loadSpell(saved, snapshot), loadTarget(saved, snapshot));
      if (saved.length() < 3) {
         throw new RuntimeException("Invalid SpellCommand: " + saved);
      } else {
         if (saved.length() > 3) {
            this.extraData = new ArrayList<>();

            for (char c : saved.substring(3).toCharArray()) {
               this.extraData.add(c - '0');
            }
         }
      }
   }

   @Override
   protected List<Integer> getExtraData() {
      return this.extraData;
   }

   @Override
   public Ent getSource() {
      return null;
   }

   @Override
   public void preEnact(Snapshot snapshot) {
      if (this.ability instanceof Tactic && this.extraData == null) {
         Tactic t = (Tactic)this.ability;
         this.extraData = t.getUsedHeroIndices(snapshot);
      }
   }

   @Override
   protected void postEnact(Snapshot snapshot) {
      super.postEnact(snapshot);
   }

   @Override
   protected boolean shouldSkipAnimation(Snapshot beforeShot) {
      return false;
   }

   @Override
   public String toSave(Snapshot previous) {
      List<TP<Ability, Boolean>> spells = SpellUtils.getAvailableSpells(previous);
      int spellIndex = -1;

      for (int i = 0; i < spells.size(); i++) {
         if (spells.get(i).a == this.ability) {
            spellIndex = i;
            break;
         }
      }

      List<Ent> entities = previous.getEntities(null, false);
      int targetIndex = entities.indexOf(this.target);
      if (targetIndex == -1) {
         targetIndex = Command.NULL_TARGET_INDEX;
      }

      if (spellIndex == -1) {
         spellIndex = Command.NULL_SPELL_INDEX;
      }

      String result = "3" + intToChar(spellIndex) + intToChar(targetIndex);
      if (this.extraData != null) {
         for (int ix = 0; ix < this.extraData.size(); ix++) {
            result = result + this.extraData.get(ix);
         }
      }

      return result;
   }

   private static Ent loadTarget(String saved, Snapshot snapshot) {
      int targetIndex = charToInt(saved.charAt(2));
      return targetIndex == NULL_TARGET_INDEX ? null : snapshot.getEntities(null, false).get(targetIndex);
   }

   private static Ability loadSpell(String saved, Snapshot snapshot) {
      int spellIndex = charToInt(saved.charAt(1));
      List<TP<Ability, Boolean>> spells = SpellUtils.getAvailableSpells(snapshot);
      return (Ability)spells.get(spellIndex).a;
   }

   @Override
   public boolean onRescue(Hero saved, Ent saver, Snapshot present, Snapshot prePresent) {
      Eff e = this.targetable.getDerivedEffects(prePresent);
      if (e.hasKeyword(Keyword.spellRescue)) {
         int cost = prePresent.getSpellCost((Spell)this.ability);
         present.untargetedUse(new EffBill().mana(cost).bEff(), null);
         present.addEvent(new ManaGainEvent(cost, Keyword.spellRescue.getColourTaggedString()));
         return true;
      } else {
         return super.onRescue(saved, saver, present, prePresent);
      }
   }

   public Ability getAbility() {
      return this.ability;
   }
}
