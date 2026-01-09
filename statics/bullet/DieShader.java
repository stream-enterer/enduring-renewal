package com.tann.dice.statics.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;

public class DieShader implements Shader {
   public static final int TEX_3D = 1024;
   static ShaderProgram program;
   Camera camera;
   RenderContext context;
   int u_projTrans;
   int u_worldTrans;
   int side;
   int v_villagerColour;
   int v_values;
   int u_keyword;
   int u_face;
   int u_lapel;
   int v_size;
   int u_glow;
   int u_landedSide;
   int u_pulsate;
   int u_hsl;
   int u_pip;
   int u_pipBonus;
   static float[] floatBuffer = new float[12];
   static final Color[] keywordCols = Colours.palette;

   public void init() {
      String vert = Gdx.files.internal("shader/dice/vertex.glsl").readString();
      String frag = Gdx.files.internal("shader/dice/fragment.glsl").readString();
      program = new ShaderProgram(vert, frag);
      if (!program.isCompiled()) {
         TannLog.error("Shader err: " + program.getLog());
      } else {
         if (program.getLog().length() > 1) {
            TannLog.log(program.getLog());
         }

         this.u_projTrans = program.getUniformLocation("u_projViewTrans");
         this.u_worldTrans = program.getUniformLocation("u_worldTrans");
         this.side = program.getUniformLocation("side");
         this.v_villagerColour = program.getUniformLocation("v_villagerColour");
         this.v_values = program.getUniformLocation("v_values[0]");
         this.v_size = program.getUniformLocation("size");
         this.u_keyword = program.getUniformLocation("u_keyword[0]");
         this.u_face = program.getUniformLocation("u_face[0]");
         this.u_lapel = program.getUniformLocation("u_lapel");
         this.u_glow = program.getUniformLocation("u_glow");
         this.u_pulsate = program.getUniformLocation("u_pulsate");
         this.u_landedSide = program.getUniformLocation("u_landedSide");
         this.u_hsl = program.getUniformLocation("u_hsl[0]");
         this.u_pip = program.getUniformLocation("u_pip[0]");
         this.u_pipBonus = program.getUniformLocation("u_pipBonus[0]");
      }
   }

   public void dispose() {
      if (program != null) {
         program.dispose();
      }

      program = null;
   }

   public int compareTo(Shader other) {
      return 0;
   }

   public boolean canRender(Renderable instance) {
      return true;
   }

   public void begin(Camera camera, RenderContext context) {
      if (program.isCompiled()) {
         this.camera = camera;
         this.context = context;
         program.begin();
         program.setUniformMatrix(this.u_projTrans, camera.combined);
         Gdx.graphics.getGL20().glActiveTexture(33984);
         Images.side_sword.getTexture().bind(0);
         program.setUniformi("u_texture", 0);
         context.setDepthTest(515);
      }
   }

   public static int colIndex(Color col) {
      return Tann.indexOf(keywordCols, col);
   }

   public static Color indexCol(int bonusColIndex) {
      return keywordCols[bonusColIndex];
   }

   public static Color bonusColDisplay(int bonusColIndex) {
      if (bonusColIndex == -1) {
         return Colours.pink;
      } else {
         Color c = indexCol(bonusColIndex);
         return c == Colours.light ? getWhiteFlash() : c;
      }
   }

   public static Color getWhiteFlash() {
      return Colours.shiftedTowards(Colours.dark, Colours.light, getWhiteBonusPipsPulsateFactor() * 0.5F + 0.5F).cpy();
   }

   private static float getWhiteBonusPipsPulsateFactor() {
      return OptionUtils.isContinuous() ? com.tann.dice.Main.pulsateFactor() : -0.05F;
   }

   public void render(Renderable renderable) {
      if (program.isCompiled()) {
         Die d = (Die)renderable.userData;
         if (!d.flatDraw) {
            program.setUniformi(this.side, d.getSideIndex());
            program.setUniformf(this.v_villagerColour, d.getColour());
            program.setUniformMatrix(this.u_worldTrans, renderable.worldTransform);
            program.setUniformi(this.v_size, (int)d.getPixelSize());
            program.setUniformf(this.u_glow, d.getCantripFlash());
            program.setUniformi(this.u_landedSide, d.getSideIndex());
            program.setUniformf(this.u_pulsate, getWhiteBonusPipsPulsateFactor());
            float[] hslData = d.getHSLData();
            program.setUniform3fv(this.u_hsl, hslData, 0, hslData.length);
            d.getPipsAsFloats(floatBuffer);
            program.setUniform1fv(this.u_pip, floatBuffer, 0, 6);
            d.getPipBonus(floatBuffer);
            program.setUniform1fv(this.u_pipBonus, floatBuffer, 0, 12);
            float[] faceLocs = d.getFaceLocs();
            program.setUniform2fv(this.u_face, faceLocs, 0, faceLocs.length);
            Vector2 lapelLocs = d.getLapelLocs();
            program.setUniformf(this.u_lapel, lapelLocs);
            float[] keywordLocs = d.getKeywordLocs();
            program.setUniform4fv(this.u_keyword, keywordLocs, 0, keywordLocs.length);
            renderable.meshPart.render(program, true);
         }
      }
   }

   public void end() {
      if (program.isCompiled()) {
         program.end();
      }
   }
}
