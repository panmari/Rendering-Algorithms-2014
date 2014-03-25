package rt;

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
		switch (dimension) {
    	case x:
    		return tuple.x;
    	case y:
    		return tuple.y;
    	default:
    		return tuple.z;
    	}
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
}
