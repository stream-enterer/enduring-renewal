package com.tann.dice.gameplay.save;

import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import java.util.ArrayList;
import java.util.List;

public class PartyData {
   public List<String> h;
   public List<String> e = new ArrayList<>();
   public PartyLayoutType plt;

   public PartyData(List<String> heroData, List<String> extraItems, PartyLayoutType plt) {
      this.h = heroData;
      this.e = extraItems;
      this.plt = plt;
   }

   public PartyData() {
   }
}
