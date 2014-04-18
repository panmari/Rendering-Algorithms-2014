package util;

import java.util.LinkedList;

public class StdHelper {

	
	private LinkedList<Float> values = new LinkedList<Float>();
	private float mean = 0;
	private float M2 = 0;
	
	private final int maxSize;
	
	/**
	 * A small standard deviation helper that accepts values through the update() method and computes the standard
	 * deviation of all values added. If maxSize of values were added, the first added value is removed from the std
	 * and the new one added. 
	 * @param maxSize
	 */
	public StdHelper(int maxSize) {
		this.maxSize = maxSize;
	}
		
	public float getStd() {
		if (values.size() > 1) //fixes division by zero
			return MyMath.sqrt(M2/(values.size() - 1)); 
		else
			return 0;
	}
	
	
	public void update(float newValue) {
		values.addLast(newValue);
		if (values.size() > maxSize) {
			float oldValue = values.pop();
			swapValues(oldValue, newValue);

		} else {
			addNewValue(newValue);
		}
	}
	
	private void swapValues(float oldValue, float newValue) {
		float delta = newValue - oldValue;
		float dold = oldValue - mean;
		mean += delta/values.size();
		float dnew = newValue - mean;
		M2 += delta*(dold + dnew);
	}
	
	private void addNewValue(float newValue) {
		float delta = newValue - mean;
		mean += delta/values.size();
		M2 += delta*(newValue - mean);
	}
}
