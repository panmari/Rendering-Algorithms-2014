 package rt.intersectables;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;

/**
 * Defines a triangle by referring back to a {@link Mesh}
 * and its vertex and index arrays. 
 */
public class MeshTriangle implements Intersectable {

	private Mesh mesh;
	private int index;
	
	/**
	 * Make a triangle.
	 * 
	 * @param mesh the mesh storing the vertex and index arrays
	 * @param index the index of the triangle in the mesh
	 */
	public MeshTriangle(Mesh mesh, int index)
	{
		this.mesh = mesh;
		this.index = index;		
	}
	
	public HitRecord intersect(Ray r)
	{
		float vertices[] = mesh.vertices;
		
		// Access the triangle vertices as follows (same for the normals):		
		// 1. Get three vertex indices for triangle
		int v0 = mesh.indices[index*3];
		int v1 = mesh.indices[index*3+1];
		int v2 = mesh.indices[index*3+2];
		
		// 2. Access x,y,z coordinates for each vertex
		Point3f a = new Point3f(vertices[v0*3], vertices[v0*3 + 1], vertices[v0*3 + 2]);
		Point3f b = new Point3f(vertices[v1*3], vertices[v1*3 + 1], vertices[v1*3 + 2]);
		Point3f c = new Point3f(vertices[v2*3], vertices[v2*3 + 1], vertices[v2*3 + 2]);

		Vector3f col0 = new Vector3f();
		col0.sub(a, b);
		Vector3f col1 = new Vector3f();
		col1.sub(a, c);
		Matrix3f t = new Matrix3f();
		t.setColumn(0, col0);
		t.setColumn(1, col1);
		t.setColumn(2, r.direction);
		try {
			t.invert();
		} catch(SingularMatrixException e) {
			//handles sliver triangles etc...
			return null;
		}
		
		Vector3f betaGammaT = new Vector3f();
		betaGammaT.sub(a, r.origin);
		t.transform(betaGammaT);
		
		if (isInside(betaGammaT)) {
			float tHit = betaGammaT.z;
			Point3f position = r.pointAt(tHit);
			Vector3f normal = makeNormal(betaGammaT);
			Vector3f wIn = new Vector3f(r.direction);
			wIn.normalize();
			wIn.negate();
			return new HitRecord(tHit, position, normal, wIn, this, mesh.material, 0, 0);
		}
		else
			return null;
	}

	private Vector3f makeNormal(Vector3f betaGammaT) {
		float normals[] = mesh.normals;

		int v0 = mesh.indices[index*3];
		int v1 = mesh.indices[index*3+1];
		int v2 = mesh.indices[index*3+2];
		
		// 2. Access x,y,z coordinates for each vertex
		Vector3f n_a = new Vector3f(normals[v0*3], normals[v0*3 + 1], normals[v0*3 + 2]);
		Vector3f n_b = new Vector3f(normals[v1*3], normals[v1*3 + 1], normals[v1*3 + 2]);
		Vector3f n_c = new Vector3f(normals[v2*3], normals[v2*3 + 1], normals[v2*3 + 2]);
		n_a.scale(1 - betaGammaT.x - betaGammaT.y);
		n_b.scale(betaGammaT.x);
		n_c.scale(betaGammaT.y);
		Vector3f normal = new Vector3f(n_a);
		normal.add(n_b);
		normal.add(n_c);
		// this should not be needed
		// normal.normalize();
		return normal;
	}

	private boolean isInside(Vector3f betaGammaT) {
		if (betaGammaT.x < 0 || 
				betaGammaT.x > 1 ||
				betaGammaT.y < 0 || 
				betaGammaT.y > 1)
			return false;
		float f = betaGammaT.x + betaGammaT.y;
		return f > 0 && f < 1 ;
	}
	
}
