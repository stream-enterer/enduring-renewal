package com.tann.dice.gameplay.trigger.global.changeHero.effects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;

public class LevelupHero extends ChangeHeroEffect {
   final int delta;

   public LevelupHero(int by) {
      this.delta = by;
   }

   @Override
   public void affectHero(DungeonContext dc, Hero h) {
      HeroType option = HeroTypeUtils.getBasicLevelupOption(new ArrayList<>(), h.getHeroType(), dc, new ArrayList<>(), h.getLevel() + this.delta);
      if (option == null) {
         TannLog.log("Failed to level up " + h);
      } else {
         h.levelUpTo(option, dc);
      }
   }

   @Override
   public String describeForSelfBuff(HeroPosition heroPosition) {
      return "Level-up the " + heroPosition.describe().toLowerCase() + " by " + this.delta;
   }

   @Override
   public Actor makePanelActor(boolean big) {
      return new TextWriter("[green]level " + Tann.delta(this.delta));
   }

   @Override
   public long getCollisionBit(HeroPosition heroPosition) {
      return Collision.LEVELUP_REWARD;
   }
}
