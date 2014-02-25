package rt;

import java.util.ArrayList;
import java.util.Iterator;

import rt.intersectables.Aggregate;

public class IntersectableList extends Aggregate {
	
	private ArrayList<Intersectable> intersectibles = new ArrayList<>();
	
	public boolean add(Intersectable i) {
		return intersectibles.add(i);
		
	}
	
	@Override
	public Iterator<Intersectable> iterator() {
		return intersectibles.iterator();
	}

}
