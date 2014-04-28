package rt.integrators;

import java.util.Iterator;

public class RussianRouletteIterator implements Iterator<Float>{

	int i = 0;
	private float[] floats;
	private float last;
	
	public RussianRouletteIterator(float... floats) {
		this.floats = floats;
		this.last = floats[floats.length - 1];
	}
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Float next() {
		if (i + 1 < floats.length) {
			float next = floats[i];
			i++;
			return next;
		} else
			return last;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Not supported.");
	}
	
}
