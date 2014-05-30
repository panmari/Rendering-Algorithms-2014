package rt.cameras;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import rt.Camera;
import rt.Ray;
import util.MyMath;
import util.StaticVecmath;

public class ThinLensCamera extends PinholeCamera implements Camera {

	Matrix4f m = new Matrix4f();
	Vector3f eye;
	private float focalLength;
	private float aperture;
	
	public ThinLensCamera(Vector3f eye, Vector3f lookAt, Vector3f up, float fov,
			float aspect, int width, int height, float focalLength, float aperture) {
		super(eye, lookAt, up, fov, aspect, width, height);
		this.focalLength = focalLength;
		this.aperture = aperture;
	}

	@Override
	public Ray makeWorldSpaceRay(int i, int j, float[] samples) {
		Ray r = super.makeWorldSpaceRay(i, j, samples);
		Point3f pointOnImagePlane = r.pointAt(focalLength);
		Point2f onCircleOfConfusion = MyMath.sampleUnitCircle(samples);
		onCircleOfConfusion.scale(aperture);
		Point3f newOrigin = new Point3f(r.origin);
		newOrigin.x += onCircleOfConfusion.x;
		newOrigin.y += onCircleOfConfusion.y;
		Vector3f newDir = StaticVecmath.sub(pointOnImagePlane, newOrigin);
		newDir.normalize();
		return new Ray(newOrigin, newDir, r.t);
	}

}
