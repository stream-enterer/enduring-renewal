package com.tann.dice.statics.bullet;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Disposable;
import com.tann.dice.util.MemUtils;
import com.tann.dice.util.TannLog;

public class NoAutoDispose implements Disposable {
   public btDefaultCollisionConfiguration defaultCollisionConfiguration = new btDefaultCollisionConfiguration();
   public btCollisionDispatcher collisionDispatcher = new btCollisionDispatcher(this.defaultCollisionConfiguration);
   public btDbvtBroadphase dbvtBroadphase = new btDbvtBroadphase();
   public btConstraintSolver constraintSolver = new btSequentialImpulseConstraintSolver();
   public btDiscreteDynamicsWorld dynamicsWorld = new btDiscreteDynamicsWorld(
      this.collisionDispatcher, this.dbvtBroadphase, this.constraintSolver, this.defaultCollisionConfiguration
   );

   public NoAutoDispose() {
      this.dynamicsWorld.setGravity(new Vector3(0.0F, -55.0F, 0.0F));
   }

   public boolean invalid() {
      return this.constraintSolver == null
         || this.defaultCollisionConfiguration == null
         || this.collisionDispatcher == null
         || this.dbvtBroadphase == null
         || this.dynamicsWorld == null
         || this.dynamicsWorld.isDisposed();
   }

   public void dispose() {
      if (this.dynamicsWorld != null && !this.dynamicsWorld.isDisposed()) {
         MemUtils.disp(this.dynamicsWorld, this.defaultCollisionConfiguration, this.collisionDispatcher, this.dbvtBroadphase, this.constraintSolver);
         this.dynamicsWorld = null;
         this.defaultCollisionConfiguration = null;
         this.collisionDispatcher = null;
         this.dbvtBroadphase = null;
         this.constraintSolver = null;
      } else {
         TannLog.error("multidispose");
      }
   }
}
