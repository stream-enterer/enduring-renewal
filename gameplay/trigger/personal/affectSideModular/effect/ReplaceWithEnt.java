package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import java.util.ArrayList;
import java.util.List;

public class ReplaceWithEnt extends ReplaceWith {
   EntType entType;

   public ReplaceWithEnt(EntType entType) {
      super(makeSides(entType));
      this.entType = entType;
   }

   private static EntSide[] makeSides(EntType entType) {
      List<EntSideState> states = entType.makeEnt().getBlankState().getAllSideStates();
      EntSide[] s = new EntSide[6];

      for (int i = 0; i < states.size(); i++) {
         EntSideState state = states.get(i);
         s[i] = new EntSide(state.getCalculatedTexture(), state.getCalculatedEffect(), entType.size, state.getHsl());
      }

      EntType.realToNice(s);
      return s;
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      return new ArrayList<>();
   }

   @Override
   public String describe() {
      return this.entType.getName(true) + "'s sides";
   }
}
