package com.tann.dice.screens.dungeon.panels.book.views;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;

public abstract class EntityLedgerView extends Group {
   public static Group getLV(EntType et) {
      return et instanceof HeroType ? new HeroLedgerView((HeroType)et, true).basicListener() : new MonsterLedgerView((MonsterType)et, false).basicListener();
   }

   public abstract EntityLedgerView basicListener();
}
