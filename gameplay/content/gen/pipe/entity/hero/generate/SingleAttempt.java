package com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import java.util.List;
import java.util.Random;

public interface SingleAttempt {
   EntSide getRandomSide(Random var1, HTBill var2, List<EntSide> var3, float var4, HeroType var5, int var6);
}
