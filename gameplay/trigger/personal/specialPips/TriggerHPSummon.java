package com.tann.dice.gameplay.trigger.personal.specialPips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.event.entState.LinkEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.Arrays;

public class TriggerHPSummon extends TriggerHP {
   public static final Color SUMMON_COL = Colours.light;
   public static final String SUMMON_TAG = TextWriter.getTag(SUMMON_COL);
   public static final TextureRegion SUMMON_IMAGE = Images.hp_plus;

   public TriggerHPSummon(String monsterType, int amt, SoundSnapshotEvent soundSnapshotEvent, PipLoc loc) {
      super(new EffBill().summon(monsterType, amt).bEff(), Arrays.asList(new LinkEvent(soundSnapshotEvent)), null, SUMMON_IMAGE, SUMMON_COL, loc);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      float totalSummonStrength = MonsterTypeLib.byName(this.eff.getSummonType()).getSummonValue() * this.getPips(type).length;
      return total + totalSummonStrength * 0.1F;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      float totalSummonHp = MonsterTypeLib.byName(this.eff.getSummonType()).getEffectiveHp() * this.getPips(entType).length;
      return hp + totalSummonHp * 0.55F;
   }
}
