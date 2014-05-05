package rt.films;

import rt.Film;
import rt.Spectrum;

/**
 * Uses a box filter when accumulating samples on a film. A box filter means
 * that samples contribute only to the pixel that they lie in. Sample values
 * are simply averaged.
 */
public class BoxFilterFilm implements Film {
	
	private Spectrum[][] image;
	public int width, height;
	private Spectrum[][] unnormalized;
	private float nSamples[][];
	
	public BoxFilterFilm(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new Spectrum[width][height];
		unnormalized = new Spectrum[width][height];
		nSamples = new float[width][height];
		
		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				image[i][j] = new Spectrum();
				unnormalized[i][j] = new Spectrum();
				nSamples[i][j] = 0.f;
			}
		}
	}
	
	public void addSample(float x, float y, Spectrum s)
	{
		if((int)x>=0 && (int)x<width && (int)y>=0 && (int)y<height)
		{
			unnormalized[(int)x][(int)y].r += s.r;
			unnormalized[(int)x][(int)y].g += s.g;
			unnormalized[(int)x][(int)y].b += s.b;
			nSamples[(int)x][(int)y]++;
		}
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Spectrum[][] makeImage()
	{
		for(int x=0; x<width; x++)
		{
			for(int y=0; y<height; y++)
			{
				image[(int)x][(int)y].r = unnormalized[(int)x][(int)y].r/nSamples[(int)x][(int)y];
				image[(int)x][(int)y].g = unnormalized[(int)x][(int)y].g/nSamples[(int)x][(int)y];
				image[(int)x][(int)y].b = unnormalized[(int)x][(int)y].b/nSamples[(int)x][(int)y];
			}
		}
		return image;
	}

	@Override
	public void addImage(Film f) {
		assert f.getHeight() == this.height;
		assert f.getWidth() == this.width;
		Spectrum[][] img = f.makeImage();
		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				addSample(i,j, img[i][j]);
			}
		}
	}
}
