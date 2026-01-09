package com.tann.dice.gameplay.trigger.global.spell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalLearnSpell extends Global {
   final Spell spell;

   public GlobalLearnSpell(Spell spell) {
      this.spell = spell;
   }

   public GlobalLearnSpell(SpellBill sb) {
      this(sb.bSpell());
   }

   @Override
   public Spell getGlobalSpell() {
      return this.spell;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(1);
      p.actor(new Explanel(this.spell, false));
      return p.pix();
   }

   @Override
   public String describeForSelfBuff() {
      return "Learn a new [blue]spell[cu]";
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      return this.spell.getBaseEffect().getReferencedKeywords();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }

   @Override
   public Eff getSingleEffOrNull() {
      return this.spell.getBaseEffect();
   }
}
