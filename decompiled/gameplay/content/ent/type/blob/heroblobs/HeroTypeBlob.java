package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import java.util.ArrayList;
import java.util.List;

public class HeroTypeBlob {
   public static List<HeroType> makeDesigned() {
      List<HeroType> result = new ArrayList<>();
      result.addAll(HeroTypeBlobYellow.makeDesigned());
      result.addAll(HeroTypeBlobOrange.makeDesigned());
      result.addAll(HeroTypeBlobGrey.makeDesigned());
      result.addAll(HeroTypeBlobRed.makeDesigned());
      result.addAll(HeroTypeBlobBlue.makeDesigned());
      result.addAll(HeroTypeBlobGreen.makeDesigned());
      return result;
   }
}
