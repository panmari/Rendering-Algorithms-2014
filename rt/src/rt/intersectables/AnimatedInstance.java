package rt.intersectables;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;

public class AnimatedInstance implements Intersectable {

	private Intersectable intersectable;
	public Material material;
	private Quat4f rotMotion;
	private Vector3f transMotion;

	/**
	 * Only supports linear movement at the time of writing...
	 * @param i
	 * @param t
	 * @param movement
	 */
	public AnimatedInstance(Intersectable i, Matrix4f motion) {
		this.intersectable = i;
		this.rotMotion = new Quat4f();
		motion.get(rotMotion);
		this.transMotion = new Vector3f(motion.getM03(), motion.getM13(), motion.getM23());
		this.material = new Diffuse(); //default material
	}

	@Override
	public HitRecord intersect(Ray r) {
		Quat4f q = new Quat4f(rotMotion);
		q.scale(r.t);
		Vector3f m = new Vector3f(transMotion);
		m.scale(r.t);
		Matrix4f mot = new Matrix4f(q, m, 1);
		InstanceHelper ih = new InstanceHelper(mot);
		
		Ray instanceRay = ih.transform(r);
		HitRecord instanceHitRecord = intersectable.intersect(instanceRay);
		if (instanceHitRecord == null)
			return null;
		
		HitRecord h = ih.transformBack(instanceHitRecord);
		h.material = this.material;
		return h;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return BoundingBox.INFINITE_BOUNDING_BOX;
	}
}
