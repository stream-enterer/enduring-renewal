package com.tann.dice.gameplay.trigger.global.changeHero;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.ChangeHeroEffect;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalChangeHeroAll extends Global {
   final ChangeHeroEffect changeHeroEffect;

   public GlobalChangeHeroAll(ChangeHeroEffect changeHeroEffect) {
      this.changeHeroEffect = changeHeroEffect;
   }

   @Override
   public void onPick(DungeonContext context) {
      List<Hero> targets = context.getParty().getHeroes();

      for (int i = targets.size() - 1; i >= 0; i--) {
         this.changeHeroEffect.affectHero(context, targets.get(i));
      }

      if (this.changeHeroEffect.needsReset()) {
         this.resetFightLogOnPick();
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor other = this.changeHeroEffect.makePanelActor(big);
      return other == null ? null : new Pixl(0, 2).border(Colours.green).actor(other).pix();
   }

   @Override
   public String describeForSelfBuff() {
      return this.changeHeroEffect.describeForSelfBuff(HeroPosition.TOP).replaceAll("The top hero", "All heroes").replaceAll("top hero", "all heroes");
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.HERO_POSITION;
   }

   public ChangeHeroEffect getChangeHeroEffect() {
      return this.changeHeroEffect;
   }

   @Override
   public boolean isOnPick() {
      return true;
   }
}
