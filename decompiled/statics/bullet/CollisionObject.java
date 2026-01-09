package com.tann.dice.statics.bullet;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;

public class CollisionObject extends ModelInstance implements Disposable {
   public final btRigidBody body;
   private Vector3 localInertia = new Vector3();
   private btCollisionShape disposeMe;
   public Vector3 center = new Vector3();
   public final Vector3 dimensions = new Vector3();
   public float radius = 0.0F;
   BoundingBox box = new BoundingBox();
   private static final float LINEAR_THRESHOLD = 0.3F;
   private static final float ANGULAR_THRESHOLD = 5.01F;

   public CollisionObject(Model model, String node, btCollisionShape shape, float mass, float friction) {
      super(model, new String[]{node});
      this.disposeMe = shape;
      if (mass > 0.0F) {
         shape.calculateLocalInertia(mass, this.localInertia);
      } else {
         this.localInertia.set(0.0F, 0.0F, 0.0F);
      }

      btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(mass, null, shape, this.localInertia);
      this.body = new btRigidBody(info);
      info.dispose();
      this.body.setRestitution(0.68F);
      this.body.setFriction(friction);
      this.body.setCollisionShape(shape);
      this.initialUpdate();
   }

   public void dispose() {
      this.body.dispose();
      this.disposeMe.dispose();
   }

   public void updateBounds() {
      this.calculateBoundingBox(this.box);
      this.box.getCenter(this.center);
      this.box.getDimensions(this.dimensions);
      this.radius = this.dimensions.x;
   }

   public boolean isMoving() {
      return this.body.getLinearVelocity().len() > 0.3F || this.body.getAngularVelocity().len() > 5.01F;
   }

   public void initialUpdate() {
      this.body.setWorldTransform(this.transform);
   }

   public void update() {
      this.body.getWorldTransform(this.transform);
   }
}
