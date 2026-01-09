package com.tann.dice.gameplay.content.ent.type.blob.monster;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import java.util.ArrayList;
import java.util.List;

public class MonsterTypeBlob {
   public static List<MonsterType> makeAll() {
      List<MonsterType> result = new ArrayList<>();
      result.addAll(MonsterTypeBlobBasic.make());
      return result;
   }
}
