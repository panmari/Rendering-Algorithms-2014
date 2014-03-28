package rt.lightsources;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.LightGeometry;
import rt.Ray;
import rt.Spectrum;
import rt.accelerators.BoundingBox;
import rt.intersectables.Rectangle;
import rt.materials.AreaLightMaterial;

/**
 * Implements a point light using a {@link rt.materials.PointLightMaterial}.
 */
public class AreaLight implements LightGeometry {

	private final AreaLightMaterial areaLightMaterial;
	private final Vector3f edge1;
	private final Vector3f edge2;
	private final Point3f lightPos;
	private final float area;
	private Vector3f normal;
	private Rectangle rectangle;
	
	/**
	 * 
	 * @param lightPos, the bottom left of this light
	 * @param edge1
	 * @param edge2
	 * @param emission
	 */
	public AreaLight(Point3f lightPos, Vector3f edge1, Vector3f edge2, Spectrum emission)
	{
		this.lightPos = lightPos;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.normal = new Vector3f();
		normal.cross(edge1, edge2);
		this.area = normal.length();
		normal.normalize();
		this.areaLightMaterial = new AreaLightMaterial(emission, area);
		this.rectangle = new Rectangle(new Point3f(lightPos), new Vector3f(edge1), new Vector3f(edge2));
	}
	
	public HitRecord intersect(Ray r) {
		HitRecord h = rectangle.intersect(r);
		if (h == null)
			return null;
		h.intersectable = this;
		h.material = this.areaLightMaterial;
		return h;
	}

	/**
	 * Sample a point on the light geometry.
	 */
	public HitRecord sample(float[] s) {
		HitRecord hitRecord = new HitRecord();
		Vector3f edge1Sampled = new Vector3f(edge1);
		edge1Sampled.scale(s[0]);
		Vector3f edge2Sampled = new Vector3f(edge2);
		edge1Sampled.scale(s[1]);
		Point3f position = new Point3f(this.lightPos);
		position.add(edge1Sampled);
		position.add(edge2Sampled);
		hitRecord.position = position;
		hitRecord.material = areaLightMaterial;
		hitRecord.normal = new Vector3f(this.normal);
		hitRecord.p = 1.f/area;
		return hitRecord;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}

}
