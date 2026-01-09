package com.tann.dice.screens.shaderFx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.screens.dungeon.panels.threeD.Actor3d;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import java.util.HashMap;
import java.util.Map;

public abstract class FXContainer extends Group {
   protected ShaderProgram program;
   protected final Actor group;
   protected Vector2 originalPosition;
   protected float random;
   private static Map<ShaderFolder, ShaderProgram> cachedShaders = new HashMap<>();

   public FXContainer(Actor group) {
      this.setTransform(false);
      this.group = group;
      this.originalPosition = Tann.getAbsoluteCoordinates(group).cpy();
      ShaderProgram.pedantic = false;
      this.random = Tann.random();
   }

   public static FXContainer randomFx(Actor a) {
      int r = Tann.randomInt(5);
      switch (r) {
         case 0:
         default:
            return new FXBurn(a);
         case 1:
            return new FXAcid(a);
         case 2:
            return new FXCut(a, 1, 0.2F, 0.35F, 20.0F, 0.5F, (float)(0.5 + (Math.random() * 2.0 - 1.0) * 0.4F));
         case 3:
            return new FXEllipse(a);
         case 4:
            return new FXSingularity(a);
      }
   }

   protected void loadShader(ShaderFolder folder) {
      this.program = fetchShaderCached(folder);
   }

   public void act(float delta) {
      super.act(delta);
      this.group.act(delta);
   }

   public void replace() {
      if (this.group instanceof Group) {
         for (Actor3d a3 : TannStageUtils.getActorsWithClass(Actor3d.class, (Group)this.group)) {
            a3.remove();
         }
      }

      Group parent = this.group.getParent();
      if (parent != null) {
         this.group.remove();
         parent.addActor(this);
      }
   }

   protected void setupNoise(ShaderProgram program) {
      TextureRegion n = Images.noise_packed;
      float atlasWidth = n.getTexture().getWidth();
      float atlasHeight = n.getTexture().getHeight();
      program.setUniformf(
         "u_noiseBounds", n.getRegionX() / atlasWidth, n.getRegionY() / atlasHeight, n.getRegionWidth() / atlasWidth, n.getRegionHeight() / atlasHeight
      );
   }

   public abstract float getDuration();

   private static ShaderProgram fetchShaderCached(ShaderFolder folderName) {
      ShaderProgram cached = cachedShaders.get(folderName);
      if (cached == null) {
         String vert = Gdx.files.internal("shader/fx/" + folderName + "/vertex.glsl").readString();
         String frag = Gdx.files.internal("shader/fx/" + folderName + "/fragment.glsl").readString();
         cached = new ShaderProgram(vert, frag);
         if (!cached.isCompiled()) {
            System.out.println(cached.getLog());
            cached = SpriteBatch.createDefaultShader();
         }

         cachedShaders.put(folderName, cached);
      }

      return cached;
   }

   public static void clearCaches() {
      cachedShaders = new HashMap<>();
   }

   public static void loadAllShaders() {
      for (ShaderFolder sf : ShaderFolder.values()) {
         fetchShaderCached(sf);
      }
   }
}
