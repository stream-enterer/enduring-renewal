package com.tann.dice.screens.dungeon.panels.book.page.stuffPage;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.util.Colours;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum PipeType {
   Modifier(Colours.purple, new ArrayList<>(PipeMod.pipes)),
   Hero(Colours.yellow, new ArrayList<>(PipeHero.pipes)),
   Monster(Colours.orange, new ArrayList<>(PipeMonster.pipes)),
   Item(Colours.grey, new ArrayList<>(PipeItem.pipes));

   final Color col;
   public final List<Pipe> contents;

   private PipeType(Color col, List<Pipe> contents) {
      this.col = col;
      this.contents = contents;
      Collections.sort(contents, new Comparator<Pipe>() {
         public int compare(Pipe o1, Pipe o2) {
            return Boolean.compare(o1.isTexturey(), o2.isTexturey());
         }
      });
   }

   public Pipe getDefaultShownPipe() {
      return this.contents.get(3);
   }
}
