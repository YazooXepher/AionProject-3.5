package com.aionemu.gameserver.geoEngine.math;

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;

public abstract class AbstractTriangle implements Collidable {

	public abstract Vector3f get1();

	public abstract Vector3f get2();

	public abstract Vector3f get3();

	public abstract void set(Vector3f v1, Vector3f v2, Vector3f v3);

	public int collideWith(Collidable other, CollisionResults results) {
		return other.collideWith(this, results);
	}
}