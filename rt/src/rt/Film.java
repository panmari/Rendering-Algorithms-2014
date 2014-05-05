package rt;

/**
 * A film stores a 2D grid of {@link rt.Spectrum} representing an image.
 * Rendered samples can be added one by one to a film. Samples are
 * filtered using some filter (depending on the implementation of this 
 * interface) when added.
 */
public interface Film {
	
	/**
	 * Add a sample to the film at a specified floating point position. The position
	 * coordinates are assumed to be in image space.
	 * 
	 * @param x x-coordinate in image space 
	 * @param y y-coordinate in image space
	 * @param s sample to be added
	 */
	public void addSample(float x, float y, Spectrum s);
	
	/**
	 * Returns the image stored in the film.
	 * Careful, this is more than a getter and should be called as few as possible.
	 * @return the image
	 */
	public Spectrum[][] makeImage();
	
	/**
	 * Returns width (in pixels) of film.
	 * 
	 * @return width in pixels
	 */
	public int getWidth();
	
	/**
	 * Returns height (in pixels) of film.
	 * 
	 * @return height in pixels
	 */
	public int getHeight();
	
	/**
	 * Adds another Film to this films
	 * @param f Film to be added
	 */
	public void addImage(Film f);

}
