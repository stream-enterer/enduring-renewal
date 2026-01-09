package com.tann.dice.gameplay.trigger.global.changeHero.effects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;

public abstract class ChangeHeroEffect {
   public abstract void affectHero(DungeonContext var1, Hero var2);

   public abstract String describeForSelfBuff(HeroPosition var1);

   public abstract Actor makePanelActor(boolean var1);

   public boolean needsReset() {
      return false;
   }

   public long getCollisionBit(HeroPosition heroPosition) {
      return 0L;
   }
}
