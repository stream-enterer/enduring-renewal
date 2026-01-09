package com.tann.dice.gameplay.trigger.global.changeHero;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.ChangeHeroEffect;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalChangeHeroPos extends Global {
   protected final HeroPosition heroPosition;
   final ChangeHeroEffect changeHeroEffect;

   public GlobalChangeHeroPos(HeroPosition heroPosition, ChangeHeroEffect changeHeroEffect) {
      this.heroPosition = heroPosition;
      this.changeHeroEffect = changeHeroEffect;
   }

   @Override
   public void onPick(DungeonContext context) {
      List<Hero> targets;
      if (this.heroPosition != null) {
         targets = this.heroPosition.getFromPosition(context.getParty().getHeroes());
      } else {
         targets = context.getParty().getHeroes();
      }

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
      if (other == null) {
         return null;
      } else {
         return (Actor)(this.heroPosition == null
            ? new Pixl(0, 2).border(Colours.green).actor(other).pix()
            : DipPanel.makeSidePanelGroup(this.heroPosition.makeActor(), other, Colours.green));
      }
   }

   @Override
   public String describeForSelfBuff() {
      return this.changeHeroEffect.describeForSelfBuff(this.heroPosition);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.heroPosition.getCollisionBit() | this.changeHeroEffect.getCollisionBit(this.heroPosition) | Collision.NUM_HEROES;
   }

   public ChangeHeroEffect getChangeHeroEffect() {
      return this.changeHeroEffect;
   }

   @Override
   public boolean isOnPick() {
      return true;
   }
}
