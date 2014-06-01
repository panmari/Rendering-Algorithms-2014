package rt.intersectables;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import rt.Intersectable;
import rt.accelerators.BoundingBox;

public class PerforatedMesh extends Aggregate {

	private Mesh m;
	boolean[] isHole;
	
	public PerforatedMesh(Mesh m, float perforatedness)
	{
		this.m = m;
		isHole = new boolean[m.triangles.length];
		Random rnd = new Random(42);
		for (int i = 0; i < m.triangles.length; i++) {
			if(rnd.nextFloat() < perforatedness)
				isHole[i] = true;
		}
	}
	
	public PerforatedMesh(Mesh m, int intervall)
	{
		this.m = m;
		isHole = new boolean[m.triangles.length];
		for (int i = 0; i < m.triangles.length; i++) {
			if(i % intervall == 0)
				isHole[i] = true;
		}
	}
	
	public Iterator<Intersectable> iterator() {
		return new PerforatedMeshIterator(m.triangles);
	}
	
	private class PerforatedMeshIterator implements Iterator<Intersectable>
	{
		private int i;
		private MeshTriangle[] triangles;
		private MeshTriangle next;
		
		public PerforatedMeshIterator(MeshTriangle[] triangles)
		{
			this.triangles = triangles;
			i = 0;
		}
		
		public boolean hasNext()
		{
			if (next == null) {
				while (i < triangles.length && isHole[i]) {
					i++;
				}
				if (i >= triangles.length)
					return false;
				next = triangles[i];
			}
			return next != null;
		}
		
		public MeshTriangle next()
		{
			if (hasNext()) {
				MeshTriangle returned = next;
				i++;
				next = null;
				return returned;
			} else 
				throw new NoSuchElementException();
		}
		
		public void remove()
		{
		}
	}

	@Override
	public int size() {
		return m.triangles.length;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return m.getBoundingBox();
	}
		
}
