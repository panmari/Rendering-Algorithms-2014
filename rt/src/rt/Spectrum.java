package rt;

import javax.vecmath.Tuple3f;

/**
 * Stores a spectrum of color values. In this implementation, we work with RGB colors.
 */
public class Spectrum {

	public float r, g, b;
	
	public Spectrum()
	{
		r = 0.f;
		g = 0.f;
		b = 0.f;
	}
	
	public Spectrum(Tuple3f v) {
		this(v.x, v.y, v.z);
	}
	
	public Spectrum(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Spectrum(Spectrum s)
	{
		this.r = s.r;
		this.g = s.g;
		this.b = s.b;
	}
	
	public Spectrum(float f) {
		this.r = f;
		this.g = f;
		this.b = f;
	}

	public void mult(float t)
	{
		r = r*t;
		g = g*t;
		b = b*t;
	}
	
	public void mult(Spectrum s)
	{
		r = r*s.r;
		g = g*s.g;
		b = b*s.b;
	}
	
	public void add(Spectrum s)
	{
		r = r+s.r;
		g = g+s.g;
		b = b+s.b;
	}
	
	public void clamp(float min, float max)
	{
		r = Math.min(max,  r);
		r = Math.max(min, r);
		g = Math.min(max,  g);
		g = Math.max(min, g);
		b = Math.min(max,  b);
		b = Math.max(min, b);
	}
	
	public String toString() {
		return String.format("(%.5f,%.5f,%.5f)",r,g,b);
	}
}
