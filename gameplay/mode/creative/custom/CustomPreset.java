package com.tann.dice.gameplay.mode.creative.custom;

import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import java.util.Arrays;
import java.util.List;

public class CustomPreset {
   private String title;
   private List<String> modifiers;

   public CustomPreset() {
   }

   public CustomPreset(String title, String... modifiers) {
      this(Arrays.asList(modifiers), title);
   }

   public CustomPreset(List<String> modifiers, String title) {
      this.title = title;
      this.modifiers = modifiers;
   }

   public CustomPreset(String title, Modifier... modifiers) {
      this(title, Arrays.asList(modifiers));
   }

   public CustomPreset(String title, List<Modifier> modifiers) {
      this(ModifierLib.serialiseToStringList(modifiers), title);
   }

   public String getTitle() {
      return this.title;
   }

   public List<String> getContent() {
      return this.modifiers;
   }

   public List<Modifier> getContentAsModifiers() {
      return ModifierUtils.deserialiseList(this.modifiers);
   }

   public static List<CustomPreset> getDefault() {
      return Arrays.asList();
   }
}
