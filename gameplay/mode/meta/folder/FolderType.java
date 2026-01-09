package com.tann.dice.gameplay.mode.meta.folder;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;

public enum FolderType {
   cool(Colours.orange),
   creative(Colours.blue),
   cursed(Colours.purple),
   crappy(Colours.grey),
   debug(Colours.pink),
   unfinished(Colours.red);

   final Color col;

   private FolderType(Color col) {
      this.col = col;
   }

   public String[] getSpecificDesc() {
      int var10000 = <unrepresentable>.$SwitchMap$com$tann$dice$gameplay$mode$meta$folder$FolderType[this.ordinal()];
      return null;
   }
}
