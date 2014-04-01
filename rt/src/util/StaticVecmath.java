package util;

import javax.vecmath.*;

public class StaticVecmath {

	public static float dist2(Tuple3f v1, Tuple3f v2)
	{
		Vector3f tmp = new Vector3f(v1);
		tmp.sub(v2);
		return tmp.lengthSquared();
	}
	
	public static Vector3f sub(Tuple3f v1, Tuple3f v2)
	{
		Vector3f r = new Vector3f(v1);
		r.sub(v2);
		return r;
	}
	
	public static Vector3f negate(Vector3f v)
	{
		Vector3f r = new Vector3f(v);
		r.negate();
		return r;
	}
	
	public static float getDimension(Tuple3f tuple, Axis dimension) {
		return dimension.normal.x * tuple.x + 
				dimension.normal.y * tuple.y + 
				dimension.normal.z * tuple.z;
	}
	
	public static enum Axis{
		x(new Vector3f(1, 0, 0)), 
		y(new Vector3f(0, 1, 0)), 
		z(new Vector3f(0, 0, 1));
		
		private final Vector3f normal;

		Axis(Vector3f normal){
			this.normal = normal;
		}
		
		public Vector3f getNormal() {
			return normal;
		}
		
		public Axis getNext() {
			int ordinalNext = (this.ordinal() + 1) % Axis.values().length;
			return Axis.values()[ordinalNext];
		}
	}

	/**
	 * sets min to the minimum of min and other (on a per element basis
	 * @param min
	 * @param other
	 */
	public static void elementwiseMin(Tuple3f min, Tuple3f other) {
		min.x = Math.min(min.x, other.x);
		min.y = Math.min(min.y, other.y);
		min.z = Math.min(min.z, other.z);
	}

	/**
	 * sets max to the maximum of max and other (on a per element basis
	 * @param max
	 * @param other
	 */
	public static void elementwiseMax(Tuple3f max, Tuple3f other) {
		max.x = Math.max(max.x, other.x);
		max.y = Math.max(max.y, other.y);
		max.z = Math.max(max.z, other.z);
	}
	
	/**
	 * Reflects the vector incoming at the normal given. 
	 * Assumes incoming vector is pointing away from surface.
	 * @param normal
	 * @param incoming
	 * @return
	 */
	public static Vector3f reflect(Vector3f normal, Vector3f incoming) {
		assert Math.abs(normal.length() - 1) < 1e-5f: "Normal not normalized: " + normal.length();
		
		float cosTheta_i = incoming.dot(normal);
		Vector3f reflected = new Vector3f();
		reflected.negate(incoming);
		Vector3f nScaled = new Vector3f(normal);
		nScaled.scale(2*cosTheta_i);
		reflected.add(nScaled);
		return reflected;
	}
}
