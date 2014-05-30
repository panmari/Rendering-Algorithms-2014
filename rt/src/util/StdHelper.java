package util;

import java.util.LinkedList;

public class StdHelper {

	
	private LinkedList<Float> values = new LinkedList<Float>();
	private LinkedList<Integer> costs = new LinkedList<Integer>();
	private float mean = 0;
	private float M2 = 0;
	
	private final int maxSize;
	private float meanCosts;
	
	/**
	 * A small standard deviation helper that accepts values through the update() method and computes the standard
	 * deviation of all values added. If maxSize of values were added, the first added value is removed from the std
	 * and the new one added. 
	 * @param maxSize
	 */
	public StdHelper(int maxSize) {
		this.maxSize = maxSize;
	}
		
	public float getVar() {
		//fixes division by zero
		if (values.size() > 1) 
			return Math.max(M2/(values.size() - 1), 0); //prevent nan if M2 is below zero
		else
			return 0;
	}
	
	/**
	 * The delta for shadow ray russian roulette
	 * @return
	 */
	public float getDelta() {
		float sqrDelta = Math.max(getVar()/meanCosts, 0);
		return MyMath.sqrt(sqrDelta);
	}
	
	public float getStd() {
		return MyMath.sqrt(getVar());
	}
	
	public void update(float newValue, int bounce) {
		assert !Float.isNaN(newValue);
		values.addLast(newValue);
		costs.addLast(bounce);
		if (values.size() > maxSize) {
			swapValues(values.pop(), newValue, costs.pop(), bounce);
		} else {
			addNewValue(newValue, bounce);
		}
		assert !Float.isNaN(mean);
	}
	
	private void swapValues(float oldValue, float newValue, int oldCost, int newCost) {
		float delta = newValue - oldValue;
		float dold = oldValue - mean;
		mean += delta/values.size();
		float dnew = newValue - mean;
		M2 += delta*(dold + dnew);
		float deltaCosts = newCost - oldCost;
		meanCosts += deltaCosts/costs.size();
	}
	
	private void addNewValue(float newValue, int newCost) {
		float delta = newValue - mean;
		mean += delta/values.size();
		M2 += delta*(newValue - mean);
		float costDelta = newCost - meanCosts;
		this.meanCosts += costDelta/costs.size();
		
	}
}
