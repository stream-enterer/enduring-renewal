package com.tann.dice.gameplay.content.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceItem;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnAbility;
import com.tann.dice.statics.ImageUtils;
import java.util.ArrayList;
import java.util.List;

public class ItBill {
   private final String name;
   private final TextureRegion image;
   private final int tier;
   private List<Personal> personals = new ArrayList<>();
   private boolean hidden;

   public static ItBill make(int tier, String name, TextureRegion override, String defaultString) {
      return override != null ? new ItBill(tier, name, override) : new ItBill(tier, name, defaultString);
   }

   public static ItBill make(int tier, String name, Item src, String defaultString) {
      return DataSourceItem.hasPTR(src) ? new ItBill(tier, name, src.getImage()) : new ItBill(tier, name, defaultString);
   }

   public static ItBill make(int tier, String name, Item src, Item src2, String defaultString) {
      if (DataSourceItem.hasPTR(src)) {
         return new ItBill(tier, name, src.getImage());
      } else {
         return DataSourceItem.hasPTR(src2) ? new ItBill(tier, name, src2.getImage()) : new ItBill(tier, name, defaultString);
      }
   }

   public ItBill(int tier, String name, TextureRegion img) {
      this.tier = tier;
      this.name = name;
      this.image = img;
      if (img instanceof AtlasRegion) {
         String var4 = ((AtlasRegion)img).name;
      }
   }

   public ItBill(int tier, String name, String imgPath) {
      this(tier, name, ImageUtils.loadExt("item/" + imgPath));
   }

   public ItBill(int tier, String name) {
      this(tier, name, getImageFromName(name));
   }

   public ItBill(String name) {
      this(-69, name, getImageFromName(name));
   }

   public ItBill(int tier, SpellBill sb) {
      this(tier, sb.bSpell());
   }

   public ItBill(int tier, Ability ability) {
      this(tier, "Learn " + ability.getTitle(), ability.getImage());
      this.prs(LearnAbility.make(ability));
   }

   public Item bItem() {
      return new Item(this.tier, this.name, this.image, this.personals, this.makeDescription(), this.hidden);
   }

   private String makeDescription() {
      return Trigger.describeTriggers(this.personals);
   }

   public ItBill prs(List<Personal> personals) {
      this.personals.addAll(personals);
      return this;
   }

   public ItBill prs(AffectSideEffect ase) {
      return this.prs(new AffectSides(ase));
   }

   public ItBill prs(AffectSideCondition asc, AffectSideEffect ase) {
      return this.prs(new AffectSides(asc, ase));
   }

   public ItBill prs(SpecificSidesType sst, AffectSideEffect ase) {
      return this.prs(new SpecificSidesCondition(sst), ase);
   }

   public ItBill prs(Personal t) {
      this.personals.add(t);
      return this;
   }

   public ItBill hidden() {
      this.hidden = true;
      return this;
   }

   private static TextureRegion getImageFromName(String name) {
      String transformedName = name.replaceAll("[ ':]", "-").toLowerCase();
      String path = "item/" + transformedName;
      TextureRegion result = ImageUtils.loadExtNull(path);
      return result != null ? result : ImageUtils.loadExt("item/placeholder");
   }

   public List<Personal> getPersonals() {
      return this.personals;
   }
}
