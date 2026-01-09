package com.tann.dice.gameplay.trigger.global;

import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.util.Tann;
import java.util.Arrays;

public class GlobalWishEnabled extends Global {
   @Override
   public String describeForSelfBuff() {
      return Tann.join("[n][text]- ", Arrays.asList(Mode.WISH.getDescriptionLines()));
   }

   @Override
   public boolean canWish() {
      return true;
   }
}
